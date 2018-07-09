package mutong.com.mtaj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/9.
 */

public class DateUtil
{
    //将时间yyyy MM dd HH mm 转化为：yyyy年MM月dd日 hh:mm
    public static String convert2String(String time,int type)
    {
        if(!StringUtil.isEmpty(time) && time.length() == 12)
        {
            //先获取年
            String year = time.substring(0,4) + "年";
            //获取月
            String month = time.substring(4,6) + "月";
            //获取日
            String day = time.substring(6,8) + "日";
            //获取小时
            String hour = time.substring(8,10) + ":";
            //获取分钟
            String minute = time.substring(10,12);
            //获取年月日
            if (type == 0)
            {
                return year + month + day;
            }
            //获取时分
            if (type == 1)
            {
                return hour + minute;
            }
        }
        return "error";
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek(String datetime)
    {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
