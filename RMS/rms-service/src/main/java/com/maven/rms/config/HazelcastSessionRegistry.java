package com.maven.rms.config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HazelcastSessionRegistry implements SessionRegistry {

    private final HazelcastInstance hazelcastInstance;
    private final IMap<String, String> userToSession; // username → sessionId
    private final Map<String, SessionInformation> localSessions = new ConcurrentHashMap<>();

    public HazelcastSessionRegistry(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.userToSession = hazelcastInstance.getMap("user-sessions");
    }

    @Override
    public List<Object> getAllPrincipals() {
        return new ArrayList<>(userToSession.keySet());
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        String sessionId = userToSession.get(principal.toString());
        if (sessionId == null) return Collections.emptyList();
        SessionInformation info = localSessions.get(sessionId);
        if (info == null) return Collections.emptyList();
        if (info.isExpired() && !includeExpiredSessions) return Collections.emptyList();
        return Collections.singletonList(info);
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        return localSessions.get(sessionId);
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        SessionInformation info = localSessions.get(sessionId);
        if (info != null) {
            info.refreshLastRequest();
        }
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        // Kill old session if it exists
        String oldSession = userToSession.put(principal.toString(), sessionId);
        if (oldSession != null && !oldSession.equals(sessionId)) {
            SessionInformation info = localSessions.get(oldSession);
            if (info != null) {
                info.expireNow();
            }
        }
        localSessions.put(sessionId, new SessionInformation(principal, sessionId, new Date()));
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        SessionInformation info = localSessions.remove(sessionId);
        if (info != null) {
            userToSession.remove(info.getPrincipal().toString(), sessionId);
        }
    }
}