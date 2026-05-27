package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.AssessmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentDetailRepository extends JpaRepository<AssessmentDetail, Long> {

    List<AssessmentDetail> findByAssessmentId(Long assessmentId);
}
