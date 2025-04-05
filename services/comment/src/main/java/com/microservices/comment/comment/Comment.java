package com.microservices.comment.comment;

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
public class Comment {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer reactCount;
    private String content;
    private String imageUrl;

    private Integer userId;
    private Integer postId;

}