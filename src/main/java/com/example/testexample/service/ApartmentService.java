package com.example.testexample.service;

import java.net.ContentHandler;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.example.testexample.dao.entity.Apartment;
import com.example.testexample.dao.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    @Transactional
    public List<Apartment> findAll(Pageable pagingSort) {
        return apartmentRepository.findAll(pagingSort).getContent();
    }

    @Transactional
    public Optional<Apartment> getById(long id) {
        return apartmentRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        apartmentRepository.deleteById(id);
    }

    @Transactional
    public Apartment save(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }
}
