package com.maven.rms.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.stereotype.Component;

import com.maven.rms.utils.DebugInformation;
import com.maven.rms.utils.HazelcastSessionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionDebugListener implements HttpSessionAttributeListener, HttpSessionListener {
	
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String browserId = HazelcastSessionUtil.encodeToCookieSessionId(event.getSession().getId());
        log.debug("[SessionDebugListener] Session: " + browserId + "   Added: " + event.getName() + " = " + event.getValue());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        String browserId = HazelcastSessionUtil.encodeToCookieSessionId(event.getSession().getId());
    	log.debug("[SessionDebugListener] Session: " + browserId + "   Removed: " + event.getName());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        String browserId = HazelcastSessionUtil.encodeToCookieSessionId(event.getSession().getId());
    	log.debug("[SessionDebugListener] Session: " + browserId + "   Replaced: " + event.getName() + " = " + event.getValue());
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        String browserId = HazelcastSessionUtil.encodeToCookieSessionId(se.getSession().getId());
    	log.debug("[SessionDebugListener] Session created: " + browserId);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    	HttpSession s = se.getSession();
    	List<String> msg = new ArrayList<String>();
    	long idleMillis = System.currentTimeMillis() - s.getLastAccessedTime();
        int maxInactive = s.getMaxInactiveInterval(); // in seconds
        String browserId = HazelcastSessionUtil.encodeToCookieSessionId(s.getId());
    	msg.add("Creation Time: " + s.getCreationTime());
    	msg.add("MaxInactiveInterval (sec): " + maxInactive);
    	msg.add("Idle Time: " + (idleMillis*1000L));
    	try {
    		msg.add("Attributes:");
    		Collections.list(s.getAttributeNames()).forEach(a ->{
    			if(s.getAttribute(a) instanceof String)
    				msg.add("\t" + a + ": " + s.getAttribute(a));
    			else
    				msg.add("\t" + a + ": Java Obj"); 
    		});
    	} catch(IllegalStateException e) {
    		msg.add("<<Session Attribute inaccessible>>");
    	}
        String reason = "unknown";

        // Heuristic checks
        if (idleMillis >= (maxInactive * 1000L)) {
            reason = "timeout (idle " + idleMillis/1000 + "s)";
        } else if (isClusterReplicationCause()) {
            reason = "replication (DeltaManager/cluster sync)";
        } else {
            reason = "explicit invalidate() or app removal";
        }
        
    	log.warn("[SessionDebugListener] Session Destroyed [" + browserId + "] due to " + reason
    			, new DebugInformation(String.join("\n", msg)));
    }
    
    private boolean isClusterReplicationCause() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(e -> e.getClassName().contains("DeltaManager") ||
                               e.getClassName().contains("ClusterSessionListener") || 
                               e.getClassName().contains("Hazelcast"));
    }
}