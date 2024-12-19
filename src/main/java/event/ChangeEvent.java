package event;

import java.util.Map;


public class ChangeEvent {


    /**
     * 表名
     */
    private String tableName;
    /**
     * 操作类型
     */
    private String opType;
    /**
     * ES查询条件
     */
    private String esQueryCondition;
    /**
     * 变更前的数据
     */
    private Map<String, Object> before;
    /**
     * 变更后的数据
     */
    private Map<String, Object> after;

    public ChangeEvent(String tableName, String opType, Map<String, Object> before, Map<String, Object> after) {
        this.tableName = tableName;
        this.opType = opType;
        this.before = before;
        this.after = after;
    }

    public ChangeEvent() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Map<String, Object> getBefore() {
        return before;
    }

    public void setBefore(Map<String, Object> before) {
        this.before = before;
    }

    public Map<String, Object> getAfter() {
        return after;
    }

    public void setAfter(Map<String, Object> after) {
        this.after = after;
    }

    public String getEsQueryCondition() {
        return esQueryCondition;
    }

    public void setEsQueryCondition(String esQueryCondition) {
        this.esQueryCondition = esQueryCondition;
    }
}
