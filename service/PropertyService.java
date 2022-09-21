package com.licenta.rentalpropertymanager.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.licenta.rentalpropertymanager.dto.LandlordDTO;
import com.licenta.rentalpropertymanager.dto.PropertyDTO;
import com.licenta.rentalpropertymanager.dto.StripeProductDTO;
import com.licenta.rentalpropertymanager.mapping.PropertyMapper;
import com.licenta.rentalpropertymanager.model.*;
import com.licenta.rentalpropertymanager.repository.*;
import com.licenta.rentalpropertymanager.util.ImageHandler;
import com.stripe.Stripe;
import com.stripe.model.Price;
import com.stripe.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyMapper propertyMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LandlordRepository landlordRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StripeProductRepository stripeProductRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public void saveImage(Image image){

        imageRepository.save(image);
    }

    public List<String> counties = Arrays.asList("Alba", "Arad", "Arges", "Bacau", "Bihor", "Bistrita-Nasaud", "Botosani", "Braila", "Brasov", "Bucuresti", "Buzau", "Calarasi", "Caras-Severin", "Cluj", "Constanta", "Covasna", "Dimbovita", "Dolj", "Galati", "Gorj", "Giurgiu", "Harghita", "Hunedoara", "Ialomita", "Iasi", "Ilfov", "Maramures", "Mehedinti", "Mures", "Neamt", "Olt", "Prahova", "Salaj", "Satu Mare", "Sibiu", "Suceava", "Teleorman", "Timis", "Tulcea", "Vaslui", "Vilcea", "Vrancea");


    public void editProperty(Long propertyId, String mail) throws Exception {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        Long userId = userService.getIdByMail(mail);
        if(property!=null){
            property.setTenant(userService.getTenant(userId));
            SimpleDateFormat formatter =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm");
            property.setRentedAt(formatter.parse
                    (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").
                            format(LocalDateTime.now())));
            property.setLastPaidAt(formatter.
                    parse(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").
                            format(LocalDateTime.now())));
            property.setRentPaid(true);
            propertyRepository.save(property);
        }

    }
    public void saveProperty(PropertyDTO propertyDTO, MultipartFile[] multipartFiles) throws Exception {
        Property property;
        Stripe.apiKey = "sk_test_51LKJ3xGKfSv5e9bHpiS8Kc0eAZPTFI4aAnSwJaL6jGhlGe9BtywTiQjCKrj7PURNZNbIBcH1ZHrNuyxsnnU3aGJh0021lmxdEJ";
        propertyDTO.setRentPaid(true);
        property = propertyMapper.convertPropertyDTOtoModel(propertyDTO);
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Property" + property.getLandlord().getLandlordId() + property.getNumberOfRooms() + property.getLocation());
        Product product = Product.create(params);
        StripeProduct stripeProduct = new StripeProduct();
        stripeProduct.setProductCode(product.getId());
        Map<String, Object> params2 = new HashMap<>();
        params2.put("unit_amount", property.getRentPrice()*100);
        params2.put("currency", "ron");
        params2.put("product", product.getId());
        Price price = Price.create(params2);
        stripeProduct.setPriceCode(price.getId());
        stripeProductRepository.save(stripeProduct);
        property.setProductId(stripeProduct.getProductId());
        propertyRepository.save(property);
        String uploadDir = "property-photos/" + property.getId();
        List<Image> imagesList= new ArrayList<>();
        if(Arrays.stream(multipartFiles).findAny().isPresent()){
            Arrays.stream(multipartFiles).filter(multipartFile -> multipartFile.getOriginalFilename().length()!=0).forEach(multipartFile -> {
                Image image = new Image();
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                image.setImageName(fileName);
                image.setProperty(property);
                imagesList.add(image);
                try {
                    ImageHandler.saveFile(uploadDir, fileName, multipartFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            imagesList.forEach(image -> {
                saveImage(image);
            });
        }
    }
    public List<PropertyDTO> getAllProperties() {

        List<PropertyDTO> propertyDTOList = new ArrayList<>();
        Iterable<Property> properties = propertyRepository.findAll();

        properties.forEach(property -> {
            if(property.getTenant()==null)
                propertyDTOList.add(propertyMapper.convertPropertyModelToDTO(property));

        });

        return propertyDTOList;
    }
    public List<String> getFirstPropertyImageUrlsAll(){
        List<String> findImageNameList = new ArrayList<>();
        getAllProperties().forEach(propertyDTO -> {
            findImageNameList.add("../../../../../property-photos/" +
                    propertyDTO.getId() + "/" + propertyMapper.convertImageModelToDTO(
                            imageRepository.findFirstByPropertyId(propertyDTO.getId())).getImageName());
        });
        return findImageNameList;
    }
    public List<String> getAllImagesByPropertyId(long id){
        List<String> findImageNameList = new ArrayList<>();
       PropertyDTO propertyDTO = findPropertyById(id);
        imageRepository.findAllByPropertyId(propertyDTO.getId()).forEach(image -> {
            findImageNameList.add(("../../../../../property-photos/" + propertyDTO.getId() + "/" + propertyMapper.convertImageModelToDTO(image).getImageName()));
        });

       return findImageNameList;
    }

    public List<String> getFirstPropertyImagesUrls() throws Exception {

        List<String> firstImageNameList = new ArrayList<>();
        if(getLandlordProperties().size()!=0) {
            getLandlordProperties().forEach(landlordProperty -> {

                firstImageNameList.add("../../../../../property-photos/" + landlordProperty.getId() + "/" + propertyMapper.convertImageModelToDTO(imageRepository.findFirstByPropertyId(landlordProperty.getId())).getImageName());
            });
        }


        return firstImageNameList;

    }
    public String getFirstTenantPropertyImageUrl() throws Exception {
            String image= "";
            image =  "../../../../../property-photos/" + getPropertyByTenant().getId() + "/" + propertyMapper.convertImageModelToDTO(imageRepository.findFirstByPropertyId(getPropertyByTenant().getId())).getImageName();
            return image;
    }

    public void deleteImages(long id)  {

        imageRepository.deleteAllByPropertyId(id);
        String path = "C:\\Users\\mariu\\Desktop\\rentalLicenta - Copy\\property-photos\\" + id;
        String base = "C:\\Users\\mariu\\Desktop\\rentalLicenta - Copy\\";
        String relative = new File(base).toURI().relativize(new File(path).toURI()).getPath();
        Path rootPath = Paths.get(relative);
        try (Stream<Path> walk = Files.walk(rootPath)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
        }
        catch (Exception ignore){

        }

    }
    public void deleteProperty(long id)  {
        deleteImages(id);
        if(imageRepository.findFirstByPropertyId(id) == null)
            propertyRepository.deleteById(id);


    }


    public boolean getContext(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserbyMail(authentication.getName()).getUserType();
    }
    public Landlord getLandlordContext() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalMail = authentication.getName();
        return landlordRepository.findById(userService.getIdByMail(currentPrincipalMail)).orElse(null);
    }
    public RedirectView payRentAsLandlord(Long propertyId) throws Exception {
        RedirectView rv = new RedirectView();
        rv.setUrl("http://localhost:8080/api/property/details/" + propertyId);
        getLandlordProperties().forEach(propertyDTO -> {
            if(propertyDTO.getId() == propertyId){
                Property property = propertyRepository.findById(propertyId).orElse(null);
                property.setRentPaid(true);
                SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    property.setLastPaidAt(formatter.parse(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                propertyRepository.save(property);
            }
        });
        return rv;
    }

    //Every day schedule to check all properties last paid at / if  30 days passed change


    public List<PropertyDTO> getLandlordProperties() throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalMail = authentication.getName();
        long landlordId = userService.getIdByMail(currentPrincipalMail);
        Landlord landlord = landlordRepository.findByLandlordId(landlordId);
        List<PropertyDTO> propertyDTOList = new ArrayList<>();
        Iterable<Property> properties = propertyRepository.findByLandlord(landlord);

        properties.forEach(property -> {
            propertyDTOList.add(propertyMapper.convertPropertyModelToDTO(property));
        });

        return propertyDTOList;

    }
    public List<String> getImagesByPropertyDTOs( List<PropertyDTO> propertyDTOList) {
        List<String> images = new ArrayList<>();
        propertyDTOList.forEach(
                propertyDTO -> {
                    images.add(
                            "../../../../../property-photos/" + propertyDTO.getId() +
                                    "/" + propertyMapper.convertImageModelToDTO(imageRepository.
                                    findFirstByPropertyId(propertyDTO.getId())).getImageName());});
                    return images;
                }


    public PropertyDTO getPropertyByTenant() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalMail = authentication.getName();
        Tenant tenant = tenantRepository.findById(userService.getIdByMail(currentPrincipalMail)).orElse(null);
        return propertyMapper.convertPropertyModelToDTO(propertyRepository.findByTenant(tenant));
    }






    public List<PropertyDTO> getPropertySortedList(String sortField, String sortDir, String city, Long noRoom) throws Exception {
        final List<PropertyDTO> propertyDTOList = new ArrayList<>();
        List<PropertyDTO> propertyDTOList2 = new ArrayList<>();
        try{
            if (sortField.equals("rentPrice") && sortDir.equals("ASC")) {
                propertyRepository.findAllByOrderByRentPriceAsc().forEach(property -> {
                    propertyDTOList.add(propertyMapper.convertPropertyModelToDTO(property));
                });
            }
            else
            if (sortField.equals("rentPrice") && sortDir.equals("DESC")) {
                propertyRepository.findAllByOrderByRentPriceDesc().forEach(property -> {
                    propertyDTOList.add(propertyMapper.convertPropertyModelToDTO(property));
                });
            }
            else
            if(sortField.equals("date") && sortDir.equals("DESC")){
                propertyRepository.findAllByOrderByDateAddedDesc().forEach(property -> {
                    propertyDTOList.add(propertyMapper.convertPropertyModelToDTO(property));
                });
            }
            if(city!=null && noRoom == 0){
                propertyDTOList.forEach(propertyDTO -> {
                    if(propertyDTO.getCity().equals(city))
                        propertyDTOList2.add(propertyDTO);
                });
                return propertyDTOList2;
            }
            else if(city==null&& noRoom != 0){
                propertyDTOList.forEach(propertyDTO -> {
                    if(propertyDTO.getNumberOfRooms().equals(noRoom))
                        propertyDTOList2.add(propertyDTO);
                });
                return propertyDTOList2;
            }
            else
                if(city!= null && noRoom!=0 ){
                    propertyDTOList.forEach(propertyDTO -> {
                        if(propertyDTO.getCity().equals(city) && propertyDTO.getNumberOfRooms().equals(noRoom))
                            propertyDTOList2.add(propertyDTO);
                    });
                    return propertyDTOList2;
                }
            return propertyDTOList;

        }catch (Exception e){

            throw new Exception("Page not found !");

        }

    }

public PagedListHolder<PropertyDTO> findPaginatedAllProperties(int pageNo, int pageSize, String sortField, String sortDir, String city, long noRoom) throws Exception {

    List<PropertyDTO> allPropertiesWithoutTenant = new ArrayList<>();




    getPropertySortedList(sortField,sortDir,city, noRoom).forEach(property -> {
        if(property.getTenantId()==null){
            allPropertiesWithoutTenant.add(property);
        }
    });
    PagedListHolder page = new PagedListHolder(allPropertiesWithoutTenant);
    page.setPage(pageNo-1);
    page.setPageSize(pageSize);

    return page;
}

    public PagedListHolder<PropertyDTO> findPaginatedMyProperties(int pageNo, int pageSize) throws Exception {
//        MutableSortDefinition x = new MutableSortDefinition ("rentPrice", true, true);

        PagedListHolder page = new PagedListHolder(getLandlordProperties());
//        page.setSort(x);
        page.setPage(pageNo-1);
        page.setPageSize(pageSize);

        return page;
    }
    public PropertyDTO findPropertyById(long id){
        PropertyDTO propertyDTO  = propertyMapper.convertPropertyModelToDTO(propertyRepository.findById(id));
        return propertyDTO;
    }



}
