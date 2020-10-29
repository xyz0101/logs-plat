package com.jenkin.log.enums;

/**
 * 归档类型枚举
 */
public enum LogInFileTypeEnum {

    DELETE("DELETE"),
    PACKAGE("PACKAGE");
    private String text;
    LogInFileTypeEnum(String text){
        this.text=text;
    }

}
