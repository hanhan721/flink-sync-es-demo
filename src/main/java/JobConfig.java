import java.io.Serializable;
import java.util.Map;

public class JobConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, String> mysqlConfMap;
    private Map<String, String> tableConfMap;
    private Map<String, String> sinkConfMap;
    private Map<String, String> jobConfMap;

    public JobConfig() {
    }

    public Map<String, String> getMysqlConfMap() {
        return mysqlConfMap;
    }

    public void setMysqlConfMap(Map<String, String> mysqlConfMap) {
        this.mysqlConfMap = mysqlConfMap;
    }

    public Map<String, String> getTableConfMap() {
        return tableConfMap;
    }

    public void setTableConfMap(Map<String, String> tableConfMap) {
        this.tableConfMap = tableConfMap;
    }

    public Map<String, String> getSinkConfMap() {
        return sinkConfMap;
    }

    public void setSinkConfMap(Map<String, String> sinkConfMap) {
        this.sinkConfMap = sinkConfMap;
    }

    public Map<String, String> getJobConfMap() {
        return jobConfMap;
    }

    public void setJobConfMap(Map<String, String> jobConfMap) {
        this.jobConfMap = jobConfMap;
    }
}
