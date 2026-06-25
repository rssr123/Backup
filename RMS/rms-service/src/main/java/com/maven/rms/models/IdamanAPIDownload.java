package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdamanAPIDownload {
    private String id;
    private String code;
    private String desc;
    private String file_nm;
    private String file_content;
    private DataModel data;

    @Getter
    @Setter
    public class DataModel
    {
        private String FileName;
        private String FileContent;
    }
}
