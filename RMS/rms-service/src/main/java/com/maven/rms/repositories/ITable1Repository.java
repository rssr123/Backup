package com.maven.rms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.Table1;

@Repository
public interface ITable1Repository extends JpaRepository<Table1, Integer> {

    @Query(value = "CALL f_getListing()", nativeQuery = true)
    List<Table1> GetTableData();

    @Query(value = "CALL sp_getListing(:c1,:c2);", nativeQuery = true)
    List<Table1> GetTableDataWithFilter(@Param("c1") Integer c1, @Param("c2") Integer c2);
    // List<Table1> GetTableDataWithFilter(Integer c1, Integer c2);

}