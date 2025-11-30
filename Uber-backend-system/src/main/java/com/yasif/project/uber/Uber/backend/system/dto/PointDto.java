package com.yasif.project.uber.Uber.backend.system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PointDto {

    private Double[] coordinates;
    private String type="Point";

    public PointDto(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}
