package com.mybank.pc.kits;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.kits]
 * 类名称:    [DateKit]
 * 类描述:    [日期处理工具类]
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/10]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class DateKit {

    private DateKit(){

    }

    public static final String STR_DATEFORMATE = "yyyy-MM-dd HH:mm:ss";


    public static TimeZone TIMEZONE = new SimpleTimeZone(8 * 60 * 60 * 1000, "BEIJING");
    /**
     * 年-月-日
     */
    public static final String yyyy_MM_dd="yyyy-MM-dd";

    /**
     * 年月日时分秒毫秒
     */
    public static final String yyyyMMddHHmmssSSS="yyyyMMddHHmmssSSS";
    /**
     * 年月日时分秒
     */
    public static final String yyyyMMddHHmmss="yyyyMMddHHmmss";

    /***
     * 日志专用
     */
    public static final String format4Login="yyyy-MM-dd HH:mm:ss.SSS"; //代码121

    /**
     * 年月日
     */
    public static final String yyyyMMdd="yyyyMMdd";


    /**
     * 将日期转为字符串
     * @param date:		要转换的日期
     * @param format:	格式串
     * @return
     */
    public static String dateToStr(Date date, String format) {
        if(date==null)return "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将字符串转为日期
     * @param dateStr:	要转化的字符串
     * @param format:	格式串
     * @return
     */
    public static Date strToDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date parse=null;
        try {
            parse= sdf.parse(dateStr);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return parse;
    }



    public static int getNowUTC(){
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(new SimpleTimeZone(8 * 60 * 60 * 1000, "BEIJING"));
        c.setTime(d);
        int utc = (int)(c.getTimeInMillis()/1000);
        return utc;
    }


    public static String getNowTimeStr(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TIMEZONE);
        Long time = (long)getNowUTC()*1000;
        Date d = new Date(time);
        return sdf.format(d);
    }

    /**
     * 前一天
     * @param date
     * @return
     */
    public static Date getBeforeDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 当天
     * @param date
     * @return
     */
    public static Date getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        date = calendar.getTime();
        return date;
    }

    /**
     * 下一天
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 时间戳格式的 比较开始时间
     * @param date
     * @return
     */
    public static String getTimeStampBegin(Date date){
        String date_str_b=dateToStr(date,DateKit.yyyy_MM_dd)+" 00:00:00.000";
        return date_str_b;
    }

    /**
     * 时间戳格式的 比较结束时间
     * @param date
     * @return
     */
    public static String getTimeStampEnd(Date date){
        String date_str_e=dateToStr(date,DateKit.yyyy_MM_dd)+" 23:59:59.999";
        return date_str_e;
    }


    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate,Date bdate) throws ParseException
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        smdate=sdf.parse(sdf.format(smdate));
        bdate=sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     *字符串的日期格式的计算
     */
    public static int daysBetween(String smdate,String bdate) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 根据日期,加天数
     * @param days
     * @param dates
     * @return
     */
    public static Date addDays(int days,Date ...dates){
        Date date=new Date();
        if(dates.length>0)
            date=dates[0];
        Calendar calendar= new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,days);
        return date=calendar.getTime();
    }

    /**
     * 年份向前推
     * @param i 向前推几年
     * @param date  计算基础日期，默认是当天
     * @return
     */
    public static Date agoYear(int i,Date date){
        if(date==null)date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,0-i);
        return calendar.getTime();
    }

    /**
     * 年份向后推
     * @param i 向后推几年
     * @param date  计算基础日期，默认是当天
     * @return
     */
    public static Date afterYear(int i,Date date){
        if(date==null)date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,i);
        return calendar.getTime();
    }
    /**
     * 获取某年第一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(Date year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(year);
        int i=calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, i);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(Date year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(year);
        int i=calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, i);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();

        return currYearLast;
    }

    /**
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        System.out.println(dateToStr(getYearLast(agoYear(10,null)),yyyy_MM_dd));
    }

}
