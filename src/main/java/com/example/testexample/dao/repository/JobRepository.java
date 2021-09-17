package com.example.testexample.dao.repository;

import com.example.testexample.dao.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    public Job getByJob(String job);
}
