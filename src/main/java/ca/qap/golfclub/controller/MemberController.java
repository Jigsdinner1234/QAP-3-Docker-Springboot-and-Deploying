package ca.qap.golfclub.controller;

import ca.qap.golfclub.dto.MemberRequest;
import ca.qap.golfclub.dto.MemberResponse;
import ca.qap.golfclub.model.MembershipType;
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
@RequestMapping("/api/members")
public class MemberController {
    private final GolfClubService service;

    public MemberController(GolfClubService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse add(@Valid @RequestBody MemberRequest request) {
        return service.addMember(request);
    }

    @GetMapping
    public List<MemberResponse> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) MembershipType membershipType,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tournamentStartDate) {
        if (name == null && membershipType == null && phoneNumber == null && tournamentStartDate == null) {
            return service.getMembers();
        }
        return service.searchMembers(name, membershipType, phoneNumber, tournamentStartDate);
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return service.getMember(id);
    }
}
