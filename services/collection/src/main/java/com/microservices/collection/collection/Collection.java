package com.microservices.collection.collection;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Collection {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String type;
    private String resourceUrl;

    private Integer topicId;
}