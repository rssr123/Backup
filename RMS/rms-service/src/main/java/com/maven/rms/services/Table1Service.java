package com.maven.rms.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.ITable1Service;
import com.maven.rms.models.Table1;
import com.maven.rms.repositories.ITable1Repository;

@Service
public class Table1Service implements ITable1Service {
    private final ITable1Repository table1Repository;

    public Table1Service(ITable1Repository table1Repository) {

        this.table1Repository = table1Repository;
    }

    @Override
    public List<Table1> GetTableData() {
        // TODO Auto-generated method stub
        return table1Repository.GetTableData();
    }

    @Override
    public List<Table1> GetTableDataWithFilter(int c1, int c2) {
        // TODO Auto-generated method stub

        List<Table1> tlb = new ArrayList<>();

        tlb = table1Repository.GetTableDataWithFilter(c1,c2);
        
        return tlb;
    }

}