package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonBilResult {
    private Integer mtt_id;
    private Integer non_bil_id;
    private String non_biling_no;

    public NonBilResult(Integer mtt_id, Integer non_bil_id, String non_biling_no) {
        this.mtt_id = mtt_id;
        this.non_bil_id = non_bil_id;
        this.non_biling_no = non_biling_no;
    }
}
