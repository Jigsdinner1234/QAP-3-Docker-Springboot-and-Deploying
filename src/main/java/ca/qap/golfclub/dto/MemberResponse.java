package ca.qap.golfclub.dto;

import ca.qap.golfclub.model.MembershipType;

import java.time.LocalDate;
import java.util.Set;

public record MemberResponse(
        Long id,
        String name,
        String address,
        String email,
        String phoneNumber,
        LocalDate membershipStartDate,
        MembershipType membershipType,
        Set<Long> tournamentIds) {
}
