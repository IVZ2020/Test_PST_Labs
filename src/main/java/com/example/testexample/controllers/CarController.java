package com.example.testexample.controllers;

import com.example.testexample.controllers.exceptions.ResourceNotFoundException;
import com.example.testexample.dao.entity.Car;
import com.example.testexample.dao.entity.User;
import com.example.testexample.service.CarService;
import com.example.testexample.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@Api("Specifies a set of operations to work with Cars")
@RestController
@Validated
@RequestMapping(value = "/cars" , produces = MediaType.APPLICATION_JSON_VALUE)
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private UserService userService;

    @ApiOperation("Returns sorted and paged list of cars.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Car exists"),
            @ApiResponse(code = 404, message = "Car not found"),
            @ApiResponse(code = 400, message = "Incorrect values for sorting"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("")
    public List<Car> getAll (
            @ApiParam(name = "page", defaultValue = "0", value = "Specifies the 'page' which contains a set of apartments.")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(name = "size", defaultValue = "10", value = "Specifies how many records page will contain")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @ApiParam(name = "sortBy", defaultValue = "id", allowableValues = "id, model, number", value = "Specifies sort field.")
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @ApiParam(name = "sortDirection", defaultValue = "ASC", allowableValues = "ASC, DESC", value = "Specifies sort direction")
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ) {
        Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(order));
        return carService.findAll(pagingSort);
    }

    @ApiOperation("Returns a full info about the specified Car. If Car not found -> throws NOT FOUND (404)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Car exists"),
            @ApiResponse(code = 404, message = "Car not found"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("/{id}")
    public Car getCar (
            @ApiParam(name = "id", value = "Specifies the ID of Car that need to get")
            @PathVariable(name = "id") long id) {
                return getCarById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "If Car was successfully created"),
            @ApiResponse(code = 400, message = "Incorrect values for Car"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Car createCar (
        @ApiParam(name = "model", format = "String",
                    value = "Model of the Car. Length should be more 2 and less than 21 characters")
        @RequestParam(name = "model", required = true) @Size(min = 3, max = 20) String model,
        @ApiParam(name = "number", format = "String",
                    value = "Number of the car. Length should be more 2 and less than 21 characters")
        @RequestParam(name = "number", required = true) @Size(min = 3, max = 20) String number,
        @ApiParam(name = "userId", value = "Specifies the ID of User the Car belong to")
        @RequestParam(name = "userId", required = true) Long userId
    ) {
        Optional<User> userOptional = userService.getById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with " + userId + " not found");
            }
        Car car = new Car(model, number);
        return assignCarToUser(car, userOptional.get());
    }

    @ApiOperation("Deletes the specified car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Car was successfully deleted"),
            @ApiResponse(code = 500, message = "Internal error")})
    @DeleteMapping("/{id}")
    public void deleteCar (
            @ApiParam(name = "id", value = "Specifies the ID of Car that need to delete")
            @PathVariable(name = "id") long id
    ) {
        carService.deleteById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "If Car was successfully updated"),
            @ApiResponse(code = 400, message = "Incorrect values for Car"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateCar (
            @ApiParam(name = "id", value = "Specifies the ID of Car that need to update")
            @PathVariable(name = "id") long id,
            @ApiParam(name = "model", format = "String",
                    value = "Model of the Car. Length should be more 2 and less than 21 characters")
            @RequestParam(name = "model", required = false) String model,
            @ApiParam(name = "number", format = "String",
                    value = "Number of the car. Length should be more 2 and less than 21 characters")
            @RequestParam(name = "number", required = false) String number
    ) {
        Car car = getCarById(id);
        car.setModel(model);
        car.setNumber(number);
        carService.save(car);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "If Car was successfully patched"),
            @ApiResponse(code = 400, message = "Incorrect values for Car"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void patchCar (
            @ApiParam(name = "id", value = "Specifies the ID of car that need to patch")
            @PathVariable(name = "id") long id,
            @ApiParam(name = "model", format = "String",
                    value = "Model of the Car. Length should be more 2 and less than 21 characters")
            @RequestParam(name = "model", required = false) String model,
            @ApiParam(name = "number", format = "String",
                    value = "Number of the car. Length should be more 2 and less than 21 characters")
            @RequestParam(name = "number", required = false) String number
    ) {
        Car car = getCarById(id);
        if (model != null) {
            car.setModel(model);
        }
        if (number != null) {
            car.setNumber(number);
        }
        carService.save(car);
    }

    private Car getCarById (long id) {
        Optional<Car> carOptional = carService.getById(id);
        if (carOptional.isEmpty()) {
            throw new ResourceNotFoundException("Car is not found");
        }
        return carOptional.get();
    }

    private Car assignCarToUser (Car car, User user) {
        Optional<User> userOptional = userService.getById(user.getId());
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(("User is not found"));
        }
        car.setUser(user);
        return carService.save(car);
    }
}
