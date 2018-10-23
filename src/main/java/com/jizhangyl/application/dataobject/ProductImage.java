package com.jizhangyl.application.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ProductImage {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer productId;

    private String imageUrl;

    private Integer imageOrder;
}
