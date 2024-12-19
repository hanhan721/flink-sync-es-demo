package utils;

import org.apache.flink.api.java.utils.MultipleParameterTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterParseTool {
    private static final List<String> EMPTY_KEYS = Collections.singletonList("password");
    public static Map<String, String> getConfigMap(MultipleParameterTool params, String key) {
        if (!params.has(key)) {
            System.out.println(
                    "Can not find key ["
                            + key
                            + "] from args: "
                            + params.toMap().toString()
                            + ".\n");
            return null;
        }

        Map<String, String> map = new HashMap<>();
        for (String param : params.getMultiParameter(key)) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
                continue;
            } else if (kv.length == 1 && EMPTY_KEYS.contains(kv[0])) {
                map.put(kv[0], "");
                continue;
            }

            System.out.println("Invalid " + key + " " + param + ".\n");
            return null;
        }
        return map;
    }
}
