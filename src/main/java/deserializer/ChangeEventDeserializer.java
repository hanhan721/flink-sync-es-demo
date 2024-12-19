package deserializer;

import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Field;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Schema;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Struct;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import enums.DateParseEnum;
import enums.SqlOperateEnum;
import event.ChangeEvent;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import utils.FlinkDateUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class ChangeEventDeserializer<T> implements DebeziumDeserializationSchema<ChangeEvent> {


    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<ChangeEvent> collector) throws Exception {
        // 创建 ChangeEvent 对象
        ChangeEvent changeEvent = new ChangeEvent();

        // 从 SourceRecord 中提取表名
        String tableName = sourceRecord.topic().split("\\.")[2];  // 从 topic 中解析表名
        changeEvent.setTableName(tableName);

        // 提取操作类型 (INSERT, UPDATE, DELETE)
        Struct value = (Struct) sourceRecord.value();
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);

        if (operation == Envelope.Operation.READ || operation == Envelope.Operation.CREATE || operation == Envelope.Operation.UPDATE) {
            if (operation == Envelope.Operation.READ){
                operation = Envelope.Operation.CREATE;
            }

            changeEvent.setOpType(SqlOperateEnum.getByOriginalOperation(operation.name()));
            Struct after = value.getStruct("after");
            Map<String, Object> afterData = extractFieldData(after);
            changeEvent.setAfter(afterData);

            changeEvent.setOpType(SqlOperateEnum.getByOriginalOperation(operation.name()));
            Struct before = value.getStruct("before");
            Map<String, Object> beforeData = extractFieldData(before);
            changeEvent.setBefore(beforeData);

            if (operation == Envelope.Operation.UPDATE){
                Integer deleted = (Integer) afterData.get("isDeleted");
                if (deleted == 1){
                    changeEvent.setOpType(SqlOperateEnum.getByOriginalOperation(Envelope.Operation.DELETE.name()));
                    changeEvent.setBefore(afterData);
                    changeEvent.setAfter(null);
                }

                if ((Integer) beforeData.get("isDeleted") == 1 && (Integer) afterData.get("isDeleted") == 0 ){
                    changeEvent.setOpType(SqlOperateEnum.getByOriginalOperation(Envelope.Operation.CREATE.name()));
                    changeEvent.setBefore(null);
                    changeEvent.setAfter(afterData);
                }
            }
        }

        if (operation == Envelope.Operation.DELETE) {
            changeEvent.setOpType(SqlOperateEnum.getByOriginalOperation(operation.name()));
            Struct before = value.getStruct("before");
            Map<String, Object> beforeData = extractFieldData(before);
            changeEvent.setBefore(beforeData);
        }

        // 将 changeEvent 收集到下游
        collector.collect(changeEvent);
    }

    // 提取 Struct 中的字段和对应的值
    private Map<String, Object> extractFieldData(Struct struct) {
        Map<String, Object> fieldData = new HashMap<>();
        if (struct != null) {
            Schema schema = struct.schema();
            for (Field field : schema.fields()) {
                Object value = struct.get(field);

                // 处理可能为 BigDecimal 类型的字段
                if (value instanceof BigDecimal && field.name().equals("id")) {
                    BigDecimal bigDecimalValue = (BigDecimal) value;
                    try {
                        // 转换 BigDecimal 为 long，假设 BigDecimal 不会超出 long 范围
                        long longValue = bigDecimalValue.longValueExact();
                        fieldData.put(field.name(), longValue);
                    } catch (ArithmeticException e) {
                        // 处理超出 long 范围的情况
                        fieldData.put(field.name(), bigDecimalValue.toBigInteger());
                    }
                } else {
                    fieldData.put(field.name(), value);
                }

            }
        }

        // 将字段转换为驼峰命名
        Map<String, Object> camelCase = convertKeysToCamelCase(fieldData);

        //时间类型格式化处理
        return convertMapToFormatDate(camelCase);
    }

    public static Map<String,Object> convertMapToFormatDate(Map<String, Object> sourceMap) {

        HashMap<String, Object> targetMap = new HashMap<>();
        sourceMap.forEach((k,v)->{
            if (DateParseEnum.FORMAT_DATE.getField().contains(k) && v instanceof Integer){
                String date = FlinkDateUtil.getSomeDay((Integer) v);
                v = FlinkDateUtil.parseAndFormatDate(date, FlinkDateUtil.FORMAT_DATE);
            }else if (DateParseEnum.FORMAT_DATE_TIME.getField().contains(k) && v instanceof Long){
                String date = FlinkDateUtil.timestampToString((Long) v, "");
                v = FlinkDateUtil.parseAndFormatDate(date, FlinkDateUtil.FORMAT_DATE_TIME);
            }
            targetMap.put(k,v);
        });

        return targetMap;
    }


    public static Map<String, Object> convertKeysToCamelCase(Map<String, Object> originalMap) {
        Map<String, Object> resultMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            String camelCaseKey = toCamelCase(entry.getKey());
            resultMap.put(camelCaseKey, entry.getValue());
        }

        return resultMap;
    }

    // 将字符串从下划线命名转换为驼峰命名
    private static String toCamelCase(String underscoreKey) {
        StringBuilder result = new StringBuilder();
        boolean toUpperCase = false;

        for (int i = 0; i < underscoreKey.length(); i++) {
            char currentChar = underscoreKey.charAt(i);
            if (currentChar == '_') {
                toUpperCase = true;  // 下划线后的字符应大写
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(currentChar));
                    toUpperCase = false;
                } else {
                    result.append(currentChar);
                }
            }
        }

        return result.toString();
    }

    @Override
    public TypeInformation<ChangeEvent> getProducedType() {
        return TypeInformation.of(ChangeEvent.class);
    }
}
