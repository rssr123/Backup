package com.maven.rms.services.OTC;

import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.OTC.OTCEMVReconciliationRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocUpRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatusRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliation;
import com.maven.rms.models.OTC.OTCEMVReconciliationBoolean;
import com.maven.rms.models.OTC.OTCEMVReconciliationSummary;
import com.maven.rms.models.OTC.OTCEMVReconciliationRC;
import com.maven.rms.models.OTC.OTCEMVReconciliationSettlement;
import com.maven.rms.models.OTC.OTCEMVReconciliationSettlement2;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatus;

public interface IOTCEMVReconciliationServiceInterface {

    List<OTCEMVReconciliation> sp_getotcemvreconciliation(OTCEMVReconciliationRequest request);

    List<OTCEMVReconciliationBoolean> sp_getotcemvreconciliationcheck(OTCEMVReconciliationRequest request);

    List<OTCEMVReconciliationSummary> sp_getotcemvreconciliationsummary(OTCEMVReconciliationRequest request);

    List<OTCEMVReconciliationRC> sp_getotcemvreconciliationrc(OTCEMVReconciliationRequest request);

    List<OTCEMVReconciliationSettlement> sp_getotcemvreconciliationsettlement(OTCEMVReconciliationRequest request);

    String sp_getotcbaldoccontent(OTCEMVReconciliationDocRequest request);

    List<OTCEMVReconciliationStatus> sp_getrcemv(OTCEMVReconciliationStatusRequest request);

    Integer sp_insrcemv(OTCEMVReconciliationStatusRequest request);

    Integer sp_updrcemv(OTCEMVReconciliationStatusRequest request);

    Integer sp_insrcemvdoc(OTCEMVReconciliationDocUpRequest request) throws SerialException, SQLException;

    List<OTCEMVReconciliationSettlement2> sp_getrcemvdoclist(OTCEMVReconciliationDocRequest request);

    String sp_getrcemvdoccontent(OTCEMVReconciliationDocRequest request);

}
