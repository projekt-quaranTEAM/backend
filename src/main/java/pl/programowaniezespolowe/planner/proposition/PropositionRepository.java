package pl.programowaniezespolowe.planner.proposition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programowaniezespolowe.planner.activity.Activity;

@Repository
public interface PropositionRepository extends JpaRepository<Proposition, Integer> {
}
