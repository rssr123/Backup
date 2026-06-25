package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import com.informix.lang.Decimal;
import com.maven.rms.models.FeeDetailItems;
import com.maven.rms.models.FeeDetailItemsRequest;
import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFDoc;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.MasterTaskList;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.Role;

public interface IMFTService {

    List<MFT> sp_getmft(MFTRequest mftRequest);

    List<FeeDetailItems> sp_getfeedetailitems(FeeDetailItemsRequest feeDetailItemsReq);

    List<MFT> sp_checkmftexist(MFTRequest mftRequest);



}
