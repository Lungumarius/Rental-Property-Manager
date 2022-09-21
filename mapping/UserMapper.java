package com.licenta.rentalpropertymanager.mapping;

import com.licenta.rentalpropertymanager.dto.*;
import com.licenta.rentalpropertymanager.model.Landlord;
import com.licenta.rentalpropertymanager.model.StripeSessionCheckout;
import com.licenta.rentalpropertymanager.model.Tenant;
import com.licenta.rentalpropertymanager.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public UserDTO convertUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setMail(user.getMail());
        userDTO.setUserType(user.getUserType());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        return userDTO;
    }
public User convertUserDTOtoModel(UserDTO userDTO) {
    User user = new User();
    user.setLastName(userDTO.getLastName());
    user.setFirstName(userDTO.getFirstName());
    user.setMail(userDTO.getMail());
    user.setPassword(bCryptPasswordEncoder().encode(userDTO.getPassword()));
    user.setUserType(userDTO.getUserType());
    user.setPhoneNumber(userDTO.getPhoneNumber());

    return user;
}
    public Tenant convertTenantDTOtoModel(TenantDTO tenantDTO) {
        Tenant tenant = new Tenant();
        tenant.setAge(tenantDTO.getAge());
        tenant.setOccupation(tenantDTO.getOccupation());
        tenant.setIsPetOwner(tenantDTO.getIsPetOwner());
        tenant.setIsSmoker(tenantDTO.getIsSmoker());
        tenant.setUser(tenantDTO.getUser());
        return tenant;
    }
    public Landlord convertLandlordDTOtoModel(LandlordDTO landlordDTO) {
        Landlord landlord = new Landlord();

        landlord.setUser(landlordDTO.getUser());

        return landlord;
    }
    public LandlordDTO convertLandlordtoDTO(Landlord landlord) {

        LandlordDTO landlordDTO = new LandlordDTO();
        landlord.setLandlordId(landlord.getLandlordId());
        landlordDTO.setStripeAccountId(landlord.getStripeAccountId());
        landlordDTO.setUser(landlord.getUser());

        return landlordDTO;
    }
    public StripeSessionCheckoutDTO convertStripeSessionCheckoutToDTO(StripeSessionCheckout stripeSessionCheckout){
        StripeSessionCheckoutDTO stripeSessionCheckoutDTO = new StripeSessionCheckoutDTO();
        stripeSessionCheckoutDTO.setSessionId(stripeSessionCheckout.getSessionId());
        stripeSessionCheckoutDTO.setPropertyId(stripeSessionCheckout.getPropertyId());
        return stripeSessionCheckoutDTO;
    }
    public StripeSessionCheckout convertStripeSessionCheckoutDTOToModel(StripeSessionCheckoutDTO stripeSessionCheckoutDTO){
        StripeSessionCheckout stripeSessionCheckout = new StripeSessionCheckout();
        stripeSessionCheckout.setSessionId(stripeSessionCheckoutDTO.getSessionId());
        stripeSessionCheckout.setPropertyId(stripeSessionCheckoutDTO.getPropertyId());
        return stripeSessionCheckout;
    }


}
