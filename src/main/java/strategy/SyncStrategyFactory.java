package strategy;

import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategy.impl.UserAddrSyncStrategy;

import java.io.IOException;
import java.util.Map;

public class SyncStrategyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SyncStrategyFactory.class);


    public static SyncStrategy getStrategy(String tableName, RestHighLevelClient esClient, Map<String,String> mysqlConf) throws IOException {

        LOG.info("MYSQL同步ES请求,tableName:{},mysqlConf:{}", tableName, mysqlConf);

        switch (tableName) {
            case "rsc_address":
                return UserAddrSyncStrategy.getInstance(esClient);
            default:
                return null;
        }


    }
}
