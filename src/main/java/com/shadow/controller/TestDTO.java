package com.shadow.controller;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TestDTO {

    private String name;

    private int age;
}
