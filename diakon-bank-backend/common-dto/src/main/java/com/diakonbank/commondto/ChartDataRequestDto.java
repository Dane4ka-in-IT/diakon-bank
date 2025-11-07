package com.diakonbank.commondto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataRequestDto {
    private String token;
    private Optional<LocalDate> fromDate = Optional.empty();
    private Optional<LocalDate> toDate = Optional.empty();
    private Optional<String> period = Optional.empty();
}
