package com.maven.rms.interfaces;

import java.sql.SQLException;
import java.util.List;

import com.maven.rms.models.RefundApprovalDetReq;
import com.maven.rms.models.RefundApprovalInfo;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RttForm;
import com.maven.rms.models.RttItem;

public interface IRefundApprovalService {
      List<RefundApprovalInfo> sp_getrefundapproval(RefundApprovalDetReq req);

      List<RttItem> sp_getrttitem(RefundApprovalDetReq req);

      Integer sp_updrttwf_status(RefundApprovalDetReq rttwfrequest);

      List<RttForm> sp_getrttform(RefundApprovalDetReq req);

      List <RefundDoc> sp_getrttdoc(RefundApprovalDetReq req) throws SQLException;

      Integer sp_updrttwf_returntask(RefundApprovalDetReq rttwfrequest);

      String sp_getRttAppEmail(String app_no); // Method to get email information based on app_no

}
