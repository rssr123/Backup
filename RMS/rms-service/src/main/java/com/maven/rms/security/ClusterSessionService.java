package com.maven.rms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.hazelcast.Hazelcast4IndexedSessionRepository;
import org.springframework.stereotype.Service;

import com.hazelcast.cluster.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.maven.rms.utils.HazelcastSessionUtil;
import com.maven.rms.utils.DebugInformation;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Service
public class ClusterSessionService {

    private final HazelcastInstance hazelcastInstance;
    private final Hazelcast4IndexedSessionRepository sessionRepository;

    @Autowired
    public ClusterSessionService(HazelcastInstance hazelcastInstance,
                                 Hazelcast4IndexedSessionRepository sessionRepository) {
        this.hazelcastInstance = hazelcastInstance;
        this.sessionRepository = sessionRepository;
    }

    
    public String checkCluster() {
        Cluster cluster = hazelcastInstance.getCluster();
        List<String> d = new ArrayList<String>();
        d.add("[ClusterSessionService] Cluster size: " + cluster.getMembers().size());
        cluster.getMembers().forEach(member -> d.add("\t" + member));
        return String.join("\n", d);
    }
    
    /**
     * Return all sessions across the cluster with attributes.
     */
    public List<Map<String, Object>> getAllSessionsWithAttributes() {
        IMap<String, MapSession> sessionMap =
                hazelcastInstance.getMap(Hazelcast4IndexedSessionRepository.DEFAULT_SESSION_MAP_NAME);

        return sessionMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    String sessionId = entry.getKey();
                    MapSession session = entry.getValue();
                    info.put("sessionId", sessionId);
                    if (session != null) {
                        info.put("creationTime", session.getCreationTime());
                        info.put("lastAccessedTime", session.getLastAccessedTime());
                        info.put("maxInactiveInterval", session.getMaxInactiveInterval().getSeconds());
                        info.put("attributes", new HashMap<>(session.getAttributeNames()
                                .stream()
                                .collect(Collectors.toMap(
                                        name -> name,
                                        session::getAttribute
                                ))));
                    }
                    return info;
                })
                .collect(Collectors.toList());
    }
    
    public Optional<Map<String, Object>> findSessionAttributesById(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }

        IMap<String, MapSession> sessionMap =
                hazelcastInstance.getMap(Hazelcast4IndexedSessionRepository.DEFAULT_SESSION_MAP_NAME);

        MapSession session = sessionMap.get(sessionId);
        if (session == null) {
            return Optional.empty();
        }

        Map<String, Object> sessionInfo = new java.util.HashMap<>();
        sessionInfo.put("sessionId", session.getId());
        sessionInfo.put("creationTime", session.getCreationTime());
        sessionInfo.put("lastAccessedTime", session.getLastAccessedTime());
        sessionInfo.put("maxInactiveInterval", session.getMaxInactiveInterval());

        // Collect all attributes
        Map<String, Object> attributes = new java.util.HashMap<>();
        for (String attrName : session.getAttributeNames()) {
            attributes.put(attrName, session.getAttribute(attrName));
        }
        sessionInfo.put("attributes", attributes);

        return Optional.of(sessionInfo);
    }
    
    /**
     * Find session by HttpServletRequest's requestedSessionId.
     */
    public Optional<Session> findSessionByRequest(HttpServletRequest request) {
        String requestedSessionId = request.getRequestedSessionId();
        return findSessionById(requestedSessionId);
    }

    /**
     * Find session by ID (unwrap HazelcastSession -> Session).
     */
    public Optional<Session> findSessionById(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }
        // unwrap HazelcastSession into MapSession safely
        org.springframework.session.Session session = sessionRepository.findById(sessionId);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.of(session);
    }
    
    public void invalidateSession(String sessionId) {
        if (sessionId == null) return;
        try {
            sessionRepository.deleteById(sessionId);
        } catch (Exception e) {
            log.error("[ClusterSessionService] Failed to invalidate session {}", sessionId, e);
        }
    }

    public void updateSession(Session session) {
    	if(session == null) return;
    	
    	HazelcastSessionUtil util = new HazelcastSessionUtil(sessionRepository);
    	try {
    		util.save(session);
    	}catch(Exception e) {
    		log.error("[ClusterSessionService] Failed to save session {}", session.getId(), e);
    	}
    }
    
    public void updateSessionAttributes(String sessionId, Map<String, Object> set, List<String> rem) {
    	IMap<String, MapSession> sessionMap = hazelcastInstance.getMap("spring:session:sessions");
    	
    	sessionMap.executeOnKey(sessionId, entry -> {
            MapSession s = entry.getValue();
            if (s == null) return null;
            if(set!= null) set.forEach(s::setAttribute);
            if(rem != null) rem.forEach(s::removeAttribute);
            entry.setValue(s);            
    		return null;
    	});
    }
    
    public void refreshSessionId(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) return;

        String currentSessionId = httpSession.getId();
        IMap<String, MapSession> sessionMap = hazelcastInstance.getMap("spring:session:sessions");

        boolean shouldRotate = sessionMap.executeOnKey(currentSessionId, entry -> {
            MapSession s = entry.getValue();
            if (s == null) return false;

            Boolean cycle = (Boolean) s.getAttribute("cycle");
            Boolean rotated = (Boolean) s.getAttribute("cycleRotated");
            Long lastRotated = (Long) s.getAttribute("lastRotated");
            long now = System.currentTimeMillis();

            if (Boolean.TRUE.equals(cycle)
                && !Boolean.TRUE.equals(rotated)
                && (lastRotated == null || (now - lastRotated) > 5000)) {
                return true;
            }
            return false;
        });

        if (shouldRotate) {
            request.changeSessionId();
            HttpSession s = request.getSession(false);
            s.removeAttribute("cycle");
            s.setAttribute("cycleRotated", true);
            s.setAttribute("lastRotated", System.currentTimeMillis());
        }
    }
    
    /**
     * Expire all other sessions for a given username.
     */
    public void expireOtherSessions(String username, String currentSessionId) {
        IMap<String, MapSession> sessionMap = hazelcastInstance
                .getMap(Hazelcast4IndexedSessionRepository.DEFAULT_SESSION_MAP_NAME);

        Map<String, Object> results = sessionMap.executeOnEntries(new ExpireSessionsProcessor(username, currentSessionId));
    	
    	List<String> allExpiredSessions = results.values().stream()
    			.filter(obj -> {if(!(obj instanceof List<?>)) return false;
    				List<?> list = (List<?>) obj;
    				return !list.isEmpty() && list.stream().allMatch(element -> element instanceof String);})
    			.map(list -> (List<String>)list)
    	        .filter(list -> list != null && !list.isEmpty())
    	        .flatMap(List::stream)
    	        .map(HazelcastSessionUtil::encodeToCookieSessionId)
                .collect(java.util.stream.Collectors.toList());
    	
        if (!allExpiredSessions.isEmpty()) {
            String nodeIp = hazelcastInstance.getCluster().getLocalMember().getAddress().getHost();
            log.warn("[ClusterSessionService] {} has expired sessions for Node {}", username, nodeIp, 
            		new DebugInformation("Expired Sessions:\n" + String.join("\n", allExpiredSessions)));
        }
    }

    private static class ExpireSessionsProcessor implements EntryProcessor<String, MapSession, Object> {
        private final String username;
        private final String currentSessionId;

        public ExpireSessionsProcessor(String username, String currentSessionId) {
            this.username = username;
            this.currentSessionId = currentSessionId;
        }

        @Override
        public Object process(Map.Entry<String, MapSession> entry) {
            List<String> expiredSessions = new ArrayList<>();
            String sessionId = entry.getKey();
            if (sessionId.equals(currentSessionId)) return null;

            MapSession session = entry.getValue();
            if (session == null) return null;

            //String sessionUsername = (String) session.getAttribute("USER");
            //if (username.equals(sessionUsername)) {
         // Try to extract username from SecurityContext
            Object ctx = session.getAttribute("SPRING_SECURITY_CONTEXT");
            String sessionUsername = null;
            if (ctx instanceof SecurityContext) {
                SecurityContext sc = (SecurityContext) ctx;
                if (sc.getAuthentication() != null &&
                        sc.getAuthentication().getPrincipal() != null) {
                    sessionUsername = sc.getAuthentication().getName();
                }
            }

            if (username.equals(sessionUsername)) {
                entry.setValue(null); // expire
                expiredSessions.add(sessionId);
            }
            
            return expiredSessions;
        }
    }
    
    public String printAllSessions() {
        List<Map<String, Object>> sessions = getAllSessionsWithAttributes();
        if (sessions.isEmpty()) {
        	return "[ClusterSessionService] No active sessions found in cluster.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[ClusterSessionService] Active Sessions: ").append(sessions.size()).append("\n");
        
        for (Map<String, Object> sessionInfo : sessions) {
            sb.append("--------------------------------------------------\n");
            sb.append("Session ID: ").append(HazelcastSessionUtil.encodeToCookieSessionId((String)sessionInfo.get("sessionId"))).append("\n");
            sb.append("Created: ").append(sessionInfo.get("creationTime")).append("\n");
            sb.append("Last Accessed: ").append(sessionInfo.get("lastAccessedTime")).append("\n");
            sb.append("Max Inactive Interval: ").append(sessionInfo.get("maxInactiveInterval")).append("s\n");

            @SuppressWarnings("unchecked")
            Map<String, Object> attributes = (Map<String, Object>) sessionInfo.get("attributes");
            if (attributes.isEmpty()) {
                sb.append("Attributes: (none)\n");
            } else {
                sb.append("Attributes:\n");
                attributes.forEach((key, value) -> {
                    sb.append("  - ").append(key).append(": ").append(value).append("\n");
                });
            }
        }
        sb.append("==================================================");

        return sb.toString();
    }
}