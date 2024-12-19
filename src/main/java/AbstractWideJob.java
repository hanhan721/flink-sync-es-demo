import com.ververica.cdc.connectors.mysql.source.offset.BinlogOffset;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.util.Preconditions;
import utils.ParameterParseTool;

import java.util.Arrays;
import java.util.Map;

public abstract class AbstractWideJob {

    private String includingTables;
    private JobConfig jobConfig = new JobConfig();

    public void init(String[] args){
        System.out.println("Input args: " + Arrays.asList(args) + ".\n");
        //String operation = args[0].toLowerCase();
        String[] opArgs = Arrays.copyOfRange(args, 0, args.length);
        MultipleParameterTool params = MultipleParameterTool.fromArgs(opArgs);
        Preconditions.checkArgument(params.has("mysql-conf"));
        Map<String, String> mysqlMap = ParameterParseTool.getConfigMap(params, "mysql-conf");

        Preconditions.checkArgument(params.has("sink-conf"));
        Map<String, String> sinkMap = ParameterParseTool.getConfigMap(params, "sink-conf");
        Map<String, String> tableMap = ParameterParseTool.getConfigMap(params, "table-conf");
        Map<String, String> jobMap = ParameterParseTool.getConfigMap(params, "job-conf");

        this.jobConfig.setMysqlConfMap(mysqlMap);
        this.jobConfig.setSinkConfMap(sinkMap);
        this.jobConfig.setTableConfMap(tableMap);
        this.jobConfig.setJobConfMap(jobMap);
        String database = this.jobConfig.getMysqlConfMap().get("database-name");
        //this.includingTables = String.format("%s.rsc_factory|%s.rsc_supply_meter|%s.rsc_meter_info", database, database, database);
    }

    public StartupOptions getStartupOptions(Map<String, String> mysqlConf){
        String startupMode = mysqlConf.get("start-up-mode");

        switch (startupMode) {
            case "initial":
                return StartupOptions.initial();
            case "earliest_offset":
                return StartupOptions.earliest();
            case "latest_offset":
                return StartupOptions.latest();
            case "specific_offsets":
                // TODO
                String file = mysqlConf.get("start_up_specific-offset_file");
                String pos = mysqlConf.get("start_up_specific-offset_pos");
                return StartupOptions.specificOffset(BinlogOffset.ofBinlogFilePosition(file, Long.parseLong(pos)));
            case "timestamp":
                // TODO
                return StartupOptions.timestamp(Long.parseLong(mysqlConf.get("binlog-ts")) * 1000L);
            default:
                System.out.println("Unknown startupOptions:  " + startupMode);
                System.exit(1);
        }
        return null;
    }

    public abstract void runJob(String[] args) throws Exception;

    public JobConfig getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }
}
