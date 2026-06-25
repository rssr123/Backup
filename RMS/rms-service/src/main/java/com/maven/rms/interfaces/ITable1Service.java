package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.Table1;

public interface ITable1Service {
    List<Table1> GetTableData();
    List<Table1> GetTableDataWithFilter(int c1,int c2);
}