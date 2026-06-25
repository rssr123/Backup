package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.DashboardRequest;

public interface DashboardRepositoryInterface {
    List<Object[]> sp_getdashboardinit();
    List<Object[]> sp_getrcptbyyear();
    List<Object[]> sp_getrcptbymonth(DashboardRequest dashboardRequest);
    List<Object[]> sp_getrcptbyday(DashboardRequest dashboardRequest);
    List<Object[]> sp_getrevenuebyss();
    List<Object[]> sp_getrevenuebypaymentmethod();
    List<Object[]> sp_getrevenuebyyear();
    List<Object[]> sp_getrevenuebymonth(DashboardRequest dashboardRequest);
    List<Object[]> sp_getrevenuebyday(DashboardRequest dashboardRequest);
    List<Object[]> sp_getrrefundstatuscnt();
}
