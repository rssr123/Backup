package com.maven.rms.config;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SessionCarryStore {

    private final IMap<String, SessionCarryData> map;

    public SessionCarryStore(HazelcastInstance hz) {
        // map name is arbitrary; pick something unique
        this.map = hz.getMap("saml-nonces");
    }

    /** Save with TTL (e.g. 5 minutes) */
    public void save(String nonce, SessionCarryData data) {
        // put with TTL (Hazelcast supports put with ttl)
        map.put(nonce, data, 30, TimeUnit.MINUTES);
    }

    /** Remove and return the DTO (atomic remove) */
    public SessionCarryData remove(String nonce) {
        return map.remove(nonce);
    }
}
