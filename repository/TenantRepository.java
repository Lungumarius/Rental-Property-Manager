package com.licenta.rentalpropertymanager.repository;

import com.licenta.rentalpropertymanager.model.Tenant;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, Long> {

}
