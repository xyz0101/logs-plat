package com.jenkin.log.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static com.jenkin.log.enums.TimeUnitsEnum.*;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/4 16:37
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class IndexUtil {
    private static final ConcurrentHashMap<String,String> INDEX_NAMES = new ConcurrentHashMap<>();
    /**
     *
     * @param suffix 后缀
     * @param logInFileTime 归档时间间隔
     * @param timeUnit 归档时间单位
     * @param systemKey 系统标志，topic^partitionkKey
     * @return
     */
    public static String getNewIndexName(String suffix,int logInFileTime,String timeUnit,String systemKey){
        try {
            //获取当前的归档时间
            String timeStr = getTimeStr(timeUnit, logInFileTime);
            String[] split = systemKey.split("\\^");
            String sysKey = split[0] + "_" + split[1]+"_";
            String name = sysKey + suffix+"_" + timeStr;
//            如果还没有达到下一次归档的时间就会返回null
            if(timeStr==null){
                 name = INDEX_NAMES.get(systemKey);
//                 如果缓存的上一次时间为空，说明是第一次创建索引，需要返回初始的所有名称
                if (name == null) {
                    //获取当前的归档时间
                    timeStr =getCurrentTimeStr(timeUnit);
                    name = sysKey + suffix+"_" + timeStr;
                    name=name.toLowerCase();
                    //缓存
                    INDEX_NAMES.put(systemKey,name);
                }
                return name;
            }else{
                name = name.toLowerCase();
                INDEX_NAMES.put(systemKey,name);
                return name;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return INDEX_NAMES.get(systemKey);

    }

    /**
     * 获取归档时间
     * @param text 归档类型
     * @param logInFileTime 时间间隔
     * @return
     * @throws ParseException
     */
    public static String getTimeStr(String text,int logInFileTime) throws ParseException {
        Date date = new Date();
        //设置转换的日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //开始时间
        Date startDate = sdf.parse("2000-01-01 00:00:00");

        if(YEAR.getText().equals(text)){
            //得到相差的年数
            long betweenDate = (new Date().getTime() - startDate.getTime())/(60*60*24*1000*(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)));
            if (betweenDate%logInFileTime==0) {
                sdf = new SimpleDateFormat("yyyy");
                return sdf.format(date);
            }
        }else  if(MONTH.getText().equals(text)){
            //得到相差的月数
            int monthDiff = getMonthDiff(startDate, new Date());
            if (monthDiff%logInFileTime==0) {
                sdf = new SimpleDateFormat("yyyy_MM");
                return sdf.format(date);
            }
        }else  if(DAY.getText().equals(text)){
            //得到相差的天数 betweenDate
            long betweenDate = (new Date().getTime() - startDate.getTime())/(60*60*24*1000);
            if (betweenDate%logInFileTime==0) {
                sdf = new SimpleDateFormat("yyyy_MM_dd");
                return sdf.format(date);
            }
        }else  if(HOUR.getText().equals(text)){
            long betweenDate = (new Date().getTime() - startDate.getTime())/(60*60*24);
            if (betweenDate%logInFileTime==0) {
                sdf = new SimpleDateFormat("yyyy_MM_dd_HH");
                return sdf.format(date);
            }
        }else  if(MIN.getText().equals(text)){
            long betweenDate = (new Date().getTime() - startDate.getTime())/(60*60);
            if (betweenDate%logInFileTime==0) {
                sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                return sdf.format(date);
            }
        }
        return null;
    }

    /**
     * 获取当前时间的归档时间
     * @param text
     * @return
     * @throws ParseException
     */
    public static String getCurrentTimeStr(String text) throws ParseException {
        Date date = new Date();
        //设置转换的日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //开始时间
        Date startDate = sdf.parse("2000-01-01 00:00:00");

        if(YEAR.getText().equals(text)){

                sdf = new SimpleDateFormat("yyyy");
                return sdf.format(date);

        }else  if(MONTH.getText().equals(text)){

                sdf = new SimpleDateFormat("yyyy_MM");
                return sdf.format(date);

        }else  if(DAY.getText().equals(text)){

                sdf = new SimpleDateFormat("yyyy_MM_dd");
                return sdf.format(date);

        }else  if(HOUR.getText().equals(text)){

                sdf = new SimpleDateFormat("yyyy_MM_dd_HH");
                return sdf.format(date);

        }else  if(MIN.getText().equals(text)){

                sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                return sdf.format(date);

        }
        return null;
    }
    /**
     * 获取两个日期相差的月数
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) {
            monthInterval--;
        }
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }


}
