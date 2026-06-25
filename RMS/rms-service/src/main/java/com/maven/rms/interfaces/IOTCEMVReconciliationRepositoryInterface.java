package com.maven.rms.interfaces;

import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.OTC.OTCEMVReconciliationRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatusRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocUpRequest;

public interface IOTCEMVReconciliationRepositoryInterface {

    List<Object[]> sp_getotcemvreconciliation(OTCEMVReconciliationRequest request);

    List<Object[]> sp_getotcemvreconciliationcheck(OTCEMVReconciliationRequest request);

    List<Object[]> sp_getotcemvreconciliationsummary(OTCEMVReconciliationRequest request);

    List<Object[]> sp_getotcemvreconciliationrc(OTCEMVReconciliationRequest request);

    List<Object[]> sp_getotcemvreconciliationsettlement(OTCEMVReconciliationRequest request);

    Blob sp_getotcbaldoccontent(OTCEMVReconciliationDocRequest request);

    List<Object[]> sp_getrcemv(OTCEMVReconciliationStatusRequest request);

    Integer sp_insrcemv(OTCEMVReconciliationStatusRequest request);

    Integer sp_updrcemv(OTCEMVReconciliationStatusRequest request);

    Integer sp_insrcemvdoc(OTCEMVReconciliationDocUpRequest request, Blob blob);

    List<Object[]> sp_getrcemvdoclist(OTCEMVReconciliationDocRequest request);

    Blob sp_getrcemvdoccontent(OTCEMVReconciliationDocRequest request);

}
