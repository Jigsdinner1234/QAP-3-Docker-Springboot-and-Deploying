package ca.qap.golfclub.repository;

import ca.qap.golfclub.model.Member;
import ca.qap.golfclub.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    @Query("""
            select distinct m from Member m
            left join m.tournaments t
            where (:name is null or lower(m.name) like lower(concat('%', :name, '%')))
              and (:membershipType is null or m.membershipType = :membershipType)
              and (:phoneNumber is null or m.phoneNumber = :phoneNumber)
              and (:tournamentStartDate is null or t.startDate = :tournamentStartDate)
            """)
    List<Member> search(@Param("name") String name,
                        @Param("membershipType") MembershipType membershipType,
                        @Param("phoneNumber") String phoneNumber,
                        @Param("tournamentStartDate") LocalDate tournamentStartDate);

    @Override
    @Query("select distinct m from Member m left join fetch m.tournaments where m.id = :id")
    Optional<Member> findById(@Param("id") Long id);
}
