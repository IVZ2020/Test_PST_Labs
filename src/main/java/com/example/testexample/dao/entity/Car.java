package com.example.testexample.dao.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@ApiModel("Car")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "cars")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Car {

    public Car(@NotNull @Valid @Size(min = 3, max = 20, message = "Input 3-20 characters") String model, @NotNull @Valid @Size(min = 3, max = 20, message = "Input 3-20 characters") String number) {
        this.model = model;
        this.number = number;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Valid
    @Size(min = 3, max = 20, message = "Input 3-20 characters")
    @Column
    private String model;

    @NotNull
    @Valid
    @Size(min = 3, max = 20, message = "Input 3-20 characters")
    @Column
    private String number;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
