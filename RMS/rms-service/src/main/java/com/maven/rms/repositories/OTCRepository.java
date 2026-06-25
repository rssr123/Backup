package com.maven.rms.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.models.OTCCheckInRequest;

@Repository
public class OTCRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    public Integer sp_insotccheckin(OTCCheckInRequest req) {
        Query query = entityManager.createNativeQuery("CALL sp_insotccheckin("
        		+ ":i_session_id,:i_user_id,:i_user_email,:i_counter_id,:i_branch_cd)")
                  .setParameter("i_session_id", req.getSessionId())
                  .setParameter("i_user_id", req.getUser_id())
                  .setParameter("i_user_email", req.getUser_email())
                  .setParameter("i_counter_id", req.getCounter_id())
                  .setParameter("i_branch_cd", req.getBranch_cd());

        Integer result = ((BigInteger)query.getSingleResult()).intValue();
        return result;
   }
    
    public Integer sp_insotccheckout(String counter_id, String user_id, String session_id) {
        Query query = entityManager.createNativeQuery("CALL sp_insotccheckout("
        		+ ":i_counter_id,:i_modified_by,:i_session_id)")
                  .setParameter("i_counter_id", counter_id)
                  .setParameter("i_modified_by", user_id)
                  .setParameter("i_session_id", session_id);

        Integer result = ((BigInteger)query.getSingleResult()).intValue();
        return result;
   }
    
    public Map<String, Map<String, Object>> sp_getotccheckinlisting(String username){
    	 Query query = entityManager.createNativeQuery("CALL sp_getotccheckinlisting(:i_username)")
                 .setParameter("i_username", username);
    	 Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
    	 List<Object[]> objects = query.getResultList();
    	 for (Object[] obj : objects) {
    		 if(map.containsKey((String)(obj[0])))
    			 ((List<String>)((Map<String, Object>)map.get((String)obj[0])).get("counters")).add((String)(obj[1]));
    		 else {
    			 Map<String, Object> data = new HashMap<String, Object>();
    			 List<String> counters = new ArrayList<String>();
    			 counters.add((String)(obj[1]));
    			 data.put("counters", counters);
    			 data.put("branch_cd", (String)(obj[2]));
    			 map.put((String)(obj[0]), data);
    		 }
    	 }
    	 return map;
    }
    
    public Map<String, Object> sp_getotccheckedincounter(String session_id, String user_id) {
   	 	Query query = entityManager.createNativeQuery("CALL sp_getotccheckinstatus(:i_session_id,:i_user_id)")
                .setParameter("i_session_id", session_id)
                .setParameter("i_user_id", user_id);
   	 	Object[] obj = (Object[]) query.getSingleResult();
   	 	Map<String, Object> map = new HashMap<String, Object>();
	   	map.put("counter_id", obj[0] == null ? "" : (String)obj[0]);
	   	map.put("otc_counter_id", obj[1] == null ? 0 : (Integer)obj[1]);
	   	map.put("session_id", obj[2] == null ? 0 : (String)obj[2]);
   	 	
   	 	return map;
    }
    
    public Map<String, String> sp_getotccheckedininfo(String counter_id){
    	Query query = entityManager.createNativeQuery("CALL sp_getotccheckedininfo("
        		+ ":i_counter_id)")
                  .setParameter("i_counter_id", counter_id);
   	 	Map<String, String> map = new HashMap<String, String>();
   	 	Object[] obj = (Object[]) query.getSingleResult();
	   	map.put("check_in", obj[0] == null ? "" : ((Timestamp)obj[0]).toLocalDateTime().toString());
	   	map.put("user_id", obj[1] == null ? "" : (String)obj[1]);
	   	return map;
    }
    
    public Map<String, String> sp_getotccheckedinuserinfo(String user_id){
    	Query query = entityManager.createNativeQuery("CALL sp_getotccheckedinuserinfo("
        		+ ":i_user_id)")
                  .setParameter("i_user_id", user_id);
   	 	Map<String, String> map = new HashMap<String, String>();
	   	List<Object[]> objects = query.getResultList();
	   	map.put("check_in", objects.get(0)[0] == null ? "" : ((Timestamp)objects.get(0)[0]).toLocalDateTime().toString());
	   	map.put("counter_id", objects.get(0)[1] == null ? "" : (String)objects.get(0)[1]);
	   	return map;
    }

    //shceduler
    public List<HashMap<String, String>> sp_getOtcOpenCtr() {
        Query query = entityManager.createNativeQuery("CALL sp_getotcopenctr()");
        List<Object[]> objects = query.getResultList();
    
        List<HashMap<String, String>> resultList = new ArrayList<>();
        for (Object[] result : objects) {
            HashMap<String, String> map = new HashMap<>();
            map.put("otc_counter_id", result[0] == null ? "" : result[0].toString());
            map.put("counter_id", result[1] == null ? "" : result[1].toString());
            map.put("session_id", result[2] == null ? "" : result[2].toString());
            map.put("user_id", result[3] == null ? "" : result[3].toString());
            map.put("check_in", result[4] == null ? "" : result[4].toString());
            map.put("check_out", result[5] == null ? "" : result[5].toString());
            map.put("c_status", result[6] == null ? "" : result[6].toString());
            map.put("created_by", result[7] == null ? "" : result[7].toString());
            map.put("modified_by", result[8] == null ? "" : result[8].toString());
            resultList.add(map);
        }
        return resultList;
    }

    ////shceduler otc checkout
    public List<HashMap<String, String>> sp_getotccheckout() {
        Query query = entityManager.createNativeQuery("CALL sp_getotccheckout()");
        List<Object[]> objects = query.getResultList();
    
        List<HashMap<String, String>> resultList = new ArrayList<>();
        for (Object[] result : objects) {
            HashMap<String, String> map = new HashMap<>();
            map.put("branch_cd", result[0] == null ? "" : result[0].toString());
            map.put("check_in", result[1] == null ? "" : result[1].toString());
            map.put("bal_status", result[2] == null ? "" : result[2].toString());
            map.put("bal_type", result[3] == null ? "" : result[3].toString());
            map.put("modified_by", result[4] == null ? "" : result[4].toString());
            resultList.add(map);
        }
        return resultList;
    }

}
