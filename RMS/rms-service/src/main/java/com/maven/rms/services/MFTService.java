package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IMFTService;
import com.maven.rms.models.FeeDetailItems;
import com.maven.rms.models.FeeDetailItemsRequest;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTTypeSearchRequest;
import com.maven.rms.repositories.IStoreProcedureRepository;
import com.maven.rms.repositories.MFTRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MFTService implements IMFTService {
    
    // private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final MFTRepository mftRepository;

    public MFTService(MFTRepository mftRepository) {
        this.mftRepository = mftRepository;
    }

    @Override
    public List<MFT> sp_getmft(MFTRequest mftRequest) {
    // public List<MFT> sp_getmft(Integer i_page, Integer i_size, Integer i_fee_detail_pk, String i_fee_detail_id,
    //         BigDecimal i_unit_fee_fr, BigDecimal i_unit_fee_to, String i_ss_cd,
    //         String i_tax_cd, Date i_dt_modified_fr, Date i_dt_modified_to, String i_modified_by, String i_status) {

        List<MFT> result = Collections.emptyList();

            List<Object[]> objects = mftRepository.sp_getmft(mftRequest);

            result = convertToGetMFT(objects);

        return result;
    }

    // public MFT sp_getmftByPK(Integer i_fee_detail_pk) {
    public MFT sp_getmftByPK(MFTRequest mftRequest) {
       // try {
            // List<MFT> data = convertToGetMFT(storeProcedureRepository.sp_getMftWFilter(i_fee_detail_pk));
            List<MFT> data = convertToGetMFT2(mftRepository.sp_getMftWFilter(mftRequest));
            return CollectionUtils.size(data) > 0 ? data.get(0) : null;
            //return data.size() > 0 ? data.get(0) : null;
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        // }
        // return null;
    }
    
    public MFT sp_getmftByID(MFTRequest mftRequest) {
        // try {
             // List<MFT> data = convertToGetMFT(storeProcedureRepository.sp_getMftWFilter(i_fee_detail_pk));
             List<MFT> data = convertToGetMFT2(mftRepository.sp_getMftWFilterID(mftRequest));
             return CollectionUtils.size(data) > 0 ? data.get(0) : null;
             //return data.size() > 0 ? data.get(0) : null;
         // } catch (Exception e) {
         //     log.error("Exception in " + this.getClass().toString(), e);
         // }
         // return null;
     }

    public Integer sp_insMFT(MFT mft) {
        Integer result = 0;

            result = mftRepository.sp_insMFT(mft);

        return result;
    }

    public Integer sp_updMFT(MFT mft) {
        Integer result = 0;

            result = mftRepository.sp_updMFT(mft);

        return result;
    }
    
    //Brian: PLEASE SOMEBODY CHECK THE SP FOR THE PROPER RETURN FORMAT FOR ALL THE SP CALLS!! 
    //THE ORIGINAL CONVERT FUNCTION HAS WRONG PARAMETERS!!!
    private List<MFT> convertToGetMFT2(List<Object[]> objects){
        List<MFT> mftList = new ArrayList<>();

        for (Object[] obj : objects) {
            MFT mft = new MFT();
	            
	        mft.setFee_detail_pk((Integer) obj[0]);
	        mft.setFee_detail_id((String) obj[1]);
	        mft.setFee_grp_id((Integer) obj[2]);
	        mft.setFee_detail_nm_e((String) obj[3]);
	        mft.setFee_detail_nm_b((String) obj[4]);
	        mft.setUnit_fee((BigDecimal) obj[5]);
	        mft.setPromo_startdt((Date) obj[6]);
	        mft.setPromo_enddt((Date) obj[7]);
	        mft.setPromo_fee((BigDecimal) obj[8]);
	        mft.setTax_cd_id((Integer) obj[9]);
	        mft.setAllow_otc((Integer) obj[10]);
	        mft.setLl_parent_id((String) obj[11]);
	        mft.setLl_start_day((Integer) obj[12]);           
	        mft.setLl_start_mth((Integer) obj[13]);
	        mft.setLl_end_day((Integer) obj[14]);
	        mft.setLl_end_mth((Integer) obj[15]);
	        mft.setLedger_cd((String) obj[16]);
	        mft.setSs_cd((String) obj[17]);
	        mft.setDt_created((Date) obj[18]);
	        mft.setDt_modified((Date) obj[19]);
	        mft.setCreated_by((String) obj[20]);
	        mft.setModified_by((String) obj[21]);
	        mft.setStatus((String) obj[22]);      
	        mft.setIsPub((Integer) obj[23]);           
	        mftList.add(mft);
        }
        return mftList;
    }

    private List<MFT> convertToGetMFT(List<Object[]> objects) {
        List<MFT> mftList = new ArrayList<>();

        for (Object[] obj : objects) {
            MFT mft = new MFT();

            mft.setFee_detail_pk((Integer) obj[0]);
            mft.setFee_detail_id((String) obj[1]);
            mft.setFee_grp_id((Integer) obj[2]);
            mft.setFee_grp_nm_en((String) obj[3]);
            mft.setFee_grp_nm_bm((String) obj[4]);
            mft.setFee_detail_nm_e((String) obj[5]);
            mft.setFee_detail_nm_b((String) obj[6]);
            mft.setUnit_fee((BigDecimal) obj[7]);
            mft.setPromo_startdt((Date) obj[8]);
            mft.setPromo_enddt((Date) obj[9]);
            mft.setPromo_fee((BigDecimal) obj[10]);
            mft.setTax_cd_id((Integer) obj[11]);
            mft.setTax_cd((String) obj[12]);
            mft.setAllow_otc((Integer) obj[13]);
            mft.setLl_parent_id((String) obj[14]);
            mft.setLl_start_day((Integer) obj[15]);
            mft.setLl_end_day((Integer) obj[16]);
            mft.setLl_start_mth((Integer) obj[17]);
            mft.setLl_end_mth((Integer) obj[18]);
            mft.setLedger_cd((String) obj[19]);
            mft.setSs_cd((String) obj[20]);
            mft.setDt_modified((Date) obj[21]);
            mft.setDt_created((Date) obj[22]);
            mft.setModified_by((String) obj[23]);
            mft.setCreated_by((String) obj[24]);
            mft.setModified_by_nm((String) obj[25]);
            mft.setCreated_by_nm((String) obj[26]);
            mft.setStatus((String) obj[27]);
            mft.setIsPub((Integer) obj[28]);
            mft.setTotal((Integer) obj[29]);

            mftList.add(mft);
        }
        return mftList;
    }

     @Override
    public List<FeeDetailItems> sp_getfeedetailitems(FeeDetailItemsRequest feeDetailItemsReq) {

        List<FeeDetailItems> result = Collections.emptyList();

            List<Object[]> objects = mftRepository.sp_getfeedetailitems(feeDetailItemsReq);

            result = convertToGetFeeDetailItems(objects);


        return result;
    }

    private List<FeeDetailItems> convertToGetFeeDetailItems(List<Object[]> objects) {
        List<FeeDetailItems> feeDetailItemsList = new ArrayList<>();

        for (Object[] obj : objects) {
            FeeDetailItems feeDetailItems = new FeeDetailItems();
            feeDetailItems.setFee_detail_id((String) obj[0]);
            feeDetailItems.setFee_grp_id((Integer) obj[1]);
            feeDetailItems.setFee_detail_nm_en((String) obj[2]);
            feeDetailItems.setFee_detail_nm_bm((String) obj[3]);
            feeDetailItems.setUnit_fee((BigDecimal) obj[4]);
            feeDetailItems.setPromo_startdt((Date) obj[5]);
            feeDetailItems.setPromo_enddt((Date) obj[6]);
            feeDetailItems.setPromo_fee((BigDecimal) obj[7]);
            feeDetailItems.setTax_cd((String) obj[8]);
            feeDetailItems.setAllow_otc((Integer) obj[9]);
            feeDetailItems.setLl_parent_id((String) obj[10]);
            feeDetailItems.setLl_start_day((Integer) obj[11]);
            feeDetailItems.setLl_start_mth((Integer) obj[12]);
            feeDetailItems.setLl_end_day((Integer) obj[13]);
            feeDetailItems.setLl_end_mth((Integer) obj[14]);
            feeDetailItems.setLedger_cd((String) obj[15]);
            feeDetailItems.setSs_cd((String) obj[16]);
            feeDetailItems.setFee_grp_nm_en((String) obj[17]);
            feeDetailItems.setFee_grp_nm_bm((String) obj[18]);
            feeDetailItems.setTax_cd_nm_en((String) obj[19]);
            feeDetailItems.setTax_cd_nm_bm((String) obj[20]);
            feeDetailItems.setTax_pct((BigDecimal) obj[21]);
            feeDetailItems.setStatus((String) obj[22]);

            feeDetailItemsList.add(feeDetailItems);
        }
        return feeDetailItemsList;
    }

    @Override
    public List<MFT> sp_checkmftexist(MFTRequest mftRequest) {
    
        List<MFT> result = Collections.emptyList();

            List<Object[]> objects = mftRepository.sp_checkmftexist(mftRequest);

            result = convertToGetMFT(objects);

        return result;
    }

    //@Override
    public List<MFT> sp_getmft_typesearch(MFTTypeSearchRequest mfttypesearchRequest) {
    // public List<MFT> sp_getmft(Integer i_page, Integer i_size, Integer i_fee_detail_pk, String i_fee_detail_id,
    //         BigDecimal i_unit_fee_fr, BigDecimal i_unit_fee_to, String i_ss_cd,
    //         String i_tax_cd, Date i_dt_modified_fr, Date i_dt_modified_to, String i_modified_by, String i_status) {

        List<MFT> result = Collections.emptyList();

            List<Object[]> objects = mftRepository.sp_getmft_typesearch(mfttypesearchRequest);

            result = convertToGetMFT(objects);

        return result;
    }
}
