package com.maven.rms.interfaces;

import java.util.List;
import com.maven.rms.models.CatalogueItemRequest;

public interface ICatalogueItemRepository {
    List<Object[]> getCatalogueItem(CatalogueItemRequest catalogueItemRequest);
    String sp_getctlrunno();
}
