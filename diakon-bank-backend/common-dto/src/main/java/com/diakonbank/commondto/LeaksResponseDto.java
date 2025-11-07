package com.diakonbank.commondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class LeaksResponseDto {
    @JsonProperty("subscriptions")
    private List<SubscriptionDto> subscriptions;

    @JsonProperty("large_expenses")
    private List<LargeExpenseDto> largeExpenses;
}
