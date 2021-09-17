package com.example.testexample.controllers;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Size;

import com.example.testexample.controllers.exceptions.ResourceNotFoundException;
import com.example.testexample.dao.entity.Job;
import com.example.testexample.service.JobService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api("Specifies a set of operations to work with Jobs")
@RestController
@Validated
@RequestMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobController {

    @Autowired
    private JobService jobService;

    @ApiOperation("Returns sorted and paged list of jobs.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Job exists"),
            @ApiResponse(code = 404, message = "Job not found"),
            @ApiResponse(code = 400, message = "Incorrect values for sorting"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("")
    public List<Job> getAll (
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
        return jobService.findAll(pagingSort);
    }

    @ApiOperation("Returns a full info about the specified Job. If Job not found -> throws NOT FOUND (404)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Job exists"),
            @ApiResponse(code = 404, message = "Job not found"),
            @ApiResponse(code = 500, message = "Internal error")})
    @GetMapping("/{id}")
    public Job getJob (
            @ApiParam(name = "id", value = "Specifies the ID of Job that need to get")
            @PathVariable(name = "id") long id) {
                return getJobById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "If Job was successfully created"),
            @ApiResponse(code = 400, message = "Incorrect values for Job"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Job createJob (
        @ApiParam(name = "job", format = "String", value = "Model of the Job. Length should be more 2 and less than 31 characters")
        @RequestParam(name = "job", required = true) @Size(min = 3, max = 30) String jobName
    ) {
        Job job = new Job();
        job.setJob(jobName);
        return jobService.save(job);
    }

    @ApiOperation("Deletes the specified job")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If Job was successfully deleted"),
            @ApiResponse(code = 500, message = "Internal error")})
    @DeleteMapping("/{id}")
    public void deleteJob (
            @ApiParam(name = "id", value = "Specifies the ID of Job that need to delete")
            @PathVariable(name = "id") long id
    ) {
        jobService.deleteById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "If Job was successfully updated"),
            @ApiResponse(code = 400, message = "Incorrect values for Job"),
            @ApiResponse(code = 500, message = "Internal error")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateJob (
            @ApiParam(name = "id", value = "Specifies the ID of Job that need to update")
            @PathVariable(name = "id") long id,
            @ApiParam(name = "job", format = "String",
                    value = "Job name. Length should be more 2 and less than 31 characters")
            @RequestParam(name = "job", required = false) String jobName
    ) {
        Job job = getJobById(id);
        job.setJob(jobName);
        jobService.save(job);
    }

    private Job getJobById (long id) {
        Optional<Job> jobOptional = jobService.getById(id);
        if (jobOptional.isEmpty()) {
            throw new ResourceNotFoundException("Job is not found");
        }
        return jobOptional.get();
    }
}
