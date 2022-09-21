package com.licenta.rentalpropertymanager.repository;


import com.licenta.rentalpropertymanager.model.Landlord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandlordRepository extends CrudRepository<Landlord, Long> {
    Landlord findByLandlordId(Long id);



}