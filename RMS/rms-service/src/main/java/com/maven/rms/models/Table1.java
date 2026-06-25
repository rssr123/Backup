package com.maven.rms.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "table1")
public class Table1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int col1;

    private int col2;

    // public int getCol1() {
    //     return col1;
    // }

    // public void setCol1(int col1) {
    //     this.col1 = col1;
    // }

    // public int getCol2() {
    //     return col2;
    // }

    // public void setCol2(int col2) {
    //     this.col2 = col2;
    // }
}