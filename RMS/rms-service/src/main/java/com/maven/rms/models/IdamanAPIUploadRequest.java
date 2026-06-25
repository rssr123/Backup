package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdamanAPIUploadRequest {

    private String sourceSystem; 
    private String refNo1; 
    private String formCode; 
    private String docDate;
    private String receivedDate; 
    private String totalPage; 
    private String stateCode; 
    private String sourceData; 
    private String dateFiler;
    private String sourceSysTRXNo; 
    private String sourceSysDocRefID; 
    private String RefNo2; 
    private String RefNo3;
    private String RefNo4;                  
    private String RefNo5; 
    private String RefNo6; 
    private String RefNo7; 
    private String RefNo8;               
    private String fileContent; 
    private String fileName;
    // public String getSourceSystem() {
    //     return sourceSystem;
    // }
    // public void setSourceSystem(String sourceSystem) {
    //     this.sourceSystem = sourceSystem;
    // }
    // public String getRefNo1() {
    //     return refNo1;
    // }
    // public void setRefNo1(String refNo1) {
    //     this.refNo1 = refNo1;
    // }
    // public String getFormCode() {
    //     return formCode;
    // }
    // public void setFormCode(String formCode) {
    //     this.formCode = formCode;
    // }
    // public String getDocDate() {
    //     return docDate;
    // }
    // public void setDocDate(String docDate) {
    //     this.docDate = docDate;
    // }
    // public String getReceivedDate() {
    //     return receivedDate;
    // }
    // public void setReceivedDate(String receivedDate) {
    //     this.receivedDate = receivedDate;
    // }
    // public String getTotalPage() {
    //     return totalPage;
    // }
    // public void setTotalPage(String totalPage) {
    //     this.totalPage = totalPage;
    // }
    // public String getStateCode() {
    //     return stateCode;
    // }
    // public void setStateCode(String stateCode) {
    //     this.stateCode = stateCode;
    // }
    // public String getSourceData() {
    //     return sourceData;
    // }
    // public void setSourceData(String sourceData) {
    //     this.sourceData = sourceData;
    // }
    // public String getDateFiler() {
    //     return dateFiler;
    // }
    // public void setDateFiler(String dateFiler) {
    //     this.dateFiler = dateFiler;
    // }
    // public String getSourceSysTRXNo() {
    //     return sourceSysTRXNo;
    // }
    // public void setSourceSysTRXNo(String sourceSysTRXNo) {
    //     this.sourceSysTRXNo = sourceSysTRXNo;
    // }
    // public String getSourceSysDocRefID() {
    //     return sourceSysDocRefID;
    // }
    // public void setSourceSysDocRefID(String sourceSysDocRefID) {
    //     this.sourceSysDocRefID = sourceSysDocRefID;
    // }
    // public String getRefNo2() {
    //     return RefNo2;
    // }
    // public void setRefNo2(String refNo2) {
    //     RefNo2 = refNo2;
    // }
    // public String getRefNo3() {
    //     return RefNo3;
    // }
    // public void setRefNo3(String refNo3) {
    //     RefNo3 = refNo3;
    // }
    // public String getRefNo4() {
    //     return RefNo4;
    // }
    // public void setRefNo4(String refNo4) {
    //     RefNo4 = refNo4;
    // }
    // public String getRefNo5() {
    //     return RefNo5;
    // }
    // public void setRefNo5(String refNo5) {
    //     RefNo5 = refNo5;
    // }
    // public String getRefNo6() {
    //     return RefNo6;
    // }
    // public void setRefNo6(String refNo6) {
    //     RefNo6 = refNo6;
    // }
    // public String getRefNo7() {
    //     return RefNo7;
    // }
    // public void setRefNo7(String refNo7) {
    //     RefNo7 = refNo7;
    // }
    // public String getRefNo8() {
    //     return RefNo8;
    // }
    // public void setRefNo8(String refNo8) {
    //     RefNo8 = refNo8;
    // }
    // public String getFileContent() {
    //     return fileContent;
    // }
    // public void setFileContent(String fileContent) {
    //     this.fileContent = fileContent;
    // }
    // public String getFileName() {
    //     return fileName;
    // }
    // public void setFileName(String fileName) {
    //     this.fileName = fileName;
    // }
    public IdamanAPIUploadRequest(String sourceSystem, String refNo1, String formCode, String docDate,
            String receivedDate, String totalPage, String stateCode, String sourceData, String dateFiler,
            String sourceSysTRXNo, String sourceSysDocRefID, String refNo2, String refNo3, String refNo4, String refNo5,
            String refNo6, String refNo7, String refNo8, String fileContent, String fileName) {
        this.sourceSystem = sourceSystem;
        this.refNo1 = refNo1;
        this.formCode = formCode;
        this.docDate = docDate;
        this.receivedDate = receivedDate;
        this.totalPage = totalPage;
        this.stateCode = stateCode;
        this.sourceData = sourceData;
        this.dateFiler = dateFiler;
        this.sourceSysTRXNo = sourceSysTRXNo;
        this.sourceSysDocRefID = sourceSysDocRefID;
        RefNo2 = refNo2;
        RefNo3 = refNo3;
        RefNo4 = refNo4;
        RefNo5 = refNo5;
        RefNo6 = refNo6;
        RefNo7 = refNo7;
        RefNo8 = refNo8;
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    
    
}
