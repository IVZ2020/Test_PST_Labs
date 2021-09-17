package com.example.testexample.controllers;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.example.testexample.controllers.exceptions.ResourceNotFoundException;
import com.example.testexample.dao.entity.Apartment;
import com.example.testexample.dao.entity.User;
import com.example.testexample.service.ApartmentService;
import com.example.testexample.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api("Specifies a set of operations to work with Apartments")
@RequestMapping(value = "/apartments", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RestController
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private UserService userService;

    @ApiOperation("Returns sorted and paged list of apartments.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Apartment exists"),
            @ApiResponse(code = 404, message = "Apartment not found"),
            @ApiResponse(code = 400, message = "Incorrect values for sorting"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("")
    public List<Apartment> getAll(
            @ApiParam(name = "page", defaultValue = "0", value = "Specifies the 'page' which contains a set of apartments.")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(name = "size", defaultValue = "10", value = "Specifies how many records page will contain.")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @ApiParam(name = "sortBy", defaultValue = "address", allowableValues = "id, address, squareValue", value = "Specifies sort field.")
            @RequestParam(name = "sortBy", defaultValue = "address") String sortBy,
            @ApiParam(name = "sortDirection", defaultValue = "ASC", allowableValues = "ASC, DESC", value = "Specifies sort direction")
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {

        Order order = new Order(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(order));
        return apartmentService.findAll(pagingSort);
    }

    @ApiOperation("Returns a full info about the specified Apartment. If Apartment not found -> throws NOT FOUND (404)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Apartment exists"),
            @ApiResponse(code = 404, message = "Apartment not found"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("/{id}")
    public Apartment getById(
            @ApiParam(name = "id", value = "Specifies the ID of Apartment that need to get")
            @PathVariable(name = "id") Long id) {

        return getApartment(id);
    }

    @ApiOperation("Deletes the specified apartment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Apartment was successfully deleted"),
            @ApiResponse(code = 500, message = "Internal error")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void delete(
            @ApiParam(name = "id", value = "Specifies the ID of Apartment that need to delete")
            @PathVariable(name = "id") Long id) {

        apartmentService.deleteById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "If Apartment was successfully created"),
            @ApiResponse(code = 400, message = "Incorrect values for Apartment"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(
            @ApiParam(name = "address", format = "String",
                    value = "Address of the apartment. Length should be more 10 and less than 100 characters")
            @RequestParam(name = "address", required = true) @Size(min = 10, max = 100) String address,
            @ApiParam(name = "square", format = "double",
                    value = "Square of the apartment. The value cannot be negative")
            @RequestParam(name = "square", required = true) @Min(0) double square,
            @ApiParam(name = "userId", value = "Specifies the ID of User the Car belong to")
            @RequestParam(name = "userId") Long userId)
    {
        Apartment apartment = new Apartment();
        apartment.setAddress(address);
        apartment.setSquareValue(square);
        assignApartmentsToUser(apartment, userService.getById(userId).get());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "If Apartment was successfully updated"),
            @ApiResponse(code = 400, message = "Incorrect values for Apartment"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "address", required = false) String address,
            @RequestParam(name = "square", required = false) Double square) {
        Apartment apartment = getApartment(id);
        apartment.setAddress(address);
        apartment.setSquareValue(square);
        apartmentService.save(apartment);
    }

    @ApiOperation("Updates partially the specified apartment.")
    @PatchMapping("/{id}")
    public void patch(
            @ApiParam(name = "id", value = "Specifies the ID of Apartment that need to patch")
            @PathVariable(name = "id") Long id,
            @ApiParam(name = "address", format = "String",
                    value = "Address of the apartment. The value cannot be negative")
            @RequestParam(name = "address", required = false) String address,
            @ApiParam(name = "square", format = "double",
                    value = "Square of the apartment. The value cannot be negative")
            @RequestParam(name = "square", required = false) Double square) {
        Apartment apartment = getApartment(id);

        if (address != null) {
            apartment.setAddress(address);
        }
        if (square != null) {
            apartment.setSquareValue(square);
        }
        apartmentService.save(apartment);
    }

    private Apartment getApartment(Long id) {
        Optional<Apartment> apartmentOptional = apartmentService.getById(id);
        if (apartmentOptional.isEmpty()) {
            throw new ResourceNotFoundException("Apartment not found.");
        }

        return apartmentOptional.get();
    }

    private void assignApartmentsToUser (Apartment apartments, User user) {
        Optional<User> userOptional = userService.getById(user.getId());
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(("User is not found"));
        }
        user.setApartments(apartments);
        userService.save(user);
    }
}
