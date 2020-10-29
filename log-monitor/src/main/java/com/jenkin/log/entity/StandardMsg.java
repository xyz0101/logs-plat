package com.jenkin.log.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author jenkin
 * @className StandardMsg
 * @description TODO
 * @date 2020/10/29 17:26
 */
@Data
public class StandardMsg {
    private Date timestamp = new Date();
    private String content;
}
