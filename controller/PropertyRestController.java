package com.licenta.rentalpropertymanager.controller;

import com.licenta.rentalpropertymanager.dto.LandlordDTO;
import com.licenta.rentalpropertymanager.dto.PropertyDTO;
import com.licenta.rentalpropertymanager.service.PropertyService;
import com.licenta.rentalpropertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/property")
public class PropertyRestController {

    @Autowired
    private PropertyService propertyService;


    @GetMapping(value = "/addtenantasmodel/{propertyId}")
    public String addTenantProperty(@PathVariable(value = "propertyId") Long propertyId, @RequestParam(value = "mail") String tenantMail) throws Exception {
        propertyService.editProperty(propertyId, tenantMail);

        return "redirect:/landlord/myproperties";
    }

    @PostMapping(value = "/add")
    public String addProperty(@ModelAttribute PropertyDTO propertyDTO, @ModelAttribute LandlordDTO landlordDTO, @RequestParam("images[]") MultipartFile[] multipartfiles) throws Exception {
        propertyService.saveProperty(propertyDTO, multipartfiles);

        return "redirect:/landlord/myproperties";
    }




    @GetMapping(value = "/delete/{id}")


    public String deleteProperty(@PathVariable long id) throws IOException {

        propertyService.deleteProperty(id);

        return "redirect:/landlord/myproperties";

    }

    //    @GetMapping(value = "/propertiesByLandlordId")
//    public ResponseEntity getPropertiesByLandlordId() throws Exception {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalMail = authentication.getName();
//        long landlordId = userService.getIdByMail(currentPrincipalMail);
//        List<PropertyDTO> propertyDTOList = propertyService.getLandlordProperties(userService.getLandlord(landlordId));
//        return ResponseEntity.accepted().body(propertyDTOList);
//    }


}

