package com.jizhangyl.application.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jizhangyl.application.enums.DateEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨贤达
 * @date 2018/8/23 17:45
 * @description
 */
@Slf4j
public class DateUtil {

    public static Date add(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    public static Date add(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    /**
     * 获取某一天开始的时间
     * 例:
     * 1. 获取今天(2018-10-05)开始的时间 --> getStart(0) --> 2018-10-05 00:00:00
     * 2. 获取明天(2018-10-06)开始的时间 --> getStart(1) --> 2018-10-06 00:00:00
     * @param amount 增量
     * @return
     */
    public static Date getStart(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, amount);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @deprecated Use {{@link #getStart(int)}} instead.
     * @see #getStart(int)
     * 获取某一天结束的时间
     * @param amount 增量
     * @return
     */
    @Deprecated
    public static Date getEnd(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, amount);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
    
    /**
     * 计算某个月开始时间
     * @param amount 1下个月，-1上个月
     * @return
     */
    public static Date getSupportBeginDayofMonth(int amount) {
    	Calendar startDate = Calendar.getInstance();
    	startDate.setTime(new Date());
    	startDate.add(Calendar.MONTH, amount); //月份加减
        
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        return  startDate.getTime();
    }
    
    /**
     * 计算某个月结束时间
     * 
     * 建议使用getSupportBeginDayofMonth(amount+1) 代替 getSupportEndDayofMonth(0),解释同上getEnd(int amount)
     * 
     * @param amount 1下个月，-1上个月
     * @return
     */
    public static Date getSupportEndDayofMonth(int amount){
    	Calendar endDate = Calendar.getInstance();
    	endDate.setTime(new Date());
    	endDate.add(Calendar.MONTH, amount); //月份加减
    	
    	endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
    	endDate.set(Calendar.HOUR_OF_DAY, 23);
    	endDate.set(Calendar.MINUTE, 59);
	    endDate.set(Calendar.SECOND, 59);
	    endDate.set(Calendar.MILLISECOND, 999);
	    return  endDate.getTime();
    }
    
    /**
     * 获取前一个月份的字符串表现形式
     * 例:
     * 本月2018-10
     * @return "2018-09"
     */
    public static String getFrontMonth(){
    	Calendar endDate = Calendar.getInstance();
    	endDate.setTime(new Date());
    	endDate.add(Calendar.MONTH, -1); //月份减
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
    	String dateString = formatter.format(endDate.getTime());
    	return dateString;
    }


    /**
     * 对字符串格式的日期s加n天
     * @param s yyyy-MM-dd HH:mm:ss 格式的日期字符串
     * @param n 待加天数
     * @return 日期相加后的新日期字符串
     */
    public static String addDay(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);//增加一天

            return sdf.format(cd.getTime());
        } catch(ParseException e) {
            throw new GlobalException(ResultEnum.QUERY_DATE_FORMAT_ERROR);
        }
    }
    
    /**
     * 获取当天零时零分零秒的日期
     * @return
     */
    public static Date initDateByDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * 
     * String 转 Date(yyyy-MM-dd HH:mm:ss)
     * @param time
     * @return
     */
    public static Date StringToDate(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dd = null;
        try {
			dd = sdf.parse(time);
		} catch (ParseException e) {
			return null;
		}
    	return dd;
    }
    
    /**
     * date转string(yyyy-MM-dd HH:mm:ss)
     * @param time
     * @return
     */
    public static String dateToString(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(time == null){
        	return "";
        }
    	return sdf.format(time);
    }
    
    /**
     * 获取time所处的时间段:上午,中午,下午,傍晚,晚上,凌晨,清晨
     * 注:判断基准为24小时制
     * @param time yyyy-MM-dd HH:mm:ss格式,若是date类型可以使用dateToString(Date time)方法进行转换
     * @return
     */
    public static DateEnum getStatus(String time) {
        String[] strs = time.split(" ");
        String tm = strs[1];
        tm = tm.substring(0, 2);
        int tmNum = 0;
        try {
            tmNum = Integer.parseInt(tm);
        } catch (NumberFormatException e) {
            log.info("【获取时间所处时段:解析小时的时候出错】");
            return DateEnum.UNKNOW;
        }
        
        if (tmNum >= 5 && tmNum <= 8) { // 早晨
            return DateEnum.NEAR_MONRING;
        } else if (tmNum > 8 && tmNum <= 12) { // 上午
            return DateEnum.MORNING;
        } else if (tmNum > 12 && tmNum <= 14) { // 中午
            return DateEnum.NOON;
        } else if (tmNum > 14 && tmNum <= 18) { // 下午
            return DateEnum.AFTERNOON;
        } else if (tmNum > 18 && tmNum <= 19) { // 傍晚
            return DateEnum.EVENING;
        } else if (tmNum > 19 && tmNum <= 23) { // 晚上
            return DateEnum.NIGHT;
        } else if (tmNum > 23 || tmNum <= 2) { // 深夜
            return DateEnum.DEEP_NIGHT;
        } else if (tmNum > 2 && tmNum < 5) { // 凌晨
            return DateEnum.BEFORE_DOWN;
        }
        return DateEnum.UNKNOW;
    }
    
    public static void main(String[] args) {
        Date sDate = DateUtil.getStart(-7);
        Date eDate = DateUtil.getStart(0);
        
        String sDateStr = DateUtil.dateToString(sDate);
        String eDateStr = DateUtil.dateToString(eDate);
        
        System.out.println("开始时间:" + sDateStr);
        System.out.println("结束时间:" + eDateStr);
        
    }
}
