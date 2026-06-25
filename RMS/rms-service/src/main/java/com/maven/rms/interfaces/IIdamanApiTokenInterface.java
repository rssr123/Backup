package com.maven.rms.interfaces;

import com.maven.rms.models.IdamanAPITokenReq;

public interface IIdamanApiTokenInterface {
    Integer updidamantoken(IdamanAPITokenReq bodyReq);

    String getidamantoken();
}
