package com.licenta.rentalpropertymanager.mapping;

import com.licenta.rentalpropertymanager.dto.ImageDTO;
import com.licenta.rentalpropertymanager.dto.PropertyDTO;
import com.licenta.rentalpropertymanager.dto.StripeProductDTO;
import com.licenta.rentalpropertymanager.model.Image;
import com.licenta.rentalpropertymanager.model.Property;
import com.licenta.rentalpropertymanager.model.StripeProduct;
import com.licenta.rentalpropertymanager.repository.LandlordRepository;
import com.licenta.rentalpropertymanager.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {

    @Autowired
    LandlordRepository landlordRepository;

    @Autowired
    PropertyRepository propertyRepository;



//    public Image convertImageDTOtoModel(ImageDTO imageDTO){
//        Image image = new Image();
//        image.setImageId(imageDTO.getImageId());
//        image.setImageName(image.getImageName());
//        image.setProperty(propertyRepository.findById(imageDTO.getPropertyId()));
//        return image;
//    }
    public ImageDTO convertImageModelToDTO(Image image){
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageId(image.getImageId());
        imageDTO.setImageName(image.getImageName());
        imageDTO.setPropertyId(image.getProperty().getId());
        return imageDTO;
    }

    public Property convertPropertyDTOtoModel(PropertyDTO propertyDTO) throws ParseException {
//        List<Property> propertyList = new ArrayList<>();
//        propertyDTO.getPropertyIdList().forEach(propertyId -> {
//
//        });
//
//        return property;
        Property property = new Property();

        property.setAllowPetOwner(propertyDTO.getAllowPetOwner());
        property.setAllowSmoker(propertyDTO.getAllowSmoker());
        property.setHasOccupation(propertyDTO.getHasOccupation());
        property.setRentPrice(propertyDTO.getRentPrice());
        property.setLocation(propertyDTO.getLocation());

        property.setCity(propertyDTO.getCity().substring(1));
        property.setLandlord(landlordRepository.findByLandlordId(propertyDTO.getLandlordId()));
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        property.setDateAdded(formatter.parse(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now())));
        property.setNumberOfRooms(propertyDTO.getNumberOfRooms());
        property.setLastPaidAt(propertyDTO.getLastPaidAt());
        property.setRentPaid(propertyDTO.getRentPaid());
        property.setRentedAt(propertyDTO.getRentedAt());
        return property;
    }
    public StripeProductDTO converStripeProductDTOtoModel(StripeProduct stripeProduct){
        StripeProductDTO stripeProductDTO = new StripeProductDTO();
        stripeProductDTO.setProductCode(stripeProduct.getProductCode());
        stripeProductDTO.setPriceCode(stripeProduct.getPriceCode());

        return stripeProductDTO;

    }
    public PropertyDTO convertPropertyModelToDTO(Property property) {
            PropertyDTO propertyDTO = new PropertyDTO();
            propertyDTO.setId(property.getId());
            propertyDTO.setAllowPetOwner(property.getAllowPetOwner());
            propertyDTO.setAllowSmoker(property.getAllowSmoker());

            propertyDTO.setRentPrice(property.getRentPrice());
            propertyDTO.setLandlordId(property.getLandlord().getLandlordId());
            if(property.getTenant()!= null){
                propertyDTO.setTenantId(property.getTenant().getTenantId());
            }
            propertyDTO.setHasOccupation(property.getHasOccupation());
            propertyDTO.setLocation(property.getLocation());
            propertyDTO.setCity(property.getCity());
            propertyDTO.setDateAdded(property.getDateAdded());
            propertyDTO.setNumberOfRooms(property.getNumberOfRooms());
        propertyDTO.setLastPaidAt(property.getLastPaidAt());
        propertyDTO.setRentPaid(property.getRentPaid());
        propertyDTO.setRentedAt(property.getRentedAt());
     return propertyDTO;
    }
    public List<PropertyDTO> convertPropertyList(List<Property> propertyList) {
        return propertyList.stream().map(property -> this.convertPropertyModelToDTO(property)).collect(Collectors.toList());
    }


}
