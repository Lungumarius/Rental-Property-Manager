package com.licenta.rentalpropertymanager.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.RegEx;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class UserDTO {

    private long id;
    @NotNull
    @Size(min = 2, max = 15, message = "Length should be between 2 and 15 characters")
    private String firstName;
    @NotNull
    @Size(min = 2, max = 15, message = "Length should be between 2 and 15 characters")
    private String lastName;
    @NotNull
    @Email(regexp =
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", message = "Invalid email")

    private String mail;

    private String phoneNumber;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message =
            "Password should contain minimum eight characters, at least one uppercase letter, " +
                    "one lowercase letter and one number")

    private String password;
    // 0 - tenant / 1 - landlord
    private Boolean userType;


}
