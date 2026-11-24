package ca.qap.golfclub.controller;

import ca.qap.golfclub.dto.TournamentRequest;
import ca.qap.golfclub.dto.TournamentResponse;
import ca.qap.golfclub.service.GolfClubService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final GolfClubService service;

    public TournamentController(GolfClubService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentResponse add(@Valid @RequestBody TournamentRequest request) {
        return service.addTournament(request);
    }

    @GetMapping
    public List<TournamentResponse> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) String location) {
        if (startDate == null && location == null) {
            return service.getTournaments();
        }
        return service.searchTournaments(startDate, location);
    }

    @GetMapping("/{id}")
    public TournamentResponse get(@PathVariable Long id) {
        return service.getTournament(id);
    }

    @PostMapping("/{tournamentId}/members/{memberId}")
    public TournamentResponse register(@PathVariable Long tournamentId, @PathVariable Long memberId) {
        return service.registerMember(tournamentId, memberId);
    }
}
