package com.application.mancala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mancala.model.Pit;

@Repository
public interface PitRepository extends JpaRepository<Pit, Integer> {

}
