package com.savvy.gradeservice.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGradeRequest {
    @NotNull(message = "score is required")
    private BigDecimal score;
}
