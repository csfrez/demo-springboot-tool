package com.csfrez.tool.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDO implements Serializable {

    private String name;
    private Integer age;
//    private String address;
    private Date birthDay;

}
