package com.licenta.rentalpropertymanager.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//do not use @EqualsAndHashCode break Jackson annotation
@Entity
@Table(name = "landlord")
public class Landlord implements Serializable {

    @Id
    private long landlordId;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name= "landlord_id")
    private User user;

    private String stripeAccountId;


    @OneToMany(mappedBy = "landlord",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Property> properties;





}
