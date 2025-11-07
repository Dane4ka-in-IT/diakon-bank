package com.diakonbank.commondto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponseDto {
    private BigDecimal totalBalance;
    private BigDecimal todayIncome;
    private BigDecimal todayExpenses;
    private List<AccountDto> accounts;
    private List<TransactionDto> lastTransactions;
}
