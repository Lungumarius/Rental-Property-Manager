package com.licenta.rentalpropertymanager.repository;


import com.licenta.rentalpropertymanager.model.Landlord;
import com.licenta.rentalpropertymanager.model.Property;
import com.licenta.rentalpropertymanager.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PropertyRepository extends PagingAndSortingRepository<Property, Long> {
    Property findByTenant(Tenant tenant);
    List<Property> findByTenantIsNotNull();
    List<Property> findByLandlord(Landlord landlord);
    Property findById(long id);
    List<Property> findAllByOrderByRentPriceAsc();
    List<Property> findAllByOrderByRentPriceDesc();
    List<Property> findAllByOrderByDateAddedDesc();


    @Modifying
    @Query("delete from Property p where p.id =:#{#id} ")
    void deleteById(@Param("id") long id);








}
