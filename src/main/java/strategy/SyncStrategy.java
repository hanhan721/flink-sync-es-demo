package strategy;


import event.ChangeEvent;
import org.apache.flink.connector.elasticsearch.sink.RequestIndexer;



public interface SyncStrategy {

    void executeSync(ChangeEvent event, RequestIndexer indexer) throws Exception;

}
