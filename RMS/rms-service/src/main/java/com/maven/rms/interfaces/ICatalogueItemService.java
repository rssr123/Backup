package com.maven.rms.interfaces;
import java.util.List;
import com.maven.rms.models.CatalogueItem;
import com.maven.rms.models.CatalogueItemRequest;

public interface ICatalogueItemService {
    List<CatalogueItem> getCatalogueItem(CatalogueItemRequest catalogueItemRequest);
    String sp_getctlrunno();
}
