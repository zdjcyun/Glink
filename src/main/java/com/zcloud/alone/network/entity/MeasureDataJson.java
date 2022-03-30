package com.zcloud.alone.network.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeasureDataJson {
    private String x;
    private String y;
    private String z;
}
