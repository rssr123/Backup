package com.maven.rms.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.informix.lang.Decimal;
import com.maven.rms.interfaces.IMTTListingService;
import com.maven.rms.models.FMS;
import com.maven.rms.models.MTTDetails;
import com.maven.rms.models.MTTItem;
import com.maven.rms.models.MTTItemDetails;
import com.maven.rms.models.MTTListing;
import com.maven.rms.models.MTTListingDetReq;
import com.maven.rms.models.MTTListingPG;
import com.maven.rms.models.MTTListingRcpt;
import com.maven.rms.models.MTTPGDetails;
import com.maven.rms.repositories.IFmsRepository;
import com.maven.rms.repositories.IMTTListingRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.RICPRepository;

@Service
@Slf4j
public class MTTListingService implements IMTTListingService {

    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final IMTTListingRepository storeProcedureRepository;
    // private final MTTRCPTRepository mttrcptRepository;
    // private final RICPRepository ricpRepository;

    public MTTListingService(IMTTListingRepository storeProcedureRepository, MTTRCPTRepository mttrcptRepository, RICPRepository ricpRepository) {
        this.storeProcedureRepository = storeProcedureRepository;
        // this.mttrcptRepository=mttrcptRepository;
        // this.ricpRepository=ricpRepository;
    }

