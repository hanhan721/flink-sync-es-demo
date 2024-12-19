package strategy.impl;

import enums.FlinkTableConstant;
import event.ChangeEvent;
import es.entity.EsAddressEntity;
import org.apache.flink.connector.elasticsearch.sink.RequestIndexer;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.action.delete.DeleteRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.action.index.IndexRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.action.index.IndexResponse;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.action.update.UpdateRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.RequestOptions;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.RestHighLevelClient;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategy.SyncStrategy;
import utils.JacksonUtils;

import java.io.IOException;
import java.util.Map;


public class UserAddrSyncStrategy implements SyncStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(UserAddrSyncStrategy.class);


    private static final UserAddrSyncStrategy INSTANCE = new UserAddrSyncStrategy();


    private RestHighLevelClient esClient;

    private UserAddrSyncStrategy() {
        this.esClient = null;
    }


    public static UserAddrSyncStrategy getInstance(RestHighLevelClient esClient) {
        INSTANCE.setEsClient(esClient);
        return INSTANCE;
    }

    private void setEsClient(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public void executeSync(ChangeEvent event, RequestIndexer indexer) throws Exception {
        LOG.info("用户地址数据同步,event:{}", JacksonUtils.toJson(event));
        //根据操作类型执行不同的操作
        switch (event.getOpType()) {
            case FlinkTableConstant.INSERT_SQL:
                //插入操作
                handleAddrInsert(event, indexer);
                break;
            case FlinkTableConstant.UPDATE_SQL:
                //更新操作
                handleAddrUpdate(event, indexer);
                break;
            case FlinkTableConstant.DELETE_SQL:
                //删除操作
                handleAddrDelete(event, indexer);
                break;
            default:
                break;
        }
    }

    /**
     * 处理用户地址删除
     * @param event   事件
     * @param indexer 索引
     */
    private void handleAddrDelete(ChangeEvent event, RequestIndexer indexer) {
        //处理用户地址删除，用户地址无法删除，如果要删除，就是直接用户删除，用户同步会直接删除索引，地址不需要做任何处理


        executeAddressEsDelete(event, indexer);
    }

    /**
     * 处理用户地址更新
     * @param event   事件
     * @param indexer 索引
     */
    private void handleAddrUpdate(ChangeEvent event, RequestIndexer indexer) throws IOException {
        //处理用户地址修改
        Map<String, Object> after = event.getAfter();
        long addId = (long) after.get("id");

        //更新地址信息
        executeAddressEsUpdate(event, indexer);
    }





    /**
     * 处理地址新增
     * @param event   事件
     * @param indexer 索引
     */
    private void handleAddrInsert(ChangeEvent event, RequestIndexer indexer) throws IOException{
        //用户地址没有地址新增场景，直接返回

        //地址信息新增ES
        executeAddressEsInsert(event);
    }

    private void executeAddressEsInsert(ChangeEvent event) throws IOException {
        LOG.info("地址【新增】数据同步开始,event:{}",JacksonUtils.toJson(event));
        EsAddressEntity esAddressEntity = JacksonUtils.toClass(event.getAfter(), EsAddressEntity.class);

        LOG.info("新增地址索引,address:{}",JacksonUtils.toJson(esAddressEntity));
        IndexRequest indexRequest = new IndexRequest("tcis_address")
                .id(String.valueOf(esAddressEntity.getId()))
                .source(JacksonUtils.toJson(esAddressEntity),XContentType.JSON);
        IndexResponse index = esClient.index(indexRequest, RequestOptions.DEFAULT);
        LOG.info("新增地址索引,resp:{}",JacksonUtils.toJson(index));
    }

    private void executeAddressEsUpdate(ChangeEvent event, RequestIndexer indexer) {
        LOG.info("地址【更新】数据同步开始,event:{}",JacksonUtils.toJson(event));
        EsAddressEntity esAddressEntity = JacksonUtils.toClass(event.getAfter(), EsAddressEntity.class);

        UpdateRequest updateRequest = new UpdateRequest("tcis_address", String.valueOf(esAddressEntity.getId()))
                .doc(JacksonUtils.toJson(esAddressEntity), XContentType.JSON);
        indexer.add(updateRequest);
    }

    private void executeAddressEsDelete(ChangeEvent event, RequestIndexer indexer) {
        LOG.info("地址【删除】数据同步开始,event:{}",JacksonUtils.toJson(event));
        long id = (long) event.getBefore().get("id");
        DeleteRequest updateRequest = new DeleteRequest("tcis_address", String.valueOf(id));
        indexer.add(updateRequest);
    }
}
