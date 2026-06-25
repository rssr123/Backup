package com.maven.rms.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.interfaces.ICommonService;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.Param;
import com.maven.rms.models.PostCode;
import com.maven.rms.models.SourceSystemCode;
import com.maven.rms.models.SourceSystemCodeRequest;
import com.maven.rms.models.WhiteIPReq;
import com.maven.rms.models.WhiteList;
import com.maven.rms.models.OTC.OTCBank;
import com.maven.rms.repositories.ICommonRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.RICPRepository;

@Service
@Slf4j
public class CommonService implements ICommonService {

    @Autowired
    private RMSProperties rmsProperties;

    // private static final Logger logger =
    // LoggerFactory.getLogger(CommonService.class);
    private final ICommonRepository commonRepository;

    // public StoreProcedureService(IStoreProcedureRepository
    // storeProcedureRepository,
    // MTTRCPTRepository mttrcptRepository, RICPRepository ricpRepository) {
    // this.storeProcedureRepository = storeProcedureRepository;
    // this.mttrcptRepository = mttrcptRepository;
    // this.ricpRepository = ricpRepository;

    // }

    public CommonService(ICommonRepository commonRepository,
            MTTRCPTRepository mttrcptRepository, RICPRepository ricpRepository) {
        this.commonRepository = commonRepository;

    }

