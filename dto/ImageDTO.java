package com.licenta.rentalpropertymanager.dto;

import com.licenta.rentalpropertymanager.model.Property;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class ImageDTO {
    private long imageId;
    private long propertyId;
    private String imageName;
}
