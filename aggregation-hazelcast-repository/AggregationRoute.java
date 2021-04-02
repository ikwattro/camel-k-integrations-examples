// camel-k: dependency=camel-hazelcast
// camel-k: dependency=mvn:com.hazelcast:hazelcast-all:4.2

import org.apache.camel.BindToRegistry;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.apache.camel.processor.aggregate.hazelcast.HazelcastAggregationRepository;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;


public class AggregationRoute extends org.apache.camel.builder.RouteBuilder {

    @BindToRegistry("hzr")
    public HazelcastAggregationRepository hazelcastRepository() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().getKubernetesConfig().setEnabled(true)
                .setProperty("namespace", "default")
                .setProperty("service-name", "my-release-hazelcast");

        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        return new HazelcastAggregationRepository(
            "kamel-demo", hazelcastInstance
        );
    }

    @Override
    public void configure() throws Exception {

        from("knative:event/sourcecsv")
            .log("${body}")
            .unmarshal().json()
            .aggregate(new ArrayListAggregationStrategy())
                .jsonpath("$.entity_id").completionTimeout(1500).aggregationRepositoryRef("hzr")
        .log("${body}");

    }

    class ArrayListAggregationStrategy implements AggregationStrategy {

        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            Object newBody = newExchange.getIn().getBody();
            ArrayList<Object> list = null;
            if (oldExchange == null) {
                list = new ArrayList<Object>();
                list.add(newBody);
                newExchange.getIn().setBody(list);
                return newExchange;
            } else {
                list = oldExchange.getIn().getBody(ArrayList.class);
                if (list == null) {
                    return oldExchange;
                }
                list.add(newBody);
                return oldExchange;
            }
        }
    }
}