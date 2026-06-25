package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.DashboardRepositoryInterface;
import com.maven.rms.models.DashboardRequest;

@Repository
public class DashboardRepository implements DashboardRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getdashboardinit() {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getdashboardinit()");
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrcptbyyear() {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrcptbyyear()");
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrcptbymonth(DashboardRequest dashboardReq) {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrcptbymonth(:i_year)")
                   .setParameter("i_year", dashboardReq.getI_year());
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrcptbyday(DashboardRequest dashboardReq) {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrcptbyday(:i_year, :i_month)")
                   .setParameter("i_year", dashboardReq.getI_year())
                   .setParameter("i_month", dashboardReq.getI_month());
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrevenuebyss() {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrevenuebyss()");
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrevenuebypaymentmethod() {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrevenuebypaymentmethod()");
         return query.getResultList();
    }


    @Override
    public List<Object[]> sp_getrevenuebyyear() {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrevenuebyyear()");
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrevenuebymonth(DashboardRequest dashboardReq) {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrevenuebymonth(:i_year)")
                   .setParameter("i_year", dashboardReq.getI_year());
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrevenuebyday(DashboardRequest dashboardReq) {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrevenuebyday(:i_year, :i_month)")
                   .setParameter("i_year", dashboardReq.getI_year())
                   .setParameter("i_month", dashboardReq.getI_month());
         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrrefundstatuscnt() {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_getrrefundstatuscnt()");
         return query.getResultList();
    }
    
}
