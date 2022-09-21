package com.licenta.rentalpropertymanager.repository;


import com.licenta.rentalpropertymanager.model.StripeProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripeProductRepository extends CrudRepository<StripeProduct, Long> {
    StripeProduct findByProductId(long propertyId);


}