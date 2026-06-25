package com.maven.rms.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.Param;
import com.maven.rms.models.PostCode;
import com.maven.rms.models.SourceSystemCode;
import com.maven.rms.models.SourceSystemCodeRequest;
import com.maven.rms.models.WhiteIPReq;
import com.maven.rms.models.WhiteList;
import com.maven.rms.models.OTC.OTCBank;

public interface ICommonService {
    // #region param
    List<Param> sp_getparam(Integer page, Integer size, String paramCd, String paramGrpNm);

    List<SourceSystemCode> sp_getsourcesystem(SourceSystemCodeRequest sourceSystemCodeRequest);

    List<OTCBank> sp_getallbanks();

    List<OTCBank> sp_getallrctype();

    List<OTCBank> sp_getallbillingstatus();

    List<OTCBank> sp_getallbillingmethod();

    List<PostCode> sp_getpostcode();

    List<WhiteList> sp_getwhitelistip();

    Integer sp_inswhiteip(WhiteIPReq insertRequest);

    Integer sp_updwhiteip(WhiteIPReq insertRequest);

    List<String> sp_getuploadedidaman();

    ResponseEntity<Map<String, Object>> checkOrigin(String origin);

    Integer sp_insextaudit(ExtAudit insertRequest);

    Integer sp_cleanextaudit();
}