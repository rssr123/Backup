package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.INonBillingItemService;
import com.maven.rms.models.NonBillingItem;
import com.maven.rms.models.NonBillingItemRequest;
import com.maven.rms.repositories.NonBillingItemRepository;

@Service
public class NonBillingItemService implements INonBillingItemService {
    
    @Autowired
    private NonBillingItemRepository nonBillingItemRepository;

    @Override
    public List<NonBillingItem> getNonBillingItem(NonBillingItemRequest nonBillingItemRequest) {
        List<Object[]> objects = nonBillingItemRepository.getNonBillingItem(nonBillingItemRequest);
        return convertToNonBillingTypeList(objects);
    }

    private List<NonBillingItem> convertToNonBillingTypeList(List<Object[]> objects) {
        List<NonBillingItem> nonBillingItemList = new ArrayList<>();

        for (Object[] obj : objects) {
            NonBillingItem nonBillingItem = new NonBillingItem();
            nonBillingItem.setNonBilNo((String) obj[0]);
            nonBillingItem.setEntNm((String) obj[1]);
            nonBillingItem.setEntTy((String) obj[2]);
            nonBillingItem.setEntNo((String) obj[3]);
            nonBillingItem.setDtCreated((Date) obj[4]);
            nonBillingItem.setFinal_amt((BigDecimal) obj[5]);
            nonBillingItem.setBil_status((String) obj[6]);
            nonBillingItem.setTotal((Integer) obj[7]);
            nonBillingItemList.add(nonBillingItem);
        }

        return nonBillingItemList;
    }

}
