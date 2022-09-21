package com.licenta.rentalpropertymanager.model;


import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//do not use @EqualsAndHashCode break Jackson annotation
@Entity
@Table(name = "property")
public class Property implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "landlord_id")
    private Landlord landlord;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private String location;
    private long rentPrice;
    private Boolean allowSmoker;
    private Date dateAdded;
    private Boolean allowPetOwner;
    private Boolean hasOccupation;
    private String city;
    private Long numberOfRooms;
    private Date lastPaidAt;
    private Boolean rentPaid;
    private Date rentedAt;


    @JoinColumn(name = "product_id")
    private long productId;


    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    List<Image> image;


}

