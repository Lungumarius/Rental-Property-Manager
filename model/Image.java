package com.licenta.rentalpropertymanager.model;

import com.licenta.rentalpropertymanager.repository.ImageRepository;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//do not use @EqualsAndHashCode break Jackson annotation
@Entity
@Table(name = "image")
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private long imageId;

    private String imageName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "property_id")
    private Property property;










}
