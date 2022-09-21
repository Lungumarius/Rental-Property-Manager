package com.licenta.rentalpropertymanager.repository;


import com.licenta.rentalpropertymanager.model.StripeSessionCheckout;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends CrudRepository<StripeSessionCheckout, Long> {
    List<StripeSessionCheckout> findByPropertyId(Long properyId);

}