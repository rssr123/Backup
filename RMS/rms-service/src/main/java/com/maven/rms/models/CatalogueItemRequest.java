package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogueItemRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_fee_detail_nm_e; 
    private Integer i_quantity;
}
