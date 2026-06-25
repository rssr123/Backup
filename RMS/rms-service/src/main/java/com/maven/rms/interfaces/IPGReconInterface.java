package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.PGRecon;
import com.maven.rms.models.PGReconDetailRequest;
import com.maven.rms.models.PGReconExcelFile;
import com.maven.rms.models.PGReconListRequest;
import com.maven.rms.models.PGReconTaskRequest;
import com.maven.rms.models.PGReconUploadRequest;
import com.maven.rms.models.RMSDetailListingRequest;

public interface IPGReconInterface {

    Integer sp_uploadDoc(PGReconUploadRequest pgDocRequest, Blob blob, PGRecon pgRecon);

    public List<Object[]> sp_getPGReconDoc();

    public BigInteger sp_insPGTxn(PGReconExcelFile pgReconExcelFile);

    public BigInteger sp_delPGTxn(BigInteger rcPGTxnId);

    public BigInteger sp_insMTTTxn(BigInteger rcPGTId);

    public BigInteger sp_updMTTTxn();

    public List<Object[]> sp_getPGReconList(PGReconListRequest pgListRequest);

    public Object[] sp_getPGReconDetail(PGReconDetailRequest pgReconDetailRequest);

    public BigInteger sp_updPGReconDetail(PGReconTaskRequest pgReconTaskRequest);

    public List<Object[]> sp_getPGDetailListing(PGDetailListingRequest pgDetailListing);

    public List<Object[]> sp_getRMSDetailListing(RMSDetailListingRequest rmsDetailListingRequest);

    public Blob sp_getrcpgdoc(PGReconListRequest pgReconRequest);
    
    public Integer sp_updPGReconStatus(BigInteger rcPGId, String reconStatus);

    //20250317 - By Geo
    public Integer sp_checkpgtask(PGReconTaskRequest pgReconTaskRequest);
}

