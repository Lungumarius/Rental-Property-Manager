package com.licenta.rentalpropertymanager.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class StripeProductDTO {

   private String productCode;
   private String priceCode;
}
