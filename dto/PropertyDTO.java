package com.licenta.rentalpropertymanager.dto;

import com.licenta.rentalpropertymanager.model.Landlord;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class PropertyDTO {

    private long id;
    private long landlordId;
    private Long tenantId;
    private String imageUrl;
    @NotNull
    @Size(min = 5, max = 50)
    private String location;
    @NotNull
    @Size(min = 1, max = 6)
    private long rentPrice;
    @NotNull
    private Boolean allowSmoker;
    @NotNull
    private Boolean allowPetOwner;
    @NotNull
    private Boolean hasOccupation;
    @NotNull
    private String city;
    private Date dateAdded;
    @NotNull
    private Long numberOfRooms;
    private Date lastPaidAt;
    private Boolean rentPaid;
    private Date rentedAt;

}
