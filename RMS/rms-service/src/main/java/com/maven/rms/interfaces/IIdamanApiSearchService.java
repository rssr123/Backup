package com.maven.rms.interfaces;

import java.io.IOException;
import java.util.List;

import com.maven.rms.models.IdamanAPISearch;
import com.maven.rms.models.IdamanAPISearchRequest;

public interface IIdamanApiSearchService {

    // List<IdamanAPISearch> idaman_api_searchDoc(String refNo1);

    List<IdamanAPISearch> idaman_api_searchDoc(IdamanAPISearchRequest idamanAPISearchRequest) throws IOException;
}
