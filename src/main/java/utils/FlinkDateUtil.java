package utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class FlinkDateUtil {
    //年月日
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    //时分秒
    public static final String FORMAT_TIME = "HH:mm:ss";
    //年月日时分秒
    public static final String FORMAT_DATE_TIME = FORMAT_DATE + " " + FORMAT_TIME;

    public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";


    public static String timestampToString(Long timestamp,String timeZone){
        if (String.valueOf(timestamp).length() == 16){
            //时间戳是毫秒
            return toDateTimeString(fromTimeMills(timestamp / 1000, timeZone),FORMAT_DATE_TIME);
        }else {
            //时间戳是秒
            return toDateTimeString(fromTimeMills(timestamp, timeZone),FORMAT_DATE_TIME);
        }
    }


    /**
     * 解析本地日期时间
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr,String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    public static LocalDateTime fromTimeMills(Long timestamp,String timeZone){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), StringUtils.isBlank(timeZone) ? ZoneId.of(DEFAULT_TIME_ZONE) : ZoneId.of(timeZone));
    }

    /**
     * 日期转字符串（日期+时间）

     */
    public static String toDateTimeString(LocalDateTime dateTime,String pattern){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(dateTime);
    }


    /**
     * 根据天数倒退日期（数据库的Date类型，读取binlog日志会被默认转换成Integer类型，代表了1970-01-01后的N天）
     */
    public static String getSomeDay(int days){
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
        Date date;
        try {
           date = sdf.parse("1970-01-01");
        }catch (ParseException e)  {
            throw new RuntimeException(e);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR,days);
        date = calendar.getTime();
        return sdf.format(date);
    }



    public static Date parseAndFormatDate(String dateStr,String pattern) {
        try {
            // 使用输入格式解析日期字符串
            SimpleDateFormat inputSdf = new SimpleDateFormat(pattern);
            Date parsedDate = inputSdf.parse(dateStr);

            // 使用输出格式格式化日期
            SimpleDateFormat outputSdf = new SimpleDateFormat(pattern);
            String formattedDateStr = outputSdf.format(parsedDate);

            // 再次解析格式化后的日期字符串
            return outputSdf.parse(formattedDateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
        }
    }

}
