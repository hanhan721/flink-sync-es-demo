package enums;

import java.util.Arrays;
import java.util.List;


public enum DateParseEnum {

    FORMAT_DATE("formatDate","yyyy-MM-dd",Arrays.asList("effDate", "expDate", "enterDate","cardCreateTime","ignoreDate","installDate")),
    FORMAT_DATE_TIME("formatDateTime","yyyy-MM-dd HH:mm:ss", Arrays.asList("createTime", "updateTime"));


    private String code;
    private String format;
    private List<String> field;

    DateParseEnum(String code, String format, List<String> field) {
        this.code = code;
        this.format = format;
        this.field = field;
    }

    public String getFormat() {
        return format;
    }

    public List<String> getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

    // 静态方法，根据格式字符串查找枚举实例
    public static DateParseEnum findByFormat(String code) {
        for (DateParseEnum dateParseEnum : values()) {
            if (dateParseEnum.getCode().equals(code)) {
                return dateParseEnum;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + code + "]");
    }
}

