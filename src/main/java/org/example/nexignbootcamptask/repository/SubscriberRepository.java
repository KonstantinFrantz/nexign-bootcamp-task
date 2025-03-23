package org.example.nexignbootcamptask.repository;

import org.example.nexignbootcamptask.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Subscriber entity operations.
 * Uses the subscriber's MSISDN as the primary key.
 */
@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {}
