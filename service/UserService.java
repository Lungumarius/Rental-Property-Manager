package com.licenta.rentalpropertymanager.service;

import com.licenta.rentalpropertymanager.dto.*;
import com.licenta.rentalpropertymanager.mapping.PropertyMapper;
import com.licenta.rentalpropertymanager.mapping.UserMapper;
import com.licenta.rentalpropertymanager.model.*;
import com.licenta.rentalpropertymanager.repository.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private LandlordRepository landlordRepository;

    @Autowired
    private PropertyRepository propertyRepository;



    @Autowired
    private StripeProductRepository stripeProductRepository;

    @Autowired
    private PropertyMapper propertyMapper;

    @Autowired
    private SessionRepository sessionRepository;

    public User saveUser(UserDTO userDTO) throws Exception {
        User user = this.getUserbyMail(userDTO.getMail());
        if (user == null) {
            user = userRepository.save(userMapper.convertUserDTOtoModel(userDTO));
            return user;
        } else {
            throw new Exception("Email address already exists.");
        }
    }

    public void saveTenant(TenantDTO tenantDTO) {
        tenantRepository.save(userMapper.convertTenantDTOtoModel(tenantDTO));
    }
    public void saveLandlord(LandlordDTO landlordDTO) {
        landlordRepository.save(userMapper.convertLandlordDTOtoModel(landlordDTO));
    }
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {

        User user = userRepository.findByMail(mail);
        if(user == null)
            throw new UsernameNotFoundException( "User not found");
        return new org.springframework.security.core.userdetails.User(user.getMail(), user.getPassword(),getAuthorities(mail)); // (2)
    }
    public RedirectView loginStripeLandlord() throws StripeException {
        User user = userRepository.findByMail(SecurityContextHolder.getContext().
                getAuthentication().getName());

        Landlord landlord = landlordRepository.findByLandlordId(user.getId());
        RedirectView rv = new RedirectView();
        Stripe.apiKey = "sk_test_51LKJ3xGKfSv5e9bHpiS8Kc0eAZPTFI4aAnSwJaL6jGhlGe9BtywTiQjCKrj7PURNZNbIBcH1ZHrNuyxsnnU3aGJh0021lmxdEJ";
        if (landlord.getStripeAccountId() == null) {
            AccountCreateParams params =
                    AccountCreateParams
                            .builder()
                            .setCountry("RO")
                            .setType(AccountCreateParams.Type.EXPRESS)
                            .setCapabilities(
                                    AccountCreateParams.Capabilities
                                            .builder()
                                            .setCardPayments(
                                                    AccountCreateParams.Capabilities.CardPayments
                                                            .builder()
                                                            .setRequested(true)
                                                            .build()
                                            )
                                            .setTransfers(
                                                    AccountCreateParams.Capabilities.Transfers
                                                            .builder()
                                                            .setRequested(true)
                                                            .build()
                                            )
                                            .build()
                            )
                            .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                            .setBusinessProfile(
                                    AccountCreateParams.BusinessProfile
                                            .builder()
                                            .setUrl("https://www.google.ro/")
                                            .build()
                            )
                            .build();
            Account account = Account.create(params);
            landlord.setStripeAccountId(account.getId());
            landlordRepository.save(landlord);

            AccountLinkCreateParams params2 =
                    AccountLinkCreateParams
                            .builder()
                            .setAccount(account.getId())
                            .setRefreshUrl("http://localhost:8080/login")
                            .setReturnUrl("http://localhost:8080/index/landlord")
                            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                            .build();

            AccountLink accountLink = AccountLink.create(params2);

            rv.setUrl(accountLink.getUrl());

            return rv;
        }
        rv.setUrl("http://localhost:8080/index/landlord");
        return rv;


    }
    public RedirectView checkPayment(StripeSessionCheckoutDTO stripeSessionCheckoutDTO) throws StripeException, ParseException {
        Stripe.apiKey = "sk_test_51LKJ3xGKfSv5e9bHpiS8Kc0eAZPTFI4aAnSwJaL6jGhlGe9BtywTiQjCKrj7PURNZNbIBcH1ZHrNuyxsnnU3aGJh0021lmxdEJ";
        RedirectView rv = new RedirectView();
        Session session =
                Session.retrieve(
                        stripeSessionCheckoutDTO.getSessionId()
                );
        if(session.getPaymentStatus().equals("paid")){
            Property property = propertyRepository.findById(stripeSessionCheckoutDTO.getPropertyId()).orElse(null);
            property.setRentPaid(true);

            SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = formatter.parse(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
            property.setLastPaidAt(date);
            propertyRepository.save(property);
            rv.setUrl("http://www.localhost:8080/api/property/tenant/mydetails");
        }
        else
            rv.setUrl("https://www.google.com/");

        return rv;

    }
    public StripeSessionCheckoutDTO getStripeSessionDTOByPropertyId(Long propertyId){

        List<StripeSessionCheckout> list = sessionRepository.findByPropertyId(propertyId);
        StripeSessionCheckoutDTO stripeSessionCheckoutDTO = userMapper.convertStripeSessionCheckoutToDTO(list.get(list.size()-1));
        return stripeSessionCheckoutDTO;
    }
    public RedirectView payRent(long propertyId) throws StripeException {
        RedirectView rv = new RedirectView();
        if(propertyRepository.findById(propertyId).getRentPaid()==false){
        Stripe.apiKey = "sk_test_51LKJ3xGKfSv5e9bHpiS8Kc0eAZPTFI4aAnSwJaL6jGhlGe9BtywTiQjCKrj7PURNZNbIBcH1ZHrNuyxsnnU3aGJh0021lmxdEJ";
            StripeProductDTO stripeProductDTO = propertyMapper.converStripeProductDTOtoModel(stripeProductRepository.findByProductId
                    (propertyRepository.findById(propertyId).getProductId()));
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(stripeProductDTO.getPriceCode())
                                        .setQuantity(1L)
                                        .build())
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://www.localhost:8080/api/user/paymentcheck/" + propertyId)
                        .setCancelUrl("https://www.amazon.com/")
                        .setPaymentIntentData(
                                SessionCreateParams.PaymentIntentData.builder()
                                        .setApplicationFeeAmount(1L)
                                        .setTransferData(
                                                SessionCreateParams.PaymentIntentData.TransferData.builder()
                                                        .setDestination(landlordRepository.findByLandlordId(propertyRepository.findById
                                                                (propertyId).getLandlord().getLandlordId()).getStripeAccountId())
                                                        .build())
                                        .build())
                        .build();
        Session session = Session.create(params);


        StripeSessionCheckout stripeSession = new StripeSessionCheckout();
        stripeSession.setSessionId(session.getId());
        stripeSession.setPropertyId(propertyId);
        sessionRepository.save(stripeSession);

        rv.setUrl(session.getUrl());
        }
        else
            rv.setUrl("https://www.amazon.com/");
        //Error page
        return rv;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String mail){
        User user = userRepository.findByMail(mail);
        String authority;
        if(user.getUserType())
            authority = "ROLE_LANDLORD";
        else
            authority = "ROLE_TENANT";
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authority));
        return authorities;
    }


    public User getUserbyMail(String mail) {
        User user = userRepository.findByMail(mail);
        return user;
    }

    public Landlord getLandlord(long landlordId) throws Exception {
        Optional<Landlord> landlordOptional = landlordRepository.findById(landlordId);
        if (landlordOptional.isPresent()) {
            return landlordOptional.get();
        } else {
            throw new Exception("Landlord not found");
        }
    }
    public long getIdByMail(String mail) throws Exception{
        long id;
        User user = new User();
          user  = userRepository.findByMail(mail);
        id = user.getId();
        return id ;
        }
    public LandlordDTO getLandlordDTOByMail() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalMail = authentication.getName();
        long landlordId = getIdByMail(currentPrincipalMail);
        LandlordDTO landlordDTO = userMapper.convertLandlordtoDTO(landlordRepository.findByLandlordId(landlordId));
        return landlordDTO;
    }


    public Tenant getTenant(long tenantId) throws Exception {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            return tenantOptional.get();
        } else {
            throw new Exception("Tenant not found");
        }
    }
    public List<UserDTO> getAllTenants(){
        List<UserDTO> allTenants = new ArrayList<>();
        userRepository.findAllByUserType(false).forEach(user -> {
            allTenants.add(userMapper.convertUserToDTO(user));
        });
        return allTenants;
    }
    public Boolean getUserType(){
       return userRepository.findByMail
                (SecurityContextHolder.getContext().getAuthentication().getName()).getUserType();
    }

    public UserDTO getTenantByPropertyId(long propertyId){
        return userMapper.convertUserToDTO
                (propertyRepository.findById(propertyId).getTenant().getUser());
    }
    public List<UserDTO> getTenantByPropertyDTOList(List<PropertyDTO> propertyDTOList) throws Exception{
        List<UserDTO> tenantDTOList = new ArrayList<>();
        propertyDTOList.forEach(propertyDTO -> {
            if(propertyDTO.getTenantId()!=null){
                User userOptional = userRepository.findById(propertyDTO.getTenantId()).orElse(null);

                tenantDTOList.add(userMapper.convertUserToDTO(userOptional));


            }
        });

        return tenantDTOList;

    }








//    public void saveFavorite(FavoriteDTO favoriteDTO) throws Exception {
//        Customer user = this.getUser(favoriteDTO.getUserId());
//        List<Favorite> favoriteList = mapper.convertFavoriteDTOtoModel(favoriteDTO, user);
//        favoriteList.forEach(favorite -> favoriteRepository.save(favorite));
//
//    }
//
//    public UserDTO getUserDTObyId(long userId) throws Exception {
//        Customer user = this.getUser(userId);
//        return userMapper.convertUserToDTO(user);
//    }
//
//    public UserDTO getUserDTObyMail(String mail) throws Exception {
//        Customer user = this.getUserbyMail(mail);
//        return userMapper.convertUserToDTO(user);
//    }
//
//    public FavoriteDTO getFavoriteDTO(long userId) throws Exception {
//        List<Favorite> favoriteList = favoriteRepository.findByUserId(userId);
//        if (favoriteList.isEmpty()) {
//            throw new Exception("User has no favorites.");
//
//        } else {
//            return mapper.convertFavoritetoDTO(favoriteList);
//        }
//    }
}
