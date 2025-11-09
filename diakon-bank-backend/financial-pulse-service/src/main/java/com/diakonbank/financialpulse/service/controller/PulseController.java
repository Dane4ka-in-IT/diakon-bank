package com.diakonbank.financialpulse.service.controller;
import com.diakonbank.commondto.*;
import com.diakonbank.financialpulse.service.dto.ai.DiakonHelpResponseDto;
import com.diakonbank.financialpulse.service.service.PulseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/api/v1/pulse")
@RequiredArgsConstructor
public class PulseController {
    private final PulseQueryService pulseQueryService;
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDto> getDashboard(@RequestHeader("X-User-Id") Long userId) {
        DashboardResponseDto dashboardData = pulseQueryService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts(@RequestHeader("X-User-Id") Long userId) {
        List<AccountDto> accounts = pulseQueryService.getAccountsByOwnerId(userId);
        return ResponseEntity.ok(accounts);
    }
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(@RequestHeader("X-User-Id") Long userId) {
        List<TransactionDto> transactions = pulseQueryService.getTransactionsByOwnerId(userId);
        return ResponseEntity.ok(transactions);
    }
    @PostMapping("/ai/chat")
    public ResponseEntity<DiakonHelpResponseDto> askDiakonHelp(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> requestBody) {
        String question = requestBody.get("question");
        DiakonHelpResponseDto response = pulseQueryService.askDiakonHelp(userId, question);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/ai/leaks")
    public ResponseEntity<LeaksResponseDto> findLeaks(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "months", defaultValue = "3") int months) {
        LeaksResponseDto leaks = pulseQueryService.analyzeForLeaks(userId, months);
        return ResponseEntity.ok(leaks);
    }
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponseDto> getAnalytics(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "fromDate") Optional<LocalDate> fromDate,
            @RequestParam(name = "toDate") Optional<LocalDate> toDate) {
        AnalyticsResponseDto analyticsData = pulseQueryService.getAnalytics(userId, fromDate, toDate);
        return ResponseEntity.ok(analyticsData);
    }
}