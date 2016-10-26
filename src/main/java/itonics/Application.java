package itonics;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithmDescriptor;
import org.carrot2.elasticsearch.ClusteringAction;
import org.carrot2.elasticsearch.LogicalField;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Application {
    public static void main(String[] args) throws UnknownHostException, ExecutionException, InterruptedException {
        Client client = client();
        Map<String, Object> attrs = new HashMap<>();
        LingoClusteringAlgorithmDescriptor.attributeBuilder(attrs)
                .desiredClusterCountBase(5);

        ClusteringAction.ClusteringActionResponse result = new ClusteringAction.ClusteringActionRequestBuilder(client)
                .setQueryHint("data mining")
                .addFieldMapping("title", LogicalField.TITLE)
                .addFieldMapping("content", LogicalField.CONTENT)
                .addAttributes(attrs)
                .setSearchRequest(
                        client.prepareSearch()
                                .setIndices("test")
                                .setTypes("test")
                                .setSize(100)
                                .setQuery(QueryBuilders.termQuery("_all", "data"))
                                .addFields("title", "content"))
                .execute()
                .get();

        System.out.println(result);
    }

    private static Client client() throws UnknownHostException {
        return TransportClient
                .builder()
                .build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
    }
}
