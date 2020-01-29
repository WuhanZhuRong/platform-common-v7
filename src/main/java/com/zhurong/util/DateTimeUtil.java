package com.zhurong.util;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeUtil.class);
    
    private static final String YMD = "yyyy-MM-dd";
    private static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";

    @Test
    public void test(){
        double a =14+1+33.8+28.48+11+37.9+13+14+3.06+7.60+24+620+61.55+26.32+75.51+13+18+1+14+15+199.6+13+15+1+65+ 2+9.9+20.7+13+49.9+129.02+68.5+20+2+20.60+2.01+2.32+14+13+15+19.9+23.9+3.05+42+7.5+19.2+13.5+13+15+15
                +15+80+16+520.5+121+520.5+16+18.4+33.6+22.3+59+97.22+0.3+14+15+15+15+56;
        System.out.println(a);
    }
    public static String parseDateToString(Date date) {
        if (null == date) return "-1";
        return new SimpleDateFormat(YMDHMS).format(date);
    }


    public static String parseDateToString(Date date, String pattern) {
        if (null == date) return "-1";
        if (StringUtils.isEmpty(pattern)) return parseDateToString(date);
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parseStringToDate(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) return null;
        return getDate(dateStr, YMD);
    }



    public static Date parseStringToDate(String dateStr, String pattern) {
        if (StringUtils.isEmpty(dateStr)) return null;
        return getDate(dateStr, pattern);
    }

    private static Date getDate(String dateStr, String ymd) {
        Date date = null;
        try {
            date = new SimpleDateFormat(ymd).parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("日期转换异常");
        }
        return date;
    }

    public static String getNextDay(String dateStr) {
        return getBeforeOrNext(dateStr, 1);
    }

    public static String getBeforeDay(String dateStr) {
        return getBeforeOrNext(dateStr, 0);
    }

    public static int getAge(Date birthday) {
        if (null == birthday) {
            return -1;
        }
        Calendar calendar = Calendar.getInstance();
        return CalculateTheAge(calendar, birthday);
    }

    public static int getAge(Date birthday, Date date) {
        if (null == birthday || null == date) {
            return -1;
        }
        // 获得指定时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return CalculateTheAge(calendar, birthday);
    }

    public static boolean isDateStr(String dateStr,String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        try {
            format.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private static int CalculateTheAge(Calendar calendar , Date birthday){
        if (calendar.before(birthday)) {
            // 生日在当前日期的后面
            return -1;
        }
        //获取指定的时间
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 获得生日时间
        calendar.setTime(birthday);
        int birthdayYear = calendar.get(Calendar.YEAR);
        int birthdayMonth = calendar.get(Calendar.MONTH) + 1;
        int birthdayDay = calendar.get(Calendar.DAY_OF_MONTH);
        int age = year - birthdayYear;
        if (month < birthdayMonth) {
            age--;
        }
        if (month == birthdayMonth && day<birthdayDay) {
            age--;
        }
        return age;
    }

    private static String getBeforeOrNext(String dateStr, int mark){
        if (StringUtils.isEmpty(dateStr)) {
            return "-1";
        }
        if (!isDateStr(dateStr,YMD)) {
            return "-1";
        }
        Date date = null;
        try {
            date = new SimpleDateFormat(YMD).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(mark == 0){//前一天
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        }else if (mark == 1){//后一天
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        }else{
            return null;
        }
        return new SimpleDateFormat(YMD).format(calendar.getTime());
    }

    public static void main(String[] args) {


    }
}
