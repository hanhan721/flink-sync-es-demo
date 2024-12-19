
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import deserializer.ChangeEventDeserializer;
import es.client.EsClientSingleton;
import event.ChangeEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.connector.elasticsearch.sink.FlushBackoffType;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.HttpHost;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.RestHighLevelClient;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.StringUtils;
import strategy.SyncStrategy;
import strategy.SyncStrategyFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MySqlCdcUserIndexEsJob extends AbstractWideJob {

/*
* 配置参数
* --job-conf
parallelism=1
--job-conf
checkpoint-interval=1000
--mysql-conf
hostname=localhost
--mysql-conf
port=3306
--mysql-conf
username=root
--mysql-conf
password=root
--mysql-conf
database-name=test
--mysql-conf
start-up-mode=latest_offset
--mysql-conf
jdbc-url="jdbc:mysql://127.0.0.1:3306/test"
--sink-conf
es-host=127.0.0.1
--sink-conf
es-port=9200
--sink-conf
es-schema=http
*
* */
    public static void main(String[] args) throws Exception {
        MySqlCdcUserIndexEsJob job = new MySqlCdcUserIndexEsJob();
        job.runJob(args);
    }


    @Override
    public void runJob(String[] args) throws Exception {
        this.init(args);
        JobConfig jobConfig = super.getJobConfig();
        Map<String, String> mysqlConf = jobConfig.getMysqlConfMap();
        Map<String, String> jobConf = jobConfig.getJobConfMap();
        Map<String, String> sinkConf = jobConfig.getSinkConfMap();
        String mysqlDatabase = mysqlConf.get("database-name");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String parallelism = jobConf.get("parallelism");
        if (StringUtils.isNullOrWhitespaceOnly(parallelism)) {
            env.setParallelism(1);
        } else {
            env.setParallelism(Integer.parseInt(parallelism));
        }

        String checkpointConf = jobConf.get("checkpoint-interval");
        if (!StringUtils.isNullOrWhitespaceOnly(checkpointConf)) {
            env.enableCheckpointing(Long.parseLong(checkpointConf));
        }

        try {
            // 在Flink任务执行前创建 Elasticsearch 索引
           EsClientSingleton.createIndexIfNotExists("address","esIndex/addrIndex.json", sinkConf);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1); // 如果索引创建失败，停止程序
        }


        String[] tables = {"rsc_address"};
        String includeTables = Arrays.stream(tables)
                .map(table -> String.format("%s.%s", mysqlDatabase, table))
                .collect(Collectors.joining("|"));

        MySqlSource<ChangeEvent> sqlSource = MySqlSource.<ChangeEvent>builder()
                .hostname(mysqlConf.get("hostname"))
                .port(Integer.parseInt(mysqlConf.get("port")))
                .databaseList(mysqlConf.get("database-name"))
                .tableList(includeTables)
                .username(mysqlConf.get("username"))
                .password(mysqlConf.get("password"))
                .deserializer(new ChangeEventDeserializer<ChangeEvent>())
                .serverTimeZone("Asia/Shanghai")
                .startupOptions(getStartupOptions(mysqlConf))
                .build();

        DataStream<ChangeEvent> userStream = env.fromSource(sqlSource, WatermarkStrategy.noWatermarks(), "MySQL Users Source");

        Elasticsearch7SinkBuilder<ChangeEvent> sinkBuilder = new Elasticsearch7SinkBuilder<>();
        sinkBuilder.setBulkFlushMaxActions(1000);  // 每次最多1000条数据
        sinkBuilder.setBulkFlushMaxSizeMb(5);  // 每次最多5MB数据
        sinkBuilder.setBulkFlushInterval(2000);  // 每隔2秒就进行批量写入
        sinkBuilder.setBulkFlushBackoffStrategy(FlushBackoffType.EXPONENTIAL, 5, 1000);  // 增加重试策略

        sinkBuilder.setHosts(new HttpHost(sinkConf.get("es-host"), Integer.parseInt(sinkConf.get("es-port")), sinkConf.get("es-scheme")));
        sinkBuilder.setEmitter( (event, context, indexer) -> {
            try {
                RestHighLevelClient esClient = EsClientSingleton.getInstance(sinkConf);
                SyncStrategy strategy = SyncStrategyFactory.getStrategy(event.getTableName(), esClient, mysqlConf);
                if (strategy == null) {
                    return;
                }
                strategy.executeSync(event, indexer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        if (!StringUtils.isNullOrWhitespaceOnly(sinkConf.get("es-username"))){
            sinkBuilder.setConnectionUsername(sinkConf.get("es-username"));
        }
        if (!StringUtils.isNullOrWhitespaceOnly(sinkConf.get("es-password"))){
            sinkBuilder.setConnectionPassword(sinkConf.get("es-password"));
        }

        userStream.sinkTo(sinkBuilder.build());

        env.execute("Mysql CDC ES Job");
    }
}
