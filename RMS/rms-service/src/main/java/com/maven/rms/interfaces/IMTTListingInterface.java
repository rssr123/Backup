package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.MTTListingDetReq;

public interface IMTTListingInterface {

    List<Object[]> sp_getMTTListing(MTTListingDetReq req);

    List<Object[]> sp_getMTTDetails(MTTListingDetReq req);

    List<Object[]> sp_getmttlistingitem(Integer i_mtt_id);

    List<Object[]> sp_getmttpg(Integer i_mtt_id);

    List<Object[]> sp_getmttrcpt(Integer i_mtt_id);

    List<Object[]> sp_getmttpg_details(Integer i_mtt_pg_id);
    
    List<Object[]> sp_getmttitem_details(Integer i_mtt_item_id);
    
}
