package com.licenta.rentalpropertymanager.repository;

import com.licenta.rentalpropertymanager.model.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface ImageRepository extends CrudRepository<Image, Long> {

    Image findFirstByPropertyId(long propertyId);
    List<Image> findAllByPropertyId(long propertyId);


    void deleteAllByPropertyId(long propertyId);

}
