package com.example.testexample.dao.repository;

import com.example.testexample.dao.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    public Car getByModel(String model);
}
