package com.licenta.rentalpropertymanager.repository;

import com.licenta.rentalpropertymanager.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByMail(String mail);
    User findById(long id);
    List<User> findAllByUserType(boolean userType);
}
