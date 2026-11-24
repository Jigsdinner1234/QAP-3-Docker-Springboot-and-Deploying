package ca.qap.golfclub.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record TournamentResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        BigDecimal entryFee,
        BigDecimal cashPrizeAmount,
        Set<Long> participatingMemberIds) {
}
