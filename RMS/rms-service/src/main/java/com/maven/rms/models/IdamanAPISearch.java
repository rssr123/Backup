package com.maven.rms.models;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdamanAPISearch {
    private String id;
    private String code;
    private String desc;
    private String fileName;
    private String fileSize;
    private Integer verID;
    private Integer classID;
    private String profileID;
    private String dateCreated;
    private String fname;
    private String fdescription;
    private String fvalue;

    private DataModel data;

    @Getter
    @Setter
    public class DataModel{
        private List<DocList> documentlist;

        // public List<DocList> getDocumentlist() {
        //     return documentlist;
        // }

        // public void setDocumentlist(List<DocList> documentlist) {
        //     this.documentlist = documentlist;
        // }

    }

    @Getter
    @Setter
    public class DocList{
        private String FileName;
        private String FileSize;
        private Integer VerID;
        private Integer ClassID;
        private String ProfileID;
        private String DateCreated;
        private List<DocFields> Fields;

		// public String getFileName() {
        //     return FileName;
        // }
        // public void setFileName(String fileName) {
        //     FileName = fileName;
        // }
        // public String getFileSize() {
        //     return FileSize;
        // }
        // public void setFileSize(String fileSize) {
        //     FileSize = fileSize;
        // }
        // public Integer getVerID() {
        //     return VerID;
        // }
        // public void setVerID(Integer verID) {
        //     VerID = verID;
        // }
        // public Integer getClassID() {
        //     return ClassID;
        // }
        // public void setClassID(Integer classID) {
        //     ClassID = classID;
        // }
        // public String getProfileID() {
        //     return ProfileID;
        // }
        // public void setProfileID(String profileID) {
        //     ProfileID = profileID;
        // }
        // public String getDateCreated() {
        //     return DateCreated;
        // }
        // public void setDateCreated(String dateCreated) {
        //     DateCreated = dateCreated;
        // }        
        // public List<DocFields> getFields() {
		// 	return Fields;
		// }
		// public void setFields(List<DocFields> fields) {
		// 	Fields = fields;
		// }

    }

    @Getter
    @Setter
    public class DocFields{
        private String Name;
        private String Description;
        private String Value;
        
        // public String getName() {
        //     return Name;
        // }
        // public void setName(String name) {
        //     Name = name;
        // }
        // public String getDescription() {
        //     return Description;
        // }
        // public void setDescription(String description) {
        //     Description = description;
        // }
        // public String getValue() {
        //     return Value;
        // }
        // public void setValue(String value) {
        //     Value = value;
        // }
    }

    // public String getId() {
    //     return id;
    // }

    // public void setId(String id) {
    //     this.id = id;
    // }

    // public String getCode() {
    //     return code;
    // }

    // public void setCode(String code) {
    //     this.code = code;
    // }

    // public String getDesc() {
    //     return desc;
    // }

    // public void setDesc(String desc) {
    //     this.desc = desc;
    // }

    // public String getFileName() {
    //     return fileName;
    // }

    // public void setFileName(String fileName) {
    //     this.fileName = fileName;
    // }

    // public String getFileSize() {
    //     return fileSize;
    // }

    // public void setFileSize(String fileSize) {
    //     this.fileSize = fileSize;
    // }

    // public Integer getVerID() {
    //     return verID;
    // }
    // public void setVerID(Integer verID) {
    //     this.verID = verID;
    // }

    // public Integer getClassID() {
    //     return classID;
    // }

    // public void setClassID(Integer classID) {
    //     this.classID = classID;
    // }

    // public String getProfileID() {
    //     return profileID;
    // }

    // public void setProfileID(String profileID) {
    //     this.profileID = profileID;
    // }

    // public String getDateCreated() {
    //     return dateCreated;
    // }

    // public void setDateCreated(String dateCreated) {
    //     this.dateCreated = dateCreated;
    // }

    // public String getFname() {
    //     return fname;
    // }

    // public void setFname(String fname) {
    //     this.fname = fname;
    // }

    // public String getFdescription() {
    //     return fdescription;
    // }

    // public void setFdescription(String fdescription) {
    //     this.fdescription = fdescription;
    // }

    // public String getFvalue() {
    //     return fvalue;
    // }

    // public void setFvalue(String fvalue) {
    //     fvalue = this.fvalue;
    // }

    // public DataModel getData() {
    //     return data;
    // }

    // public void setData(DataModel data) {
    //     this.data = data;
    // }
}