    @Override
    public List<MTTListing> sp_getMTTListing(MTTListingDetReq req) {
        
             List<MTTListing> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getMTTListing(req);
            result = convertToMTTListingList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<MTTListing> convertToMTTListingList(List<Object[]> objects) {
        List<MTTListing> mttListingList = new ArrayList<>();
        for (Object[] obj : objects) {
            MTTListing mttListing = new MTTListing();
            
            mttListing.setSs_cd((String)obj[0]);
            mttListing.setOrn_no((String)obj[1]);
            mttListing.setOrn_dt((Date)obj[2]);
            mttListing.setTotal_amt((BigDecimal)obj[3]);
            mttListing.setOrder_status((String)obj[4]);
            mttListing.setRcpt_no((String)obj[5]);
            mttListing.setRcpt_dt((Date)obj[6]);
            mttListing.setTotal((Integer)obj[7]);
            mttListing.setMtt_id((Integer)obj[8]);
            mttListing.setRms_type((String)obj[9]);

           mttListingList.add(mttListing);
        }
        return mttListingList;
    }    

    @Override
    public List<MTTDetails> sp_getMTTDetails(MTTListingDetReq req) {
        List<MTTDetails> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getMTTDetails(req);
            result = convertToMTTDetailsList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<MTTDetails> convertToMTTDetailsList(List<Object[]> objects) {
        List<MTTDetails> mttDetailsList = new ArrayList<>();
        for (Object[] obj : objects) {
            MTTDetails mttDetails = new MTTDetails();
            
            mttDetails.setRms_type((String)obj[0]);
            mttDetails.setSs_cd((String)obj[1]);
            mttDetails.setOrn_no((String)obj[2]);
            mttDetails.setOrn_dt((Date)obj[3]);
            mttDetails.setCust_ip((String)obj[4]);
            mttDetails.setCust_nm((String)obj[5]);
            mttDetails.setCust_addr_1((String)obj[6]);
            mttDetails.setCust_addr_2((String)obj[7]);
            mttDetails.setCust_addr_3((String)obj[8]);
            mttDetails.setCust_postcode((String)obj[9]);
            mttDetails.setCust_city((String)obj[10]);
            mttDetails.setCust_state((String)obj[11]);
            mttDetails.setCust_email((String)obj[12]);
            mttDetails.setCust_phone((String)obj[13]);
            mttDetails.setTotal_amt((BigDecimal)obj[14]);
            mttDetails.setOrder_status((String)obj[15]);


           mttDetailsList.add(mttDetails);
        }
        return mttDetailsList;
    }

    @Override
    public List<MTTItem> sp_getmttlistingitem(Integer i_mtt_id) {
        List<MTTItem> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getmttlistingitem(i_mtt_id);
            result = convertToMTTItemList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<MTTItem> convertToMTTItemList(List<Object[]> objects) {
        List<MTTItem> mttDetailsList = new ArrayList<>();
        for (Object[] obj : objects) {
            MTTItem mttItem = new MTTItem();         
            mttItem.setMtt_item_id((Integer)obj[0]);
            mttItem.setMtt_id((Integer)obj[1]);
            mttItem.setFee_detail_pk((Integer)obj[2]);
            mttItem.setLine_no((Integer)obj[3]);
            mttItem.setItem_desc((String)obj[4]);
            mttItem.setQty((Integer)obj[5]);
            mttItem.setUnit_fee((BigDecimal)obj[6]);
            mttItem.setTax_amt((BigDecimal)obj[7]);
            mttItem.setDisc_amt((BigDecimal)obj[8]);
            mttItem.setGross_amt((BigDecimal)obj[9]);
            mttItem.setFee_detail_id((String)obj[10]);
            mttItem.setItem_ref_no((String)obj[11]);
            mttItem.setGrant_cd((String)obj[12]);
            mttItem.setTax_pct((BigDecimal)obj[13]);
            mttItem.setNet_amt((BigDecimal)obj[14]);
            mttItem.setEntity_type((String)obj[15]);
            mttItem.setEntity_no((String)obj[16]);
            mttItem.setEntity_nm((String)obj[17]);
            mttItem.setCp_no((String)obj[18]);
            mttItem.setCp_tier((Integer)obj[19]);
            mttItem.setCp_tier_amt((BigDecimal)obj[20]);
            mttItem.setCp_tier_discpct((BigDecimal)obj[21]);
            mttItem.setDps_id((String)obj[22]);
            mttItem.setDps_task((String)obj[23]);
            mttItem.setPymt_case((String)obj[24]);
            mttItem.setLocation((String)obj[25]);
            mttItem.setLit_item_ref((String)obj[26]);
            mttItem.setTxn_type((String)obj[27]);
            mttItem.setCalendar_yr((Integer)obj[28]);
            mttItem.setModified_by((String)obj[29]);
            mttItem.setCreated_by((String)obj[30]);
            mttItem.setDt_created((Date)obj[31]);
            mttItem.setDt_modified((Date)obj[32]);
            mttItem.setStatus((String)obj[33]);
            mttDetailsList.add(mttItem);
        }
        return mttDetailsList;
    }

    @Override
    public List<MTTListingPG> sp_getmttpg(Integer i_mtt_id) {
        List<MTTListingPG> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getmttpg(i_mtt_id);
            result = convertToMTTIPGList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<MTTListingPG> convertToMTTIPGList(List<Object[]> objects) {
        List<MTTListingPG> mttDetailsList = new ArrayList<>();
        for (Object[] obj : objects) {
            MTTListingPG mttPG = new MTTListingPG();
            
            mttPG.setPymt_submit_dt((Date)obj[0]);
            mttPG.setPg_pymt_id((String)obj[1]);
            mttPG.setPg_pymt_amt((BigDecimal)obj[2]);
            mttPG.setPg_txn_status((String)obj[3]);
            mttPG.setMtt_pg_id((Integer)obj[4]);

           mttDetailsList.add(mttPG);
        }
        return mttDetailsList;
    }

    @Override
    public List<MTTListingRcpt> sp_getmttrcpt(Integer i_mtt_id) {
        List<MTTListingRcpt> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getmttrcpt(i_mtt_id);
            result = convertToMTTRCPTList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<MTTListingRcpt> convertToMTTRCPTList(List<Object[]> objects) {
        List<MTTListingRcpt> mttDetailsList = new ArrayList<>();
        for (Object[] obj : objects) {
            MTTListingRcpt mttRCPT = new MTTListingRcpt();
            
            mttRCPT.setRcpt_no((String)obj[0]);
            mttRCPT.setRcpt_dt((Date)obj[1]);
            mttRCPT.setRcpt_reprint((Integer)obj[2]);
            mttRCPT.setDt_modified((Date)obj[3]);

           mttDetailsList.add(mttRCPT);
        }
        return mttDetailsList;
    }

    @Override
    public List<MTTPGDetails> sp_getmttpg_details(Integer i_mtt_pg_id) {
        List<MTTPGDetails> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getmttpg_details(i_mtt_pg_id);
            result = convertToMTTPGDetailsList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<MTTPGDetails> convertToMTTPGDetailsList(List<Object[]> objects) {
        List<MTTPGDetails> mttDetailsList = new ArrayList<>();
        for (Object[] obj : objects) {
            MTTPGDetails mttPGDetails = new MTTPGDetails();
            
            mttPGDetails.setPymt_submit_dt((Date)obj[0]);
            mttPGDetails.setPg_pymt_method((String)obj[1]);
            mttPGDetails.setPg_pymt_id((String)obj[2]);
            mttPGDetails.setPg_pymt_desc((String)obj[3]);
            mttPGDetails.setPg_pymt_amt((BigDecimal)obj[4]);
            mttPGDetails.setPg_curr_cd((String)obj[5]);
            mttPGDetails.setPg_tax_amt((BigDecimal)obj[6]);
            mttPGDetails.setPg_b4tax_amt((BigDecimal)obj[7]);
            mttPGDetails.setPg_txn_id((String)obj[8]);
            mttPGDetails.setPg_txn_status((String)obj[9]);

           mttDetailsList.add(mttPGDetails);
        }
        return mttDetailsList;
    }

    @Override
    public List<MTTItemDetails> sp_getmttitem_details(Integer i_mtt_item_id) {
        List<MTTItemDetails> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getmttitem_details(i_mtt_item_id);
            result = convertToMTTItemDetailsList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<MTTItemDetails> convertToMTTItemDetailsList(List<Object[]> objects) {
        List<MTTItemDetails> mttDetailsList = new ArrayList<>();
        for (Object[] obj : objects) {
            MTTItemDetails mttItemDetails = new MTTItemDetails();
            
            mttItemDetails.setFee_detail_id((String)obj[0]);
            mttItemDetails.setItem_ref_no((String)obj[1]);
            mttItemDetails.setItem_desc((String)obj[2]);
            mttItemDetails.setQty((Integer)obj[3]);
            mttItemDetails.setUnit_fee((BigDecimal)obj[4]);
            mttItemDetails.setGross_amt((BigDecimal)obj[5]);
            mttItemDetails.setGrant_cd((String)obj[6]);
            mttItemDetails.setDisc_amt((BigDecimal)obj[7]);
            mttItemDetails.setTax_pct((BigDecimal)obj[8]);
            mttItemDetails.setTax_amt((BigDecimal)obj[9]);
            mttItemDetails.setNet_amt((BigDecimal)obj[10]);
            mttItemDetails.setEntity_type((String)obj[11]);
            mttItemDetails.setEntity_no((String)obj[12]);
            mttItemDetails.setEntity_nm((String)obj[13]);
            mttItemDetails.setCp_no((String)obj[14]);
            mttItemDetails.setCp_tier((Integer)obj[15]);
            mttItemDetails.setCp_tier_amt((BigDecimal)obj[16]);
            mttItemDetails.setCp_tier_discpct((BigDecimal)obj[17]);
            mttItemDetails.setDps_id((String)obj[18]);
            mttItemDetails.setDps_task((String)obj[19]);
            mttItemDetails.setPymt_case((String)obj[20]);
            mttItemDetails.setLocation((String)obj[21]);
            mttItemDetails.setLit_item_ref((String)obj[22]);
            mttItemDetails.setTxn_type((String)obj[23]);
            mttItemDetails.setCalendar_yr((Integer)obj[24]);

           mttDetailsList.add(mttItemDetails);
        }
        return mttDetailsList;
    }
}
