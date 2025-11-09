package com.diakonbank.authservice.dto.request;
import com.diakonbank.authservice.enums.SupportedBank;
import lombok.Data;
@Data
public class SyncBankRequest {
    private SupportedBank bank;
}