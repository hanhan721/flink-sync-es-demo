package es.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.HttpHost;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.auth.AuthScope;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.RequestOptions;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.RestClient;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.RestHighLevelClient;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.indices.CreateIndexRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.indices.CreateIndexResponse;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.indices.GetIndexRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.common.xcontent.XContentType;
import utils.JsonFileReader;

import java.io.IOException;
import java.util.Map;


public class EsClientSingleton {
    private static volatile RestHighLevelClient client;

    private EsClientSingleton() {
        // private constructor to prevent instantiation
    }

    public static RestHighLevelClient getInstance(Map<String, String> sinkConf) {
        if (client == null) {
            synchronized (EsClientSingleton.class) {
                if (client == null) {
                    client = new RestHighLevelClient(
                            RestClient.builder(new HttpHost(sinkConf.get("es-host"), Integer.parseInt(sinkConf.get("es-port")), sinkConf.get("es-scheme")))
                                    .setRequestConfigCallback(
                                            requestConfigBuilder -> requestConfigBuilder
                                                    .setConnectTimeout(5000)
                                                    .setSocketTimeout(60000)
                                    )
                                    .setHttpClientConfigCallback(
                                            httpClientBuilder -> {
                                                HttpAsyncClientBuilder httpAsyncClientBuilder = httpClientBuilder
                                                        .setMaxConnTotal(100)
                                                        .setMaxConnPerRoute(100);
                                                if (StringUtils.isNotBlank(sinkConf.get("es-username")) && StringUtils.isNotBlank(sinkConf.get("es-password"))) {
                                                    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                                                    credentialsProvider.setCredentials(AuthScope.ANY,
                                                            new UsernamePasswordCredentials(sinkConf.get("es-username"), sinkConf.get("es-password")));
                                                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

                                                }

                                                return httpAsyncClientBuilder;
                                            }

                                    )
                    );
                }
            }
        }
        return client;
    }

    public static void close() throws IOException {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    public static void createIndexIfNotExists(String indexName, String filePath, Map<String, String> sinkConf) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean indexExists = getInstance(sinkConf).indices().exists(getIndexRequest, RequestOptions.DEFAULT);

        if (!indexExists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            // Define index mappings and settings here
            String mapping = JsonFileReader.readJsonFile(filePath);
            createIndexRequest.source(mapping, XContentType.JSON);

            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            System.out.println("Index created: " + createIndexResponse.index());
        } else {
            System.out.println("Index already exists.");
        }
    }
}
