package ca.qap.golfclub;

import ca.qap.golfclub.dto.MemberRequest;
import ca.qap.golfclub.dto.TournamentRequest;
import ca.qap.golfclub.model.MembershipType;
import ca.qap.golfclub.service.GolfClubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GolfClubApiIntegrationTest {
    @Autowired
    private GolfClubService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registersMemberAndSearchesByTournamentStartDate() {
        var member = service.addMember(new MemberRequest("Taylor Example", "1 Main Street",
                "taylor@example.com", "555-0199", LocalDate.of(2026, 7, 21), MembershipType.MONTHLY));
        var tournament = service.addTournament(new TournamentRequest(LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 16), "Halifax Golf Club", new BigDecimal("50.00"),
                new BigDecimal("500.00")));

        service.registerMember(tournament.id(), member.id());

        assertThat(service.searchMembers(null, null, null, LocalDate.of(2026, 8, 15)))
                .anyMatch(result -> result.id().equals(member.id()));
        assertThat(service.searchMembers(null, MembershipType.MONTHLY, null, null))
                .anyMatch(result -> result.id().equals(member.id()));
        assertThat(service.searchMembers(null, null, "555-0199", null))
                .anyMatch(result -> result.id().equals(member.id()));
        assertThat(service.getTournament(tournament.id()).participatingMemberIds()).contains(member.id());
        assertThat(service.searchTournaments(null, "halifax"))
                .anyMatch(result -> result.id().equals(tournament.id()));
    }

    @Test
    void rejectsTournamentEndDateBeforeStartDate() {
        assertThatThrownBy(() -> service.addTournament(new TournamentRequest(
                LocalDate.of(2026, 8, 16), LocalDate.of(2026, 8, 15), "Halifax",
                BigDecimal.ZERO, BigDecimal.ZERO)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createsAndSearchesMembersThroughHttpEndpoints() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "HTTP Test Member",
                                  "address": "1 Main Street",
                                  "email": "http-test-member@example.com",
                                  "phoneNumber": "555-0123",
                                  "membershipStartDate": "2026-07-21",
                                  "membershipType": "ANNUAL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());

        mockMvc.perform(get("/api/members").param("name", "http test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("HTTP Test Member"));
    }
}
