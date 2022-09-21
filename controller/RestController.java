package com.licenta.rentalpropertymanager.controller;

import com.licenta.rentalpropertymanager.dto.LandlordCreationDTO;
import com.licenta.rentalpropertymanager.dto.PropertyDTO;
import com.licenta.rentalpropertymanager.dto.TenantCreationDTO;
import com.licenta.rentalpropertymanager.dto.UserDTO;
import com.licenta.rentalpropertymanager.service.PropertyService;
import com.licenta.rentalpropertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@Controller

@RequestMapping("/")
public class RestController {
    @Autowired
    PropertyService propertyService;

    @Autowired
    UserService userService;

  @GetMapping("")
    public String rootHandler(Model model) {
        if(userService.getUserType()==null){
            model.addAttribute("userDTO" , new UserDTO());
            return "login-v2";
        }
        else
            if(userService.getUserType()==true)
                return "index2";
            else
                return "index";

    }

    @GetMapping("/welcome")
    public String showWelcomePage() {
        return "welcome";
    }


    @GetMapping("/registertenant")
    public String showRegisterTenant(Model model) {
        model.addAttribute("tenantCreationDTO", new TenantCreationDTO());
        return "registertenant";
    }


    @GetMapping("/registerlandlord")
    public String showRegisterLandlord(Model model) {
        model.addAttribute("landlordCreationDTO", new LandlordCreationDTO());
        return "registerlandlord";
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "login-v2";
    }


    @GetMapping("/index/tenant")
    public String showTenantIndex() {
        return "index";
    }

    @GetMapping("/index/landlord")
    public String showLandlordIndex() {
        return "index2";
    }

    @GetMapping("/tenant/allproperties")
    public RedirectView redirectWithUsingRedirectVieww() {

        return new RedirectView("/tenant/allproperties/page/1?sortField=date&sortDir=DESC");
    }

    @GetMapping("/landlord/allproperties")
    public RedirectView redirectWithUsingRedirectView() {

        return new RedirectView("/landlord/allproperties/page/1?sortField=date&sortDir=DESC");
    }

    @GetMapping("/landlord/myproperties")
    public String viewDefaultLandlordProperties(Model model) throws Exception {
        return showLandlordProperties(1, model);
    }

    @GetMapping("/api/property/addtenant/{id}")
    public String addTenant(@PathVariable(value = "id") Long id, Model model) {
        String mail = "";
        model.addAttribute("currentProperty", id);
        model.addAttribute("tenantMail", mail);
        return "addtenant";
    }

    @GetMapping("/api/property/details/{id}")
    public String moreDetails(@PathVariable(value = "id") Long id, Model model) throws Exception {
        String mail = "";
        model.addAttribute("currentProperty", id);
        model.addAttribute("tenantMail", mail);
        if (propertyService.findPropertyById(id).getTenantId() != null) {
            model.addAttribute("tenant", userService.getTenantByPropertyId(id));
        }


        model.addAttribute("propertyDTO", propertyService.findPropertyById(id));
        model.addAttribute("currentUser", propertyService.getLandlordContext());
        model.addAttribute("imageDTOlinks", propertyService.getAllImagesByPropertyId(id));
        return "property-details";
    }
    @GetMapping("/api/property/tenant/details/{id}")
    public String tenantMoreDetails(@PathVariable(value = "id") Long id, Model model) throws Exception {
        String mail = "";
        model.addAttribute("currentProperty", id);
        model.addAttribute("tenantMail", mail);
        model.addAttribute("landlord", userService.getLandlord(propertyService.findPropertyById(id).getLandlordId()));
        model.addAttribute("propertyDTO", propertyService.findPropertyById(id));
        model.addAttribute("imageDTOlinks", propertyService.getAllImagesByPropertyId(id));
        return "property-tenant-details";
    }
    @GetMapping("/tenant/myproperty")
    public String myProperty(Model model) throws Exception {
        model.addAttribute("propertyDTO", propertyService.getPropertyByTenant());
        model.addAttribute("imageDTOlink", propertyService.getFirstTenantPropertyImageUrl());
        return "tenant-myproperty";
    }
    @GetMapping("/api/property/tenant/mydetails")
    public String tenantPropertyMoreDetails( Model model) throws Exception {
        String mail = "";
        model.addAttribute("propertyDTO", propertyService.getPropertyByTenant());
        model.addAttribute("landlord", userService.getLandlord(propertyService.findPropertyById(propertyService.getPropertyByTenant().getId()).getLandlordId()));
        model.addAttribute("imageDTOlinks", propertyService.getAllImagesByPropertyId(propertyService.getPropertyByTenant().getId()));
        return "property-tenant-mydetails";
    }



