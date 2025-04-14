package com.mobylab.springbackend.service.dto;

import com.mobylab.springbackend.entity.Bid;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BidDto {
    private Integer amount;
    private LocalDateTime bidDate;

    public BidDto() {
    }
    public BidDto(Bid bid) {
        this.amount = bid.getAmount();
        this.bidDate = bid.getTimestamp();
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public LocalDateTime getBidDate() {
        return bidDate;
    }

    public void setBidDate(LocalDateTime bidDate) {
        this.bidDate = bidDate;
    }
}
