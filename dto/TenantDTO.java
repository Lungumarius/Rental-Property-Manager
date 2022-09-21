package com.licenta.rentalpropertymanager.dto;

import com.licenta.rentalpropertymanager.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class TenantDTO{
    private Long tenantId;
    @NotNull
    private Boolean isSmoker;
    @NotNull
    private Boolean isPetOwner;

    @Size(max = 30, message = "Maximum 30 characters allowed")
    private String occupation;
    @NotNull
    @Size(max = 2, message = "Invalid age")
    private int age;
    User user;
    // 0 - tenant / 1 - landlord
}
