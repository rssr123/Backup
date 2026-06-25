package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.informix.lang.Decimal;
import com.maven.rms.models.MTTDetails;
import com.maven.rms.models.MTTItem;
import com.maven.rms.models.MTTItemDetails;
import com.maven.rms.models.MTTListing;
import com.maven.rms.models.MTTListingDetReq;
import com.maven.rms.models.MTTListingPG;
import com.maven.rms.models.MTTListingRcpt;
import com.maven.rms.models.MTTPGDetails;

public interface IMTTListingService {

     List<MTTListing> sp_getMTTListing(MTTListingDetReq req);

    List <MTTDetails> sp_getMTTDetails(MTTListingDetReq req);

    List <MTTItem> sp_getmttlistingitem(Integer i_mtt_id);

    List <MTTListingPG> sp_getmttpg(Integer i_mtt_id);

    List <MTTListingRcpt> sp_getmttrcpt(Integer i_mtt_id);

    List<MTTPGDetails> sp_getmttpg_details(Integer i_mtt_pg_id);

    List<MTTItemDetails> sp_getmttitem_details(Integer i_mtt_item_id);
    
}
