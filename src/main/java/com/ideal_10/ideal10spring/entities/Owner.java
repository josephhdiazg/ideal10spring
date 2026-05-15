package com.ideal_10.ideal10spring.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String identificationNumber;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String fullName;

    @Size(max = 30)
    @Column(length = 30)
    private String phone;

    @Email
    @Size(max = 120)
    @Column(length = 120)
    private String email;

    @Column(nullable = false)
    private Boolean active = true;
}
