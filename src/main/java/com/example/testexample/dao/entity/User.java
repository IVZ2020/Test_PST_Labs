package com.example.testexample.dao.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User {

    public User(@NotNull @Size(min = 3, max = 30, message = "Input 3-30 characters") @Valid String name, @NotNull @Valid int age) {
        this.name = name;
        this.age = age;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 30, message = "Input 3-30 characters")
    @Valid
    @Column
    private String name;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="user")
    private List<Car> cars = new ArrayList<>();

    @NotNull
    @Valid
    @Column
    private int age;

    @OneToOne
    private Apartment apartments;

    @ManyToMany
    @JoinTable(name="users_jobs",
            joinColumns = @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name="job_id", referencedColumnName="id")
    )
    private List<Job> jobs = new ArrayList<>();
}
