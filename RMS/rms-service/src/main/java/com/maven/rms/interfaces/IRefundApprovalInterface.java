package com.maven.rms.interfaces;

import java.sql.SQLException;
import java.util.List;

import com.maven.rms.models.RefundApprovalDetReq;


public interface IRefundApprovalInterface {
       List<Object[]> sp_getrefundapproval(RefundApprovalDetReq req);
       List<Object[]> sp_getrttitem(RefundApprovalDetReq req);
       Integer sp_updrttwf_status(RefundApprovalDetReq rttwfrequest);
       Integer sp_updrttwf_returntask(RefundApprovalDetReq rttwfrequest);
       List<Object[]> sp_getrttform(RefundApprovalDetReq req);
       
       // New method to fetch refund document details
       List<Object[]> sp_getrttdoc(RefundApprovalDetReq req) throws SQLException;

       String sp_getRttAppEmail(String app_no); // Method to get email information based on app_no
   }
   