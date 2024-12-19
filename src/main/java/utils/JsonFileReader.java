package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class JsonFileReader {
    public static String readJsonFile(String filePath) {
        String result = "";
        try {
            // 使用 ClassLoader 获取资源文件
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(filePath);

            if (inputStream == null) {
                throw new IllegalArgumentException("文件没有找到: " + filePath);
            }

            // 读取文件内容并转换为字符串
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
