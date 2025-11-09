package com.diakonbank.commondto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
@Data
@Builder
public class CategorySummaryDto {
    private String category;
    private BigDecimal amount;
    private BigDecimal percentage;
}