package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBillingItemService;
import com.maven.rms.models.BillingItem;
import com.maven.rms.models.BillingItemRequest;
import com.maven.rms.repositories.BillingItemRepository;

@Service
public class BillingItemService implements IBillingItemService{
    
    @Autowired
    private BillingItemRepository billingItemRepository;

    @Override
    public List<BillingItem> getBillingItem(BillingItemRequest billingItemRequest) {
        List<Object[]> objects = billingItemRepository.getBillingItem(billingItemRequest);
        return convertToBillingTypeList(objects);
    }

    private List<BillingItem> convertToBillingTypeList(List<Object[]> objects) {
        List<BillingItem> billingItemList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingItem billingItem = new BillingItem();
            billingItem.setBillingNo((String) obj[0]);
            billingItem.setEntNm((String) obj[1]);
            billingItem.setEntTy((String) obj[2]);
            billingItem.setEntNo((String) obj[3]);
            billingItem.setDtCreated((Date) obj[4]);
            billingItem.setFinal_amt((BigDecimal) obj[5]);
            billingItem.setBil_status((String) obj[6]);
            billingItem.setTotal((Integer) obj[7]);
            billingItemList.add(billingItem);
        }

        return billingItemList;
    }

}
