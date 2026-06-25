package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IOTCBankInSlipService;
import com.maven.rms.models.FMSARIModel;
import com.maven.rms.models.OTCBankInSlip;
import com.maven.rms.repositories.OTCBankInSlipRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCBankInSlipService implements IOTCBankInSlipService{
    
    private final OTCBankInSlipRepository otcbisRepo;

    public OTCBankInSlipService(OTCBankInSlipRepository otcBankInSlipRepo){
        this.otcbisRepo = otcBankInSlipRepo;
    }

    @Override
    public List<OTCBankInSlip> sp_getotcbisinfo(String i_branch_cd, Date i_bal_date){
        List<OTCBankInSlip> getotcbisinfo = Collections.emptyList();
        
        List<Object[]> objects = otcbisRepo.sp_getotcbisinfo(i_branch_cd, i_bal_date);

        getotcbisinfo = convertBISInfo(objects);

        return getotcbisinfo;
    }

    @Override
    public List<OTCBankInSlip> sp_getotcbiscash(String i_branch_cd, Date i_bal_date){
        List<OTCBankInSlip> getotcbiscash = Collections.emptyList();
        
        List<Object[]> objects = otcbisRepo.sp_getotcbiscash(i_branch_cd, i_bal_date);

        getotcbiscash = convertBISCash(objects);

        return getotcbiscash;
    }

    @Override
    public List<OTCBankInSlip> sp_getotcbisphy(String i_branch_cd, Date i_bal_date){
        List<OTCBankInSlip> getotcbisphy = Collections.emptyList();
        
        List<Object[]> objects = otcbisRepo.sp_getotcbisphy(i_branch_cd, i_bal_date);

        getotcbisphy = convertBISPhy(objects);

        return getotcbisphy;
    }

    @Override
    public BigInteger sp_insbankinslip(String i_branch_cd, Date i_bal_date, String i_ssm4uuserrefno){
        BigInteger result = null;
        
        result = otcbisRepo.sp_insbankinslip(i_branch_cd, i_bal_date, i_ssm4uuserrefno);

        return result;
    }

    @Override
    public List<FMSARIModel> sp_getotcfmsari(String i_otc_type, Date dt_balancing) {

        List<Object[]> objects = Collections.emptyList();
        List<FMSARIModel> fmsARIList = new ArrayList<>();

        try {
            // Get FMS ARI from repository
            objects = otcbisRepo.sp_getotcfmsari(i_otc_type, dt_balancing);
        } catch (Exception e) {
            log.error("Error while fetching data from sp_getotcfmsari({}, {})", i_otc_type, dt_balancing, e);
            return fmsARIList; // return empty list if fetch fails
        }

        if (objects != null && !objects.isEmpty()) {
            for (Object[] obj : objects) {
                try {
                        FMSARIModel fmsARI = new FMSARIModel();
                        fmsARI.setType((String) obj[0]);
                        fmsARI.setLink_branch((String) obj[1]);
                        fmsARI.setAmt((BigDecimal) obj[2]);
                        fmsARI.setCust((String) obj[3]);
                        fmsARI.setRms_batch_no((String) obj[4]);
                        fmsARI.setDt_sent((Date) obj[5]);
                        fmsARI.setDesc((String) obj[6]);
                        fmsARI.setAttr_ext_sys((String) obj[7]);
                        fmsARI.setCoa1((String) obj[8]);
                        fmsARI.setCoa2((String) obj[9]);
                        fmsARI.setBranch((String) obj[10]);
                        if (obj[11] != null) fmsARI.setQty(((Number) obj[11]).intValue());
                        fmsARI.setSub_acct((String) obj[12]);
                        fmsARI.setTxn_desc((String) obj[13]);
                        fmsARI.setUnit_price((BigDecimal) obj[14]);
                        fmsARI.setRcpt_no((String) obj[15]);
                        fmsARI.setPayee_info((String) obj[16]);
                        fmsARI.setEnt_nm((String) obj[17]);
                        fmsARI.setEnt_no((String) obj[18]);
                        fmsARI.setEnt_ty((String) obj[19]);
                        fmsARI.setItem_amt((BigDecimal) obj[20]);
                        fmsARI.setPymt_mode((String) obj[21]);
                        fmsARI.setItem_tax_amt((BigDecimal) obj[22]);
                        fmsARI.setLineNbr((Integer) obj[23]);
                        fmsARI.setDiscAmt((BigDecimal) obj[24]);
                        fmsARI.setDepositID((String) obj[25]);
                        fmsARI.setDepositTask((String) obj[26]);

                        fmsARIList.add(fmsARI);
                    } catch (Exception e) {
                        log.error("Error while mapping FMSARIModel for object: {}", e);
                    }
            }
        }

        return fmsARIList;
    }

    private List<OTCBankInSlip> convertBISInfo(List<Object[]> objects){

        List<OTCBankInSlip> BISList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCBankInSlip OTCBISListing = new OTCBankInSlip();
            OTCBISListing.setBranch_cd((String) obj[0]);
            OTCBISListing.setDt_bal((Date) obj[1]);
            OTCBISListing.setCompleted_by((String) obj[2]);
            OTCBISListing.setDt_completed((Date) obj[3]);
            OTCBISListing.setTotal((BigDecimal) obj[4]);
            OTCBISListing.setGtotal_cash((BigDecimal) obj[5]);
            OTCBISListing.setNo_che((Integer) obj[6]);
            OTCBISListing.setGtotal_che((BigDecimal) obj[7]);
            OTCBISListing.setNo_bd((Integer) obj[8]);
            OTCBISListing.setGtotal_bd((BigDecimal) obj[9]);
            OTCBISListing.setNo_mo((Integer) obj[10]);
            OTCBISListing.setGtotal_mo((BigDecimal) obj[11]);
            OTCBISListing.setBankInSlipNo((String) obj[12]);
            BISList.add(OTCBISListing);
        }

        return BISList;
    }

    private List<OTCBankInSlip> convertBISCash(List<Object[]> objects){

        List<OTCBankInSlip> BISCList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCBankInSlip OTCBISCListing = new OTCBankInSlip();
            OTCBISCListing.setParam_cd((String) obj[0]);
            OTCBISCListing.setDenomination((String) obj[1]);
            OTCBISCListing.setQuantity((Integer) obj[2]);
            OTCBISCListing.setAmount((BigDecimal) obj[3]);
            OTCBISCListing.setTotal_cash((BigDecimal) obj[4]);
            BISCList.add(OTCBISCListing);
        }

        return BISCList;
    }

        private List<OTCBankInSlip> convertBISPhy(List<Object[]> objects){

        List<OTCBankInSlip> OTCBalList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCBankInSlip OTCBalListing = new OTCBankInSlip();
            OTCBalListing.setTotal_cash_amt((BigDecimal) obj[0]);
            OTCBalListing.setCol_slip_no((String) obj[1]);
            OTCBalListing.setOrn_no((String) obj[2]);
            OTCBalListing.setChe_bank_nm((String) obj[3]);
            OTCBalListing.setChe_payer_nm((String) obj[4]);
            OTCBalListing.setChe_ba_acct_no((String) obj[5]);
            OTCBalListing.setChe_no((String) obj[6]);
            OTCBalListing.setChe_date((Date) obj[7]);
            OTCBalListing.setChe_amt((BigDecimal) obj[8]);
            OTCBalListing.setBd_bank_nm((String) obj[9]);
            OTCBalListing.setBd_no((String) obj[10]);
            OTCBalListing.setBd_date((Date) obj[11]);
            OTCBalListing.setBd_amt((BigDecimal) obj[12]);
            OTCBalListing.setMo_rm_no((String) obj[13]);
            OTCBalListing.setMo_date((Date) obj[14]);
            OTCBalListing.setMo_payer_nm((String) obj[15]);
            OTCBalListing.setMo_id_no((String) obj[16]);
            OTCBalListing.setMo_contact_no((String) obj[17]);
            OTCBalListing.setMo_amt((BigDecimal) obj[18]);
            OTCBalListing.setDetail_type((String) obj[19]);
            OTCBalListing.setId((BigInteger) obj[20]);
            OTCBalListing.setOtc_id((BigInteger) obj[21]);                                
            OTCBalList.add(OTCBalListing);
        }

        return OTCBalList;
    }
}
