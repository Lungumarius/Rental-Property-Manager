package com.licenta.rentalpropertymanager.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//do not use @EqualsAndHashCode break Jackson annotation
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String mail;
    private String phoneNumber;
    private String password;
    // 0 - tenant / 1 - landlord
    private Boolean userType;






}
