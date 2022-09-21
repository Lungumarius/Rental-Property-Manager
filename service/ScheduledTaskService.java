package com.licenta.rentalpropertymanager.service;

import com.licenta.rentalpropertymanager.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@EnableScheduling
public class ScheduledTaskService {
    @Autowired
    PropertyRepository propertyRepository;

    @Scheduled(cron = "0 0 * * *")
    public void rentChecker() throws ParseException {
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = formatter.parse(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
        propertyRepository.findByTenantIsNotNull().forEach(property -> {
            if(property.getLastPaidAt().after(date) && property.getRentPaid() == true){
                property.setRentPaid(false);
            }
        });


    }
}
