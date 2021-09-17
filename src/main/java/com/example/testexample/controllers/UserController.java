package com.example.testexample.controllers;

import com.example.testexample.controllers.exceptions.ResourceNotFoundException;
import com.example.testexample.dao.entity.*;
import com.example.testexample.dao.entity.User;
import com.example.testexample.service.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Api("Specifies a set of operations to work with Users")
@RestController
@Validated
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ApartmentService apartmentService;

    @ApiOperation("Returns sorted and paged list of users.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If User exists"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 400, message = "Incorrect values for sorting"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("")
    public List<User> getAll (
            @ApiParam(name = "page", defaultValue = "0", value = "Specifies the 'page' which contains a set of apartments.")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(name = "size", defaultValue = "10", value = "Specifies how many records page will contain")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @ApiParam(name = "sortBy", defaultValue = "id", allowableValues = "id, name, age", value = "Specifies sort field.")
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @ApiParam(name = "sortDirection", defaultValue = "ASC", allowableValues = "ASC, DESC", value = "Specifies sort direction")
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ) {
        Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(order));
        return userService.findAll(pagingSort);
    }

    @ApiOperation("Returns a full info about the specified User. If User not found -> throws NOT FOUND (404)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If User exists"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("/{id}")
    public User getUser (
            @ApiParam(name = "id", value = "Specifies the ID of User that need to get")
            @PathVariable(name = "id") long id) {
                return getUserById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "If User was successfully created"),
            @ApiResponse(code = 400, message = "Incorrect values for User"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser (
        @ApiParam(name = "name", format = "String",
                    value = "Name of the User. Length should be more 2 and less than 31 characters")
        @RequestParam(name = "name", required = true) @Size(min = 3, max = 20) String name,
        @ApiParam(name = "age", format = "int",
                    value = "Age of the user. The value cannot be negative")
        @RequestParam(name = "age", required = true) int age,
        @ApiParam(name = "apartmentId", format = "Long",
                value = "New Apartment of the user. Length should be more 2 and less than 31 characters")
        @RequestParam(name = "apartmentId", required = false) Long apartmentId
    ) {
        User user = new User(name, age);
        user = userService.save(user);
        if (apartmentId != null) {
            user = assignApartmentToUser(apartmentId, user);
        }
        return user;
    }

    @ApiOperation("Deletes the specified user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If User was successfully deleted"),
            @ApiResponse(code = 500, message = "Internal error")})
    @DeleteMapping("/{id}")
    public void deleteUser (
            @ApiParam(name = "id", value = "Specifies the ID of User that need to delete")
            @PathVariable(name = "id") long id
    ) {
        userService.deleteById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "If User was successfully updated"),
            @ApiResponse(code = 400, message = "Incorrect values for User"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateUser (
            @ApiParam(name = "id", value = "Specifies the ID of User that need to update")
            @PathVariable(name = "id") long id,
            @ApiParam(name = "name", format = "String",
                    value = "Name of the User. Length should be more 2 and less than 31 characters")
            @RequestParam(name = "name", required = false) String name,
            @ApiParam(name = "age", format = "int",
                    value = "Age of the user. The value cannot be negative")
            @RequestParam(name = "age", required = false) int age,
            @ApiParam(name = "apartmentId", format = "Long",
                    value = "New Apartment of the user. Length should be more 2 and less than 31 characters")
            @RequestParam(name = "apartmentId", required = false) Long apartmentId
    ) {
        User user = getUserById(id);
        user.setName(name);
        user.setAge(age);
        user = userService.save(user);
        if (apartmentId != null) {
            user = assignApartmentToUser(apartmentId, user);
        }
        return user;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "If User was successfully patched"),
            @ApiResponse(code = 400, message = "Incorrect values for User"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PostMapping("/{userId}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User assignJobToUser (
            @ApiParam(name = "userId", value = "Specifies the ID of user that need to patch")
            @PathVariable(name = "userId") Long userId,
            @ApiParam(name = "jobId",  value = "New Job of the user.")
            @RequestParam(name = "jobId") Long jobId
    ) {
        User user = getUserById(userId);
        Job job = getJobById(jobId);
        user.getJobs().add(job);
        return userService.save(user);
    }

    private User getUserById (long id) {
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User is not found");
        }
        return userOptional.get();
    }

    private Job getJobById (long id) {
        Optional<Job> jobOptional = jobService.getById(id);
        if (jobOptional.isEmpty()) {
            throw new ResourceNotFoundException("Job is not found");
        }
        return jobOptional.get();
    }

    private User assignApartmentToUser (Long apartmentId, User user) {

        Optional<Apartment> apartment = apartmentService.getById(apartmentId);
        if (apartment.isEmpty()) {
            throw new ResourceNotFoundException("No apartment found");
        }
        user.setApartments(apartment.get());
        return userService.save(user);
    }
}
