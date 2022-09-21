package com.licenta.rentalpropertymanager.util;


import com.licenta.rentalpropertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity

public class SecurityConfigurer
        extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Autowired
    AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //My AuthenticationManager uses the user service to handle the Authentication Request of user details
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());



    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/property/tenant/mydetails",  "/index/tenant").hasAuthority("ROLE_TENANT")
                    .antMatchers("/api/user/**" , "/api/user/login", "/welcome", "/registertenant", "/registerlandlord","/api/user/addtenant", "/api/user/addlandlord", "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/vendor/**", "/fonts/**", "/build/**", "/dist/**", "/plugins/**" ).permitAll()// (1)
                    .antMatchers("/index/landlord","/api/property/addtenant/**").hasAuthority("ROLE_LANDLORD") // (2)
                    .anyRequest().authenticated() // (3)
                    .and()

                    .formLogin() // (5)

                   .loginPage("/login")
//                    .permitAll().successHandler(authenticationSuccessHandler)
                    .defaultSuccessUrl("/properties", true)
                    .usernameParameter("mail")
                    .passwordParameter("password")
                    .permitAll()
                    .and()
                    .logout() // (6)
                    .permitAll()
                    // (7)
                    .and()

                    .csrf().disable();
        }


    }



