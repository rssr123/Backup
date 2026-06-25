package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.PGDetailListingResponse;
import com.maven.rms.models.PGReconDetailRequest;
import com.maven.rms.models.PGReconDetailResponse;
import com.maven.rms.models.PGReconListRequest;
import com.maven.rms.models.PGReconListResponse;
import com.maven.rms.models.PGReconTaskRequest;
import com.maven.rms.models.PGReconUploadRequest;
import com.maven.rms.models.RMSDetailListingRequest;
import com.maven.rms.models.RMSDetailListingResponse;

public interface IPGReconService {

    public Integer sp_uploadDoc(PGReconUploadRequest pgDocRequest) throws SerialException, SQLException;

    public List<BigInteger> sp_insPGTxn() throws SQLException;

    public Integer sp_insMTTTxn(List<BigInteger> rcPGTxnIds);

    public List<PGReconListResponse> sp_getPGReconList(PGReconListRequest pgListRequest);

    public PGReconDetailResponse sp_getPGReconDetail(PGReconDetailRequest pgReconDetailRequest);

    public BigInteger sp_updPGReconDetail(PGReconTaskRequest pgReconTaskRequest);

    public List<PGDetailListingResponse> sp_getPGDetailListing(PGDetailListingRequest pgDetailListing);

    public List<RMSDetailListingResponse> sp_getRMSDetailListing(RMSDetailListingRequest rmsDetailListingRequest);

    public String sp_getrcpgdoc(PGReconListRequest pgReconRequest) throws SQLException;

    //20250317 - By Geo
    public Integer sp_checkpgtask(PGReconTaskRequest pgReconTaskRequest);
}
