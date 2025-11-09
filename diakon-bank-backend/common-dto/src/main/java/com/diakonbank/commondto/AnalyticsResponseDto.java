package com.diakonbank.commondto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
public class AnalyticsResponseDto {
    private String period;
    private AggregatedData expenses;
    private AggregatedData incomes;
    @Data
    @Builder
    public static class AggregatedData {
        private BigDecimal total;
        private List<CategorySummaryDto> byCategory;
    }
}