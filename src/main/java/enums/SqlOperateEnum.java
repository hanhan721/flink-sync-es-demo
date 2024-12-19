package enums;

import io.debezium.data.Envelope;
import org.apache.flink.util.StringUtils;


public enum SqlOperateEnum {

    INSERT("INSERT", Envelope.Operation.CREATE.name()),
    UPDATE("UPDATE", Envelope.Operation.UPDATE.name()),
    DELETE("DELETE", Envelope.Operation.DELETE.name()),
    ;

    private String operate;

    private String originalOperation;


    SqlOperateEnum(String operate, String originalOperation) {
        this.operate = operate;
        this.originalOperation = originalOperation;
    }

    public String getOperate() {
        return operate;
    }

    public static String getByOriginalOperation(String originalOperation) {
        if (StringUtils.isNullOrWhitespaceOnly(originalOperation)) {
            return null;
        }
        for (SqlOperateEnum sqlOperateEnum : SqlOperateEnum.values()) {
            if (sqlOperateEnum.originalOperation.equals(originalOperation)) {
                return sqlOperateEnum.operate;
            }
        }
        return null;
    }
}
