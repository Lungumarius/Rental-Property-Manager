package com.licenta.rentalpropertymanager.util;

import com.licenta.rentalpropertymanager.dto.UserDTO;
import com.licenta.rentalpropertymanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired HttpSession session; //autowiring session

    @Autowired UserRepository repository; //autowire the user repo


    private static final Logger logger =  LoggerFactory.getLogger(AuthenticationSuccessHandlerImpl.class);


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String mail = "";
        if(authentication.getPrincipal() instanceof Principal) {
            mail = ((Principal)authentication.getPrincipal()).getName();

        }else {
            mail = ((UserDTO)authentication.getPrincipal()).getMail();
        }
        logger.info("mail: " + mail);
        //HttpSession session = request.getSession();
        session.setAttribute("userId", mail);
    }
}