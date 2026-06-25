package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.ICatalogueItemService;
import com.maven.rms.models.CatalogueItem;
import com.maven.rms.models.CatalogueItemRequest;
import com.maven.rms.repositories.CatalogueItemRepository;

@Service
public class CatalogueItemService implements ICatalogueItemService {

    @Autowired
    private CatalogueItemRepository catalogueItemRepository;

    @Override
    public List<CatalogueItem> getCatalogueItem(CatalogueItemRequest catalogueItemRequest) {
        List<Object[]> objects = catalogueItemRepository.getCatalogueItem(catalogueItemRequest);
        return convertToCatalogueItemList(objects);
    }

    private List<CatalogueItem> convertToCatalogueItemList(List<Object[]> objects) {
        List<CatalogueItem> catalogueItemList = new ArrayList<>();

        for (Object[] obj : objects) {
            CatalogueItem catalogueItem = new CatalogueItem();
            catalogueItem.setFeeDetailNmE((String) obj[0]);
            catalogueItem.setSsCd((String) obj[1]);
            catalogueItem.setUnitFee((BigDecimal) obj[2]);
            catalogueItem.setFeeDetailId((String) obj[3]);
            catalogueItem.setTaxPct((BigDecimal) obj[4]);
            catalogueItem.setTotal((Integer) obj[5]);
            catalogueItemList.add(catalogueItem);
        }

        return catalogueItemList;
    }

    @Override
    public String sp_getctlrunno() {
        String result = "";
        try {

            result = catalogueItemRepository.sp_getctlrunno();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }
}
