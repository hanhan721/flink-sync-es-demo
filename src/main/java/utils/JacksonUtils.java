package utils;


import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;


public class JacksonUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static final ObjectMapper objectMapperWithoutNulls = new ObjectMapper();

    static {
        objectMapperWithoutNulls.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    public static <T> T toClass(Map<String, Object> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 转换为不带 null  JSON
     */
    public static String toJsonWithoutNulls(Object obj) {
        try {
            return objectMapperWithoutNulls.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T mergeObjects(T before, Object update,Class<T> clazz) throws Exception {

        // 将 update 对象序列化为 JSON 树
        JsonNode updateNode = objectMapper.valueToTree(update);

        // 使用 JSON 数据更新 before 对象
        objectMapper.readerForUpdating(before).readValue(updateNode);

        return before;
    }


}
