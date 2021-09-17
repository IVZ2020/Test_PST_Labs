package com.example.testexample.service;

import com.example.testexample.dao.entity.Car;
import com.example.testexample.dao.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {
    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Transactional
    public List<Car> findAll(Pageable pagingSort) {
        return carRepository.findAll(pagingSort).getContent();
    }

    @Transactional
    public Optional<Car> getById(long id) {
        return carRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        carRepository.deleteById(id);
    }

    @Transactional
    public Car save(Car car) {
        return carRepository.save(car);
    }

    @Transactional
    public Car getCarByModel(String model) {
        return carRepository.getByModel(model);
    }
}
