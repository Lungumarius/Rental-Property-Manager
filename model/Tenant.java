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
@Table(name = "tenant")
public class Tenant implements Serializable {

    @Id
    private Long tenantId;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "tenant_id")
    private User user;

    private Boolean isSmoker;
    private Boolean isPetOwner;
    private String occupation;
    private int age;

    @OneToMany(mappedBy = "tenant", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Property> propertyList = new ArrayList<>();


}
