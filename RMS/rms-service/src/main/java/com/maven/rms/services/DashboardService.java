package com.maven.rms.services;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IDashboardServiceInterface;
import com.maven.rms.models.Dashboard;
import com.maven.rms.models.DashboardInit;
import com.maven.rms.models.DashboardRequest;
import com.maven.rms.repositories.DashboardRepository;


@Service
public class DashboardService implements IDashboardServiceInterface {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public List<DashboardInit> sp_getdashboardinit() {
        List<DashboardInit> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getdashboardinit();
        result = convertDashBoardInit(objects);
        return result;
    }

    @Override
    public List<Dashboard> sp_getrcptbyyear() {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrcptbyyear();
        result = convertRcptYearList(objects);
        return result;
    }

    @Override
    public List<Dashboard> sp_getrcptbymonth(DashboardRequest dashboardReq) {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrcptbymonth(dashboardReq);
        result = convertRcptMonthList(objects);
        return result;
    }

    @Override
    public List<Dashboard> sp_getrcptbyday(DashboardRequest dashboardReq) {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrcptbyday(dashboardReq);
        result = convertRcptDayList(objects);
        return result;
    }

    private List<DashboardInit> convertDashBoardInit(List<Object[]> objects) {
        List<DashboardInit> dashboards = new ArrayList<>();

        for (Object[] obj : objects) {
            DashboardInit dashboard = new DashboardInit();
            dashboard.setRevenue_year((Integer) obj[0]);
            dashboard.setRevenue((BigDecimal) obj[1]);
            dashboard.setSs_cd((String) obj[2]);
            dashboard.setRevenue_by_ss((BigDecimal) obj[3]);
            dashboard.setPayment_method((String) obj[4]);
            dashboard.setRevenue_by_pm((BigDecimal) obj[5]);
            dashboard.setRcpt_year((Integer) obj[6]);
            dashboard.setCount_rcpt_mtt((Integer) obj[7]);
            dashboard.setCount_rcpt_otc((Integer) obj[8]);
            dashboard.setRefund_status((String) obj[9]);
            dashboard.setRefund_count((Integer) obj[10]);

            dashboards.add(dashboard);
        }
        return dashboards;
    }
    
    private List<Dashboard> convertRcptYearList(List<Object[]> objects) {
        List<Dashboard> rcptYearList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard rcptYear = new Dashboard();
            rcptYear.setReceiptYear((Integer) obj[0]);
            rcptYear.setCount_rcpt_mtt((Integer) obj[1]);
            rcptYear.setCount_rcpt_otc((Integer) obj[2]);


            rcptYearList.add(rcptYear);
        }
        return rcptYearList;
    }

    private List<Dashboard> convertRcptMonthList(List<Object[]> objects) {
        List<Dashboard> rcptMonthList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard rcptMonth = new Dashboard();
            rcptMonth.setReceiptYear((Integer) obj[0]);
            rcptMonth.setReceiptMonth((Integer) obj[1]);
            rcptMonth.setCount_rcpt_mtt((Integer) obj[2]);
            rcptMonth.setCount_rcpt_otc((Integer) obj[3]);
            rcptMonthList.add(rcptMonth);
        }
        return rcptMonthList;
    }

    private List<Dashboard> convertRcptDayList(List<Object[]> objects) {
        List<Dashboard> rcptDayList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard rcptDay = new Dashboard();
            rcptDay.setReceiptDate((Integer) obj[0]);
            rcptDay.setCount_rcpt_mtt((Integer) obj[1]);
            rcptDay.setCount_rcpt_otc((Integer) obj[2]);
            rcptDayList.add(rcptDay);
        }
        return rcptDayList;
    }

    @Override
    public List<Dashboard> sp_getrevenuebyss() {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrevenuebyss();
        result = convertRevenueBySs(objects);
        return result;
    }

    @Override
    public List<Dashboard> sp_getrevenuebypaymentmethod() {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrevenuebypaymentmethod();
        result = convertRevenueBySs(objects);
        return result;
    }

    private List<Dashboard> convertRevenueBySs(List<Object[]> objects) {
        List<Dashboard> revenueBySSList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard revenueBySS = new Dashboard();
            revenueBySS.setSs_cd((String) obj[0]);
            revenueBySS.setRevenue((BigDecimal) obj[1]);
            revenueBySSList.add(revenueBySS);
        }
        return revenueBySSList;
    }

    @Override
    public List<Dashboard> sp_getrevenuebyyear() {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrevenuebyyear();
        result = convertRevenueYearList(objects);
        return result;
    }

    @Override
    public List<Dashboard> sp_getrevenuebymonth(DashboardRequest dashboardReq) {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrevenuebymonth(dashboardReq);
        result = convertRevenueMonthList(objects);
        return result;
    }

    @Override
    public List<Dashboard> sp_getrevenuebyday(DashboardRequest dashboardReq) {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrevenuebyday(dashboardReq);
        result = convertRevenueDayList(objects);
        return result;
    }

    
    private List<Dashboard> convertRevenueYearList(List<Object[]> objects) {
        List<Dashboard> revenueYearList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard revenueYear = new Dashboard();
            revenueYear.setReceiptYear((Integer) obj[0]);
            revenueYear.setRevenue((BigDecimal) obj[1]);
            revenueYearList.add(revenueYear);
        }
        return revenueYearList;
    }

    private List<Dashboard> convertRevenueMonthList(List<Object[]> objects) {
        List<Dashboard> revenueMonthList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard revenueMonth = new Dashboard();
            revenueMonth.setReceiptYear((Integer) obj[0]);
            revenueMonth.setReceiptMonth((Integer) obj[1]);
            revenueMonth.setRevenue((BigDecimal) obj[2]);
            revenueMonthList.add(revenueMonth);
        }
        return revenueMonthList;
    }

    private List<Dashboard> convertRevenueDayList(List<Object[]> objects) {
        List<Dashboard> revenueDayList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard revenueDay = new Dashboard();
            revenueDay.setReceiptDate((Integer) obj[0]);
            revenueDay.setRevenue((BigDecimal) obj[1]);
            revenueDayList.add(revenueDay);
        }
        return revenueDayList;
    }

    @Override
    public List<Dashboard> sp_getrrefundstatuscnt() {
        List<Dashboard> result = Collections.emptyList();
        List<Object[]> objects = dashboardRepository.sp_getrrefundstatuscnt();
        result = convertRefundCount(objects);
        return result;
    }

    private List<Dashboard> convertRefundCount(List<Object[]> objects) {
        List<Dashboard> revenueBySSList = new ArrayList<>();

        for (Object[] obj : objects) {
            Dashboard revenueBySS = new Dashboard();
            revenueBySS.setRefund_status((String) obj[0]);
            revenueBySS.setCount_refund((Integer) obj[1]);
            revenueBySSList.add(revenueBySS);
        }
        return revenueBySSList;
    }


    
}
