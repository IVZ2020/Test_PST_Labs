package com.example.testexample.service;

import com.example.testexample.dao.entity.Job;
import com.example.testexample.dao.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public List<Job> findAll(Pageable pagingSort) {
        return jobRepository.findAll(pagingSort).getContent();
    }

    @Transactional
    public Optional<Job> getById(long id) {
        return jobRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        jobRepository.deleteById(id);
    }

    @Transactional
    public Job save(Job job) {
        return jobRepository.save(job);
    }
}
