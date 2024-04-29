package com.csfrez.tool.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private String name;
    private Integer age;
    private String birthDay;

}