    @GetMapping("/landlord/myproperties/page/{pageNo}")
    public String showLandlordProperties(@PathVariable(value = "pageNo") int pageNo, Model model) throws Exception {

        model.addAttribute("propertyDTOs", propertyService.getLandlordProperties());

        int pageSize = 5;
        List<PropertyDTO> listProperties = new ArrayList<>();
        PagedListHolder<PropertyDTO> page = propertyService.findPaginatedMyProperties(pageNo, pageSize);
        page.getPageList().forEach(i -> {
            if (i != null)
                listProperties.add(i);
            else
                listProperties.add(new PropertyDTO());
        });

        String mail = "";

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getPageCount());
        model.addAttribute("totalItems", page.getNrOfElements());
        model.addAttribute("listProperties", listProperties);
        model.addAttribute("imageDTOlinks", propertyService.getFirstPropertyImagesUrls());
        model.addAttribute("tenantDTOList", userService.getTenantByPropertyDTOList
                (propertyService.getLandlordProperties()));
        model.addAttribute("mail", mail);

        return "landlord-myproperties";
    }

    @GetMapping("/landlord/allproperties/page/{pageNo}")
    public String showLandlordProperties(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField,
                                         @RequestParam("sortDir") String sortDir, @RequestParam(value = "city", required = false) String city, @RequestParam(value = "noRoom", required = false) Long noRoom) throws Exception {

        int pageSize = 5;
        if (noRoom == null) {
            noRoom = 0L;
        }

        PagedListHolder<PropertyDTO> page = propertyService.findPaginatedAllProperties(pageNo, pageSize, sortField, sortDir, city, noRoom);
        List<PropertyDTO> listProperties = page.getPageList();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getPageCount());
        model.addAttribute("totalItems", page.getNrOfElements());
        model.addAttribute("listProperties", listProperties);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("imageDTOs",  propertyService.getImagesByPropertyDTOs(listProperties));
        model.addAttribute("counties", propertyService.counties);
        model.addAttribute("currentCounty", city);
        model.addAttribute("currentnoRoom", noRoom);
        model.addAttribute("date");
        return "landlord-allproperties";
    }
    @GetMapping("/tenant/allproperties/page/{pageNo}")
    public String showTenantAllProperties(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField,
                                         @RequestParam("sortDir") String sortDir, @RequestParam(value = "city", required = false) String city, @RequestParam(value = "noRoom", required = false) Long noRoom) throws Exception {

        int pageSize = 5;
        if (noRoom == null) {
            noRoom = 0L;
        }

        PagedListHolder<PropertyDTO> page = propertyService.findPaginatedAllProperties(pageNo, pageSize, sortField, sortDir, city, noRoom);
        List<PropertyDTO> listProperties = page.getPageList();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getPageCount());
        model.addAttribute("totalItems", page.getNrOfElements());
        model.addAttribute("listProperties", listProperties);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("imageDTOs", propertyService.getImagesByPropertyDTOs(listProperties));
        model.addAttribute("counties", propertyService.counties);
        model.addAttribute("currentCounty", city);
        model.addAttribute("currentnoRoom", noRoom);
        model.addAttribute("date");
        return "tenant-allproperties";
    }


    @GetMapping("/landlord/addproperty")
    public String addProperty(Model model, RedirectAttributes redirectAttributes) throws Exception {
        model.addAttribute("propertyDTO", new PropertyDTO());
        model.addAttribute("landlordDTO", userService.getLandlordDTOByMail());

        model.addAttribute("counties", propertyService.counties);

        return "addproperty";
    }

}
