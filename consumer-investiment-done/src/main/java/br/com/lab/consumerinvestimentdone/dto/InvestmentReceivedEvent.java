package br.com.lab.consumerinvestimentdone.dto;

import lombok.Data;

@Data
public class InvestmentReceivedEvent {
    private Long investmentId;

    private Long accountId;

    private Double valueOfInvestment;
}
