package com.licenta.rentalpropertymanager.controller;

import com.licenta.rentalpropertymanager.dto.*;
import com.licenta.rentalpropertymanager.model.User;
import com.licenta.rentalpropertymanager.repository.SessionRepository;
import com.licenta.rentalpropertymanager.service.PropertyService;
import com.licenta.rentalpropertymanager.service.UserService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;


@Controller
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private UserService userServices;
    //
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RestController restController;
    @Autowired
    private HttpSession httpSession;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private SessionRepository sessionRepository;

    //    @Autowired
//    private JwtUtil jwtUtil;
    @PostMapping(value = "/login")
    public RedirectView loginUser(@ModelAttribute UserDTO newUser, HttpServletResponse response) throws Exception {
        try {

            //calling the authentication manager from the SecurityConfigurer.java


            Authentication authentication = new UsernamePasswordAuthenticationToken(newUser.getMail(),
                    newUser.getPassword(), userServices.getAuthorities(newUser.getMail()));
            authenticationManager.authenticate(
                    authentication
            );
            final SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);


            // create a cookie
//        response.sendRedirect("http://localhost:8080/properties");
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect mail or password", e);
        }
        RedirectView rv = new RedirectView();
        if(!userServices.getUserbyMail(newUser.getMail()).getUserType()) {

            rv.setUrl("http://localhost:8080/index/tenant");
            return rv;
        }

        else
            return userServices.loginStripeLandlord();

    }
    @GetMapping(value = "/payrent/{propertyId}")
    public RedirectView payRent(@PathVariable (value = "propertyId") Long propertyId) throws StripeException {
        return userServices.payRent(propertyId);
    }
    @GetMapping(value = "/paymentcheck/{propertyId}")
    public RedirectView checkPayment(@PathVariable (value = "propertyId") Long propertyId) throws StripeException, ParseException {
        return userServices.checkPayment(userServices.getStripeSessionDTOByPropertyId(propertyId));
    }

    @GetMapping(value = "/landlord/payrent/{propertyId}")
    public RedirectView payRentAsLandlord(@PathVariable (value = "propertyId") Long propertyId) throws Exception {
        return propertyService.payRentAsLandlord(propertyId);
    }
    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
        //Where you go after logout here.
    }

    @PostMapping(value = "/addtenant")
    public String receiveTenant(@ModelAttribute TenantCreationDTO tenantCreationDTO) throws Exception {
            UserDTO userDTO = tenantCreationDTO.getUserDTO();
            TenantDTO tenantDTO = tenantCreationDTO.getTenantDTO();
            userDTO.setUserType(false);
            tenantDTO.setUser(userServices.saveUser(userDTO));
            userServices.saveTenant(tenantDTO);
            return "redirect:/login";
    }

    @PostMapping(value = "/addlandlord")
    public String receiveLandlord(@ModelAttribute LandlordCreationDTO landlordCreationDTO) throws Exception {

            UserDTO userDTO = landlordCreationDTO.getUserDTO();
            LandlordDTO landlordDTO = landlordCreationDTO.getLandlordDTO();
            userDTO.setUserType(true);
            landlordDTO.setUser(userServices.saveUser(userDTO));

            userServices.saveLandlord(landlordDTO);

            return "redirect:/login";

    }
}
