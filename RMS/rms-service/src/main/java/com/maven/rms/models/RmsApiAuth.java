package com.maven.rms.models;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rms_api_auth")
public class RmsApiAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_auth_id")
    private Long apiAuthId;

    @Column(name = "nm", length = 100)
    private String nm;

    @Column(name = "pw", length = 100)
    private String pw;

    @Column(name = "status", length = 10)
    private String status;

    // Standard getters and setters

    // public Long getApiAuthId() {
    //     return apiAuthId;
    // }

    // public void setApiAuthId(Long apiAuthId) {
    //     this.apiAuthId = apiAuthId;
    // }

    // public String getNm() {
    //     return nm;
    // }

    // public void setNm(String nm) {
    //     this.nm = nm;
    // }

    // public String getPw() {
    //     return pw;
    // }

    // public void setPw(String pw) {
    //     this.pw = pw;
    // }

    // public String getStatus() {
    //     return status;
    // }

    // public void setStatus(String status) {
    //     this.status = status;
    // }
}
