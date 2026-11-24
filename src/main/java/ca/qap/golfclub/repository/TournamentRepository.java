package ca.qap.golfclub.repository;

import ca.qap.golfclub.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Query("""
            select distinct t from Tournament t
            where (:startDate is null or t.startDate = :startDate)
              and (:location is null or lower(t.location) like lower(concat('%', :location, '%')))
            """)
    List<Tournament> search(@Param("startDate") LocalDate startDate,
                            @Param("location") String location);

    @Override
    @Query("select distinct t from Tournament t left join fetch t.participants where t.id = :id")
    Optional<Tournament> findById(@Param("id") Long id);
}
