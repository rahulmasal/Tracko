package com.tracko.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationBatchRequest {
    @NotEmpty(message = "At least one location point is required")
    @Valid
    private List<LocationPoint> points;
}
