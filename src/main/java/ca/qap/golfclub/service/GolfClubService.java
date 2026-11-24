package ca.qap.golfclub.service;

import ca.qap.golfclub.dto.MemberRequest;
import ca.qap.golfclub.dto.MemberResponse;
import ca.qap.golfclub.dto.TournamentRequest;
import ca.qap.golfclub.dto.TournamentResponse;
import ca.qap.golfclub.model.Member;
import ca.qap.golfclub.model.Tournament;
import ca.qap.golfclub.repository.MemberRepository;
import ca.qap.golfclub.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GolfClubService {
    private final MemberRepository memberRepository;
    private final TournamentRepository tournamentRepository;

    public GolfClubService(MemberRepository memberRepository, TournamentRepository tournamentRepository) {
        this.memberRepository = memberRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Transactional
    public MemberResponse addMember(MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("A member with this email already exists");
        }
        return toResponse(memberRepository.save(new Member(
                request.name(), request.address(), request.email(), request.phoneNumber(),
                request.membershipStartDate(), request.membershipType())));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {
        return memberRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        return toResponse(findMember(id));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> searchMembers(String name, ca.qap.golfclub.model.MembershipType membershipType,
                                               String phoneNumber, LocalDate tournamentStartDate) {
        return memberRepository.search(blankToNull(name), membershipType, blankToNull(phoneNumber),
                tournamentStartDate).stream().map(this::toResponse).toList();
    }

    @Transactional
    public TournamentResponse addTournament(TournamentRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Tournament endDate must be on or after startDate");
        }
        Tournament tournament = new Tournament(request.startDate(), request.endDate(), request.location(),
                request.entryFee(), request.cashPrizeAmount());
        return toResponse(tournamentRepository.save(tournament));
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> getTournaments() {
        return tournamentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TournamentResponse getTournament(Long id) {
        return toResponse(findTournament(id));
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> searchTournaments(LocalDate startDate, String location) {
        return tournamentRepository.search(startDate, blankToNull(location))
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public TournamentResponse registerMember(Long tournamentId, Long memberId) {
        Tournament tournament = findTournament(tournamentId);
        Member member = findMember(memberId);
        tournament.getParticipants().add(member);
        member.getTournaments().add(tournament);
        return toResponse(tournament);
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member " + id + " was not found"));
    }

    private Tournament findTournament(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament " + id + " was not found"));
    }

    private MemberResponse toResponse(Member member) {
        Set<Long> tournamentIds = member.getTournaments().stream()
                .map(Tournament::getId).collect(Collectors.toSet());
        return new MemberResponse(member.getId(), member.getName(), member.getAddress(), member.getEmail(),
                member.getPhoneNumber(), member.getMembershipStartDate(), member.getMembershipType(), tournamentIds);
    }

    private TournamentResponse toResponse(Tournament tournament) {
        Set<Long> memberIds = tournament.getParticipants().stream()
                .map(Member::getId).collect(Collectors.toSet());
        return new TournamentResponse(tournament.getId(), tournament.getStartDate(), tournament.getEndDate(),
                tournament.getLocation(), tournament.getEntryFee(), tournament.getCashPrizeAmount(), memberIds);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
