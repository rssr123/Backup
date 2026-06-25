// src/environments/perm.ts
export const perm = {
    // Tax Code related permissions
    Tax_Code_Maintenance_View_Page: "TCD001",
    Tax_Code_Maintenance_Add_Item: "TCD002",
    Tax_Code_Maintenance_Edit: "TCD003",

    // Fee Group related permissions
    Fee_Group_Maintenance_View_Page: "FG001",
    Fee_Group_Maintenance_Add_Item: "FG002",
    Fee_Group_Maintenance_Edit: "FG003",

    // MFT related permissions
    Master_Fee_Table_View_MFT: "MFT001",
    Master_Fee_Table_View_MFT_Details: "MFT002",
    Master_Fee_Table_View_Master_Task_List: "MFT003",
    Master_Fee_Table_View_Task_Details: "MFT004",
    Master_Fee_Table_Add_MFT_Requester_Form_R: "MFT005",
    Master_Fee_Table_Approve_Add_MFT_Requester_Form_RHOD: "MFT006",
    Master_Fee_Table_Request_Add_MFT_with_Requester_Form_FA: "MFT007",
    Master_Fee_Table_Approve_Add_MFT_with_Requester_Form_FHOD: "MFT008",
    Master_Fee_Table_Request_Add_MFT_FA: "MFT009",
    Master_Fee_Table_Approve_Add_MFT_FHOD: "MFT010",
    Master_Fee_Table_Edit_MFT_Requester_Form_R: "MFT011",
    Master_Fee_Table_Approve_Edit_MFT_Requester_Form_RHOD: "MFT012",
    Master_Fee_Table_Request_Edit_MFT_with_Requester_Form_FA: "MFT013",
    Master_Fee_Table_Approve_Edit_MFT_with_Requester_Form_FHOD: "MFT014",
    Master_Fee_Table_Request_Edit_MFT_FA: "MFT015",
    Master_Fee_Table_Approve_Edit_MFT_FHOD: "MFT016",
    Master_Fee_Table_Cancel_Task: "MFT017",
    Master_Fee_Table_View_MFT_Info: "MFT018",
    Master_Fee_Table_Add_MFT: "MFT019",
    Master_Fee_Table_Export_MFT_Excel: "MFT020",
    Master_Fee_Table_Edit_MFT: "MFT021",

    // FMS related permissions
    FMS_Ledger_Code_View_FMS_Code_Listing_Page: "FMS001",
    FMS_Ledger_Code_Add_FMS_Code: "FMS002",
    FMS_Ledger_Code_Edit_FMS_Code: "FMS003",
    FMS_Ledger_Code_Activate_FMS_Code: "FMS004",
    FMS_Ledger_Code_View_FMS_Ledger_Code_Details: "FMS005",
    FMS_Ledger_Code_FMS_Code_Upload_Excel: "FMS006",
    FMS_Account_Code_View_Page: "FMS007",
    FMS_Account_Code_Edit: "FMS008",
    View_Financial_Post_Accounting_Schedulers_Page: "FMS009",
    Retrying_Scheduler: "FMS010",

    // Role and Permissions Configuration related permissions
    Roles_and_Permissions_Configuration_View_Roles_and_Permissions_Configuration_Page: "RPC001",
    Roles_and_Permissions_Configuration_Add_User_Role: "RPC002",
    Roles_and_Permissions_Configuration_Edit_User_Role_Name_Permissions: "RPC003",
    Roles_and_Permissions_Configuration_Deactivate_User_Role: "RPC004",

    // Accrual Listing related permissions
    Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page: "AL001",
    Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page: "AL002",

    // Bank & Payment Gateway related permissions
    Bank_and_Payment_Gateway_Files_View_PG_Settlement_Upload_Screen: "PRF001",
    Bank_and_Payment_Gateway_Files_Upload_PG_Settlement_File: "PRF002",
    Bank_and_Payment_Gateway_Files_Download_PG_Settlement_File: "PRF003",
    Bank_and_Payment_Gateway_Files_View_Closed_Cancelled_PG_RMS_Reconciliation_Task: "PRF004",

    Bank_and_Payment_Gateway_Files_View_Bank_Statement_Upload_Screen: "BRF001",
    Bank_and_Payment_Gateway_Files_Upload_Bank_Statement: "BRF002",
    Bank_and_Payment_Gateway_Files_View_Closed_Cancelled_PG_Bank_Reconciliation_Task: "BRF003",

    // Reconciliation related permissions
    BP_036_Reconciliation_Of_Collection_View_Reconciliation_Details_Page_PG: "RC001",
    BP_036_Reconciliation_Of_Collection_View_PG_Transaction_List_PG_RMS: "RC003",
    BP_036_Reconciliation_Of_Collection_View_RMS_Transaction_list: "RC004",
    BP_036_Reconciliation_Of_Collection_Open_Close_Add_Remarks_PG_RMS: "RC005",
    BP_036_Reconciliation_Of_Collection_View_Reconciliation_Details_Page_Bank: "RC006",
    BP_036_Reconciliation_Of_Collection_Download_PG_Settlement_File_PG_Bank: "RC007",
    BP_036_Reconciliation_Of_Collection_View_PG_Transaction_List_PG_Bank: "RC008",
    BP_036_Reconciliation_Of_Collection_Download_Bank_Statement_File: "RC009",
    BP_036_Reconciliation_Of_Collection_View_Bank_Statement_Transaction_List: "RC010",
    BP_036_Reconciliation_Of_Collection_Open_Close_Add_Remarks_PG_Bank: "RC011",

    // Reporting and Analysis related permissions
    Reporting_and_Analysis_View_Payment_Collection_Report_Payment_Mode: "RA001",
    Reporting_and_Analysis_Download_PDF_Payment_Mode: "RA002",
    Reporting_and_Analysis_Download_Excel_Payment_Mode: "RA003",
    Reporting_and_Analysis_Download_CSV_Payment_Mode: "RA004",
    Reporting_and_Analysis_View_Payment_Collection_Report_Source_System: "RA005",
    Reporting_and_Analysis_Download_PDF_Source_System: "RA006",
    Reporting_and_Analysis_Download_Excel_Source_System: "RA007",
    Reporting_and_Analysis_Download_CSV_Source_System: "RA008",
    Reporting_and_Analysis_View_Payment_Collection_Report_Fee_Detail_ID: "RA009",
    Reporting_and_Analysis_Download_PDF_Fee_Detail_ID: "RA010",
    Reporting_and_Analysis_Download_Excel_Fee_Detail_ID: "RA011",
    Reporting_and_Analysis_Download_CSV_Fee_Detail_ID: "RA012",
    Reporting_and_Analysis_Daily_Collection_Listing: "RA013",
    Reporting_and_Analysis_Matched_Transaction_Listing: "RA014",
    Reporting_and_Analysis_Unmatched_Transaction_Listing: "RA015",
    Reporting_and_Analysis_PG_Settlement_Disbursement_Listing: "RA016",
    Reporting_and_Analysis_Unmatched_Aging: "RA017",
    Reporting_and_Analysis_Deferred_Income_Aging: "RA018",
    Reporting_and_Analysis_RIPL_Aging: "RA019",
    Reporting_and_Analysis_RICP_Aging: "RA020",
    Reporting_and_Analysis_Refund_Status_Detailed: "RA029",
    Reporting_and_Analysis_Refund_Summary_Status: "RA030",
    Reporting_and_Analysis_Refund_Aging: "RA031",
    Reporting_and_Analysis_Billing: "RA032",
    Reporting_and_Analysis_Catalogue_Product_Service: "RA033",
    Reporting_and_Analysis_Summary_Billing_by_Class_ID: "RA034",
    Reporting_and_Analysis_Detailed_Billing_by_Class_ID: "RA035",
    Reporting_and_Analysis_Detailed_Billing_by_Billing_Type: "RA036",
    Reporting_and_Analysis_Counter_Collection: "RA021",
    Reporting_and_Analysis_Daily_Balancing: "RA022",
    Reporting_and_Analysis_Master_Balancing: "RA023",
    Reporting_and_Analysis_OTC_Collection: "RA024",
    Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID: "RA025",
    Reporting_and_Analysis_OTC_Receipt_Cancellation: "RA026",
    Reporting_and_Analysis_OTC_Returned_Cheque: "RA027",
    Reporting_and_Analysis_Bank_In_Slip: "RA028",

    // MTT related permissions
    MTT_Listing_View_Listing_Page: "MTT001",
    MTT_Listing_View_Details_Page: "MTT002",

    // User Role related permissions
    User_Role_View_Listing_Page: "UR001", 
    
    // My Task related permissions
    Assigned_Task_Listing: "MT001",
    Created_Task_Listing: "MT002",
    Finance_Admin_Task_Listing: "MT003",
    BYM_Task_Listing: "MT004",
    PG_Task_Listing: "MT005",
    OTC_Staff_Task_Listing: "MT006",
    OTC_Supervisor_Task_Listing: "MT007",
    OTC_Branch_Manager_Task_Listing: "MT008",
    SME_Task_Listing: "MT009",
    Public_Task_Listing: "MT010",
    LGL_Task_Listing: "MT011",

    //Branch Code Maintenance permissions
    OTC_Collection_Receipting_View_Listing_Page: "OTC001",
    OTC_Collection_Receipting_Pay_Now_Button: "OTC002",
    OTC_Collection_Receipting_View_Button: "OTC003",
    OTC_Returned_Cheque_View_Listing_Page: "OTC004",
    OTC_Returned_Cheque_View_Dishonor: "OTC005",
    OTC_Returned_Cheque_View_Details: "OTC006",
    OTC_Check_In: "OTC007",
    OTC_Check_Out: "OTC008",
    
    OTC_Receipt_Cancellation_View_Listing_Page: "OTC010",
    OTC_Receipt_Cancellation_View_Details: "OTC011",
    OTC_Receipt_Cancellation_Cancellation_Approval: "OTC012",
    OTC_Receipt_Cancellation_Update_Task_Status: "OTC013",

    //OTC Reprint Receipt
    OTC_Reprint_Receipt_View_Listing_Page: "OTC014",
    // OTC_Reprint_Receipt_View_Details_Button: "OTC015",
    // OTC_Reprint_Receipt_Justifications: "OTC016",
    // OTC_Reprint_Receipt_Download_button: "OTC017",

    OTC_EMV_Reconciliation_View_Listing_Page: "OTC018",
    OTC_EMV_Reconciliation_View_Details_Page: "OTC019",
    OTC_EMV_Reconciliation_Perform_Reconciliation: "OTC020",

    // Refund Account Code related permissions
    Refund_Account_Code_Maintenance_View_Page: "RAC001",
    Refund_Account_Code_Maintenance_Add_Item: "RAC002",
    Refund_Account_Code_Maintenance_Edit: "RAC003",

    // Branch Code Maintenance related permissions
    Branch_Code_Maintenance_View_Page: "BCD001",
    Branch_Code_Maintenance_Add_Item: "BCD002",
    Branch_Code_Maintenance_Edit: "BCD003",

    //Billing
    Billing_Listing_Details: "BIL001",
    Billing_Registration: "BIL002",
    Billing_Registration_Approval: "BIL003",
    Billing_Cancellation_Listing: "BIL004",
    Billing_Cancellation_Request: "BIL005",
    Billing_Cancellation_Approval: "BIL006",
    Billing_Adjustment_Listing: "BIL007",
    Billing_Adjustment_Request: "BIL008",
    Billing_Adjustment_Approval: "BIL009",

    // Billing related permissions
    Billing_Issuance_By_Source_System_Billing_Registration: "BIL010",
    Billing_Issuance_By_Source_System_Billing_Details: "BIL011",

    //Billing Class Maintenance permissions
    Billing_Class_Maintenance_View_Page: "BC001",
    Billing_Class_Maintenance_Add_Item: "BC002",
    Billing_Class_Maintenance_Edit: "BC003",

    //Billing Type Maintenance permissions
    Billing_Type_Maintenance_View_Page: "BT001",
    Billing_Type_Maintenance_Add_Item: "BT002",
    Billing_Type_Maintenance_Edit: "BT003",

    //OTC Balancing permissions
    OTC_COUNTER_BALANCING_Detail_Page:"OTC021",
    OTC_DAILY_BALANCING_View_Listing:"OTC022",
    OTC_DAILY_BALANCING_View_Detail:"OTC023",
    OTC_MASTER_BALANCING_View_Listing:"OTC024",
    OTC_MASTER_BALANCING_View_Detail:"OTC025",
    OTC_MASTER_BALANCING_Download_BankInSlip:"OTC026",
    OTC_BALANCING_Update_PaymentMode:"OTC027",

    //Refund related permissions
    Refund_Paid_Transaction_View_Listing_Page: "RR001",
    Refund_Chargeback_View_Listing_Page: "RR002",
    Refund_Request: "RR003",
    Refund_Listing: "RR004",

    // Service Provider related permissions
    Service_Provider_View_Listing_Page: "SP001",

    Service_Provider_Maintenance_View_Page: "SPM001",
    Service_Provider_Maintenance_Add_Item: "SPM002",
    Service_Provider_Maintenance_Edit: "SPM003",

    // Court Order related permissions
    Court_Order_View_Listing_Page: "CO001",

    // Non RMS Receipting related permissions
    Non_RMS_Receipting_Reconcile: "NRMS001",
    Non_RMS_Receipting_View: "NRMS002",
    Non_RMS_Receipting_View_Listing_Page: "NRMS003",


    // Credit Control SME Task related permissions
    Credit_Control_SME_Task_Pickup: "CC001",
    Credit_Control_SME_Task_View_Listing: "CC002",
    Credit_Control_SME_Task_View_Details: "CC003",

    Non_Billing_Listing_View: "NB001"
};