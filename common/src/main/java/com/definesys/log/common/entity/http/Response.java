package com.definesys.log.common.entity.http;


/**
 * @author ：jenkin
 * @date ：Created at 2020/3/13 10:24
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class Response<T> {

    private String responseCode;

    private String msg;

    private T data;

    public Response(String responseCode, String msg, T data) {
        this.responseCode = responseCode;
        this.msg = msg;
        this.data = data;
    }
    public Response(String responseCode, String msg) {
        this.responseCode = responseCode;
        this.msg = msg;
    }
    public Response(){}

    public static  <T> Response<T> ok(){
        return new Response<T>("200","请求成功");
    }
    public  static  <T>Response<T> ok(T data){
        return new Response<T>("200","请求成功",data);
    }

    public  Response<T> data(T data){
        this.setData(data);
        return this;
    }

    public static Response error(){
        return new Response<Object>("500","请求错误");
    }
    public static Response error(String code,String  msg){
        return new Response<Object>(code,msg);
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
