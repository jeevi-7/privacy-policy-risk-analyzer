package com.privacy.analyzer.repository;

import com.privacy.analyzer.model.PrivacyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacyPolicyRepository extends JpaRepository<PrivacyPolicy, Long> {

}