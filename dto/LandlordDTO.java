package com.licenta.rentalpropertymanager.dto;

import com.licenta.rentalpropertymanager.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class LandlordDTO{
    private long landlordId;
    User user;
    private int dueRentValue;
    private String stripeAccountId;
    // 0 - tenant / 1 - landlord
}