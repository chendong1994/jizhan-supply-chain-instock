package com.jizhangyl.application.other;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author 曲健磊
 * @date 2018年9月20日 上午10:05:10
 * @description 测试BigDecimal
 */
public class BigDecimalTest {

	public static void main(String[] args) {
		BigDecimal a = new BigDecimal(2880);
		System.out.println(formatToNumber(a));
		String a1 = "2880.00";
		BigDecimal b = new BigDecimal(a1);
		
	}
	
	public static String formatToNumber(BigDecimal obj) {
        DecimalFormat df = new DecimalFormat("#.00");
        if(obj.compareTo(BigDecimal.ZERO)==0) {
            return "0.00";
        }else if(obj.compareTo(BigDecimal.ZERO)>0&&obj.compareTo(new BigDecimal(1))<0){
            return "0"+df.format(obj).toString();
        }else {
            return df.format(obj).toString();
        }
    }
}
