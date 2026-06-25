package com.maven.rms.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HazelcastConfig {
	@Value("${server.cluster.ips:null}")
	private String ips;
	@Value("${server.cluster.hazelcast.backup.count:1}")
	private Integer bCount;
	
    @Bean
    public Config hazelcastConfiguration() {
        Config config = new Config();
        config.setInstanceName("hazelcast-sessions");

        NetworkConfig netConfig = config.getNetworkConfig();
        netConfig.setPort(5701).setPortAutoIncrement(true);
        JoinConfig join = netConfig.getJoin();
        
        if(!ips.equals("null")) {
        	join.getMulticastConfig().setEnabled(false);
        	join.getTcpIpConfig().setEnabled(true);
        	Arrays.asList(ips.split(",")).forEach(i -> join.getTcpIpConfig().addMember(i));        	
        }
        else
        	join.getMulticastConfig().setEnabled(true);	// Disable multicast if not available in your infra

        MapConfig sessionMap = new MapConfig("spring:session:sessions");
        sessionMap.setBackupCount(bCount);
        
        sessionMap.setTimeToLiveSeconds(1200); //20 minutes max live session
        sessionMap.setMaxIdleSeconds(0);	//20 minutes max idle (+20 mins from getNotification ping refresh)
        sessionMap.setEvictionConfig(
            new EvictionConfig()
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setSize(5000)
                .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
        );
        sessionMap.setInMemoryFormat(InMemoryFormat.OBJECT);
        
        config.setProperty("hazelcast.map.cleanup.delay.seconds", "30");
        config.setProperty("hazelcast.map.expiry.delay.seconds", "10");
        
        config.setProperty("hazelcast.tcp.join.timeout.seconds", "8");
        config.setProperty("hazelcast.wait.seconds.before.join", "3");
        
        config.addMapConfig(sessionMap);
        
        config.setProperty("hazelcast.health.monitoring.level", "SILENT");
        config.setProperty("hazelcast.logging.type", "slf4j");
        config.setProperty("hazelcast.partition.count", "71"); // optional: lowers memory per partition
        config.setProperty("hazelcast.shutdownhook.enabled", "true");
        
        config.setProperty("hazelcast.diagnostics.enabled", "true");
        config.setProperty("hazelcast.diagnostics.metric.level", "info");
        config.setProperty("hazelcast.diagnostics.max.rolled.file.size.mb", "20");
        config.setProperty("hazelcast.diagnostics.max.rolled.file.count", "10");
        config.setProperty("hazelcast.diagnostics.directory", System.getProperty("catalina.base", ".") + "/logs/hazelcast-diagnostics");
        
        return config;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config config) {
        // create instance from the config that already contains serializers
        //return Hazelcast.newHazelcastInstance(config);
    	return Hazelcast.getOrCreateHazelcastInstance(config);
    }
}
