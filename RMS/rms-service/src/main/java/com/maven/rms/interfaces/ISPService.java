package com.maven.rms.interfaces;

import com.maven.rms.models.DailySettlementRequest;

public interface ISPService {

    String sp_insdailysettlement(DailySettlementRequest dailyRequest);
    String sp_checksp(DailySettlementRequest dailyRequest);
    
} 
