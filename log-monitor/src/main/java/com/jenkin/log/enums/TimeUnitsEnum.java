package com.jenkin.log.enums;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间单位枚举
 */
public enum TimeUnitsEnum {

    YEAR("YEAR"),
    MONTH("MONTH"),
    DAY("DAY"),
    HOUR("HOUR"),
    MIN("MIN");
    private String text;
    TimeUnitsEnum(String text){
        this.text=text;
    }


    public String getText() {
        return text;
    }}