    @Override
    public ResponseEntity<Map<String, Object>> checkOrigin(String origin) {
        Map<String, Object> response = new HashMap<>();

        try {
            String[] allowedOrigins = rmsProperties.getAllowOrigin();
            List<String> originsList = Arrays.asList(allowedOrigins);
            boolean isAllowed = originsList.contains(origin) ||
                    originsList.contains("*") ||
                    originsList.contains("https://" + origin) ||
                    originsList.contains("http://" + origin);

            response.put("checkedOrigin", origin);
            response.put("isAllowed", isAllowed);
            response.put("allowedOrigins", originsList);
            response.put("timestamp", new Date());
            response.put("status", "success");

            log.info(String.format("Origin check - %s: %s", origin, isAllowed));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking origin: " + e.getMessage(), e);
            response.put("status", "error");
            response.put("error", "Unable to check origin: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // #region param
    @Override
    public List<Param> sp_getparam(Integer page, Integer size, String paramCd, String paramGrpNm) {
        List<Param> result = Collections.emptyList();

        try {

            result = convertToGetParam(commonRepository.sp_getparam(page, size, paramCd, paramGrpNm));
        } catch (NumberFormatException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

        }

        return result;
    }

    private List<Param> convertToGetParam(List<Object[]> objects) {
        List<Param> paramList = new ArrayList<>();

        for (Object[] obj : objects) {
            // Extract values from obj array and cast them to their respective types
            String param_cd = (String) obj[0];
            String nm_en = (String) obj[1];
            String nm_bm = (String) obj[2];
            Integer total = (Integer) obj[3];

            // Create a new param instance using the extracted values
            Param param = new Param();
            param.setParam_cd(param_cd);
            param.setNm_en(nm_en);
            param.setNm_bm(nm_bm);
            param.setTotal(total);

            // Add the param instance to the paramList list
            paramList.add(param);
        }
        return paramList;
    }

    @Override
    public List<SourceSystemCode> sp_getsourcesystem(SourceSystemCodeRequest sourceSystemCodeRequest) {
        List<SourceSystemCode> result = Collections.emptyList();

        try {

            List<Object[]> objects = commonRepository.sp_getsourcesystem(sourceSystemCodeRequest);
            result = convertToGetSourceSystem(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<SourceSystemCode> convertToGetSourceSystem(List<Object[]> objects) {
        List<SourceSystemCode> sourceSystemCodeList = new ArrayList<>();

        for (Object[] obj : objects) {

            // Create a new sourceSystemCode instance using the extracted values
            SourceSystemCode sourceSystemCode = new SourceSystemCode();
            sourceSystemCode.setSs_id((BigInteger) obj[0]);
            sourceSystemCode.setSs_cd((String) obj[1]);
            sourceSystemCode.setSs_nm((String) obj[2]);
            sourceSystemCode.setDt_modified((Date) obj[3]);
            sourceSystemCode.setModified_by((String) obj[4]);
            sourceSystemCode.setStatus((String) obj[5]);
            sourceSystemCode.setStatus_en((String) obj[6]);
            sourceSystemCode.setStatus_bm((String) obj[7]);
            sourceSystemCode.setTotal((Integer) obj[8]);

            sourceSystemCodeList.add(sourceSystemCode);
        }
        return sourceSystemCodeList;
    }

    // #endregion

    @Override
    public List<OTCBank> sp_getallbanks() {
        List<OTCBank> result = Collections.emptyList();

        try {

            List<Object[]> objects = commonRepository.sp_getallbanks();
            result = convertToGetBank(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    @Override
    public List<OTCBank> sp_getallrctype() {
        List<OTCBank> result = Collections.emptyList();

        try {

            List<Object[]> objects = commonRepository.sp_getallrctype();
            result = convertToGetBank(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<OTCBank> convertToGetBank(List<Object[]> objects) {
        List<OTCBank> bankList = new ArrayList<>();

        for (Object[] obj : objects) {

            // Create a new sourceSystemCode instance using the extracted values
            OTCBank bank = new OTCBank();
            bank.setParam_cd((String) obj[0]);
            bank.setNm_en((String) obj[1]);
            bank.setNm_bm((String) obj[2]);
            bank.setParam_grp_nm((String) obj[3]);
            bank.setTotal((Integer) obj[4]);

            bankList.add(bank);
        }
        return bankList;
    }

    @Override
    public List<OTCBank> sp_getallbillingstatus() {
        List<OTCBank> result = Collections.emptyList();

        try {

            List<Object[]> objects = commonRepository.sp_getallbillingstatus();
            result = convertToGetBank(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    @Override
    public List<OTCBank> sp_getallbillingmethod() {
        List<OTCBank> result = Collections.emptyList();

        try {

            List<Object[]> objects = commonRepository.sp_getallbillingmethod();
            result = convertToGetBank(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    @Override
    public List<PostCode> sp_getpostcode() {
        List<PostCode> result = Collections.emptyList();

        try {

            result = convertToGetPostCode(commonRepository.sp_getpostcode());
        } catch (NumberFormatException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

        }

        return result;
    }

    private List<PostCode> convertToGetPostCode(List<Object[]> objects) {

        List<PostCode> postCodeList = new ArrayList<>();
        for (Object[] obj : objects) {

            PostCode postCode = new PostCode();
            postCode.setPostcode((String) obj[0]);
            postCode.setLocation((String) obj[1]);
            postCode.setState((String) obj[2]);
            postCode.setCity((String) obj[3]);
            postCode.setTotal((Integer) obj[4]);

            // Add the param instance to the paramList list
            postCodeList.add(postCode);
        }
        return postCodeList;
    }

    @Override
    public List<WhiteList> sp_getwhitelistip() {
        List<WhiteList> result = Collections.emptyList();

        try {

            result = convertToGetWhiteListIP(commonRepository.sp_getwhitelistip());
        } catch (NumberFormatException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

        }

        return result;
    }

    private List<WhiteList> convertToGetWhiteListIP(List<Object[]> objects) {

        List<WhiteList> whiteLists = new ArrayList<>();
        for (Object[] obj : objects) {

            WhiteList whiteList = new WhiteList();
            whiteList.setSs_cd((String) obj[0]);
            whiteList.setIp((String) obj[1]);
            whiteList.setRemark((String) obj[2]);

            whiteLists.add(whiteList);
        }
        return whiteLists;
    }

    // @Override
    // public Integer sp_inswhiteip(WhiteIPReq insertRequest) {
    // Integer result = 0;
    // result = commonRepository.sp_inswhiteip(insertRequest);
    // return result;
    // }

    @Override
    public Integer sp_inswhiteip(WhiteIPReq insertRequest) {
        String ipString = insertRequest.getI_ip();

        // Split the IP string by comma and trim whitespace
        String[] ipAddresses = ipString.split(",");

        Integer totalInserted = 0;

        for (String ip : ipAddresses) {
            String trimmedIp = ip.trim();
            if (!trimmedIp.isEmpty()) {
                // Create a new request object for each IP
                WhiteIPReq singleIpRequest = new WhiteIPReq();
                singleIpRequest.setI_ss_cd(insertRequest.getI_ss_cd());
                singleIpRequest.setI_ip(trimmedIp);
                singleIpRequest.setI_remark(insertRequest.getI_remark());

                Integer result = commonRepository.sp_inswhiteip(singleIpRequest);
                if (result > 0) {
                    totalInserted += result;
                }
            }
        }

        return totalInserted;
    }


    public Map<String, Integer> sp_inswhiteip_v2(WhiteIPReq insertRequest) {
        String ipString = insertRequest.getI_ip();
        String[] ipAddresses = ipString.split(",");

        Map<String, Integer> resultMap = new LinkedHashMap<>();

        for (String ip : ipAddresses) {
            String trimmedIp = ip.trim();
            if (!trimmedIp.isEmpty()) {
                WhiteIPReq singleIpRequest = new WhiteIPReq();
                singleIpRequest.setI_ss_cd(insertRequest.getI_ss_cd());
                singleIpRequest.setI_ip(trimmedIp);
                singleIpRequest.setI_remark(insertRequest.getI_remark());

                Integer result = commonRepository.sp_inswhiteip(singleIpRequest);
                // store raw result from SP for this IP
                resultMap.put(trimmedIp, result);
            }
        }

        return resultMap;
    }

    @Override
    public Integer sp_updwhiteip(WhiteIPReq insertRequest) {
        String ipString = insertRequest.getI_ip();

        // Split the IP string by comma and trim whitespace
        String[] ipAddresses = ipString.split(",");

        Integer totalInserted = 0;

        for (String ip : ipAddresses) {
            String trimmedIp = ip.trim();
            if (!trimmedIp.isEmpty()) {
                // Create a new request object for each IP
                WhiteIPReq singleIpRequest = new WhiteIPReq();
                singleIpRequest.setI_ip(trimmedIp);
                singleIpRequest.setI_remark(insertRequest.getI_remark());

                Integer result = commonRepository.sp_updwhiteip(singleIpRequest);
                if (result > 0) {
                    totalInserted += result;
                }
            }
        }

        return totalInserted;
    }

    public Map<String, Boolean> sp_updwhiteip_v2(WhiteIPReq insertRequest) {
        String ipString = insertRequest.getI_ip();
        String[] ipAddresses = ipString.split(",");

        Map<String, Boolean> resultMap = new LinkedHashMap<>();

        for (String ip : ipAddresses) {
            String trimmedIp = ip.trim();
            if (!trimmedIp.isEmpty()) {
                WhiteIPReq singleIpRequest = new WhiteIPReq();
                singleIpRequest.setI_ip(trimmedIp);
                singleIpRequest.setI_remark(insertRequest.getI_remark());

                Integer result = commonRepository.sp_updwhiteip(singleIpRequest);

                // success if > 0 records updated
                resultMap.put(trimmedIp, result != null && result > 0);
            }
        }

        return resultMap;
    }


    @Override
    public List<String> sp_getuploadedidaman() {
        List<String> result = Collections.emptyList();

        try {

            result = commonRepository.sp_getuploadedidaman();
        } catch (NumberFormatException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

        }

        return result;
    }

    @Override
    public Integer sp_insextaudit(ExtAudit insertRequest) {
        // version 1 is original working flow
        // version 2 is to include correlation ID in request body

        return insertaudit_flow_v2(insertRequest);
    }

    public Integer insertaudit_flow_v1(ExtAudit insertRequest) {
        Integer result = 0;
        result = commonRepository.sp_insextaudit(insertRequest);
        return result;
    }

    public Integer insertaudit_flow_v2(ExtAudit insertRequest) {
        // Get correlation ID from MDC using the same key
        String correlationId = MDC.get("correlationId");
        String requestBody = insertRequest.getI_request_body();

        // Only prepend correlation ID if both exist
        if (correlationId != null && !correlationId.trim().isEmpty() &&
                requestBody != null && !requestBody.trim().isEmpty()) {
            insertRequest.setI_request_body("CorrelationID: " + correlationId + " | " + requestBody);
        }
        // If no correlation ID or empty request body, keep original unchanged

        Integer result = commonRepository.sp_insextaudit(insertRequest);
        return result;
    }

    @Override
    public Integer sp_cleanextaudit() {
        Integer result = commonRepository.sp_cleanextaudit();
        return result;
    }

}