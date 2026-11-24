package ca.qap.golfclub.dto;

import ca.qap.golfclub.model.MembershipType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MemberRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotNull LocalDate membershipStartDate,
        @NotNull MembershipType membershipType) {
}
