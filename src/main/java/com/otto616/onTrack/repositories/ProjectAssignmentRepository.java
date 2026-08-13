package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {
    List<ProjectAssignment> findByWorkerIdOrderByStartDateDesc(Long workerId);
    List<ProjectAssignment> findByMachineryIdOrderByStartDateDesc(Long machineryId);
    List<ProjectAssignment> findByProviderIdOrderByStartDateDesc(Long providerId);
    List<ProjectAssignment> findAllByOrderByStartDateDesc();
}