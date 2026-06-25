package com.maven.rms.interfaces;
import java.util.List;

import com.maven.rms.models.Dashboard;
import com.maven.rms.models.DashboardInit;
import com.maven.rms.models.DashboardRequest;

public interface IDashboardServiceInterface {
    List<DashboardInit> sp_getdashboardinit();

    List<Dashboard> sp_getrcptbyyear();
    List<Dashboard> sp_getrcptbymonth(DashboardRequest dashboardRequest);
    List<Dashboard> sp_getrcptbyday(DashboardRequest dashboardRequest);

    List<Dashboard> sp_getrevenuebyss();
    List<Dashboard> sp_getrevenuebypaymentmethod();

    List<Dashboard> sp_getrevenuebyyear();
    List<Dashboard> sp_getrevenuebymonth(DashboardRequest dashboardRequest);
    List<Dashboard> sp_getrevenuebyday(DashboardRequest dashboardRequest);

    List<Dashboard> sp_getrrefundstatuscnt();
}
