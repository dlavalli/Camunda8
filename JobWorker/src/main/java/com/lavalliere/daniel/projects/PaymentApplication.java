package com.lavalliere.daniel.projects;

import com.lavalliere.daniel.projects.handler.CreditCardServiceHandler;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import org.apache.commons.configuration2.PropertiesConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class PaymentApplication {

    // Fill these values according to provided camunda account
    private final static String ZEEBE_ADDRESS = "[ZEEBE_ADDRESS]";
    private final static String ZEEBE_CLIENT_ID = "[ZEEBE_CLIENT_ID]";
    private final static String ZEEBE_CLIENT_SECRET = "[ZEEBE_CLIENT_SECRET]";
    private final static String ZEEBE_AUTHORIZATION_SERVER_URL = "[ZEEBE_AUTHORIZATION_SERVER_URL]";
    private final static String ZEEBE_TOKEN_AUDIENCE = "[ZEEBE_TOKEN_AUDIENCE]";

    private final static String ZEEBE_GRPC_ADDRESS = "http://127.0.0.1:26500";
    private final static String ZEEBE_REST_ADDRESS = "http://127.0.0.1:8080";

    public static void main(String[] args) {



        /*
        final OAuthCredentialsProvider credentialsProvider =
            new OAuthCredentialsProviderBuilder()
                    .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
                    .audience(ZEEBE_TOKEN_AUDIENCE)
                    .clientId(ZEEBE_CLIENT_ID)
                    .clientSecret(ZEEBE_CLIENT_SECRET)
                    .build();
        */

        try (ZeebeClient client = ZeebeClient.newClientBuilder()
                .grpcAddress(new URI(ZEEBE_GRPC_ADDRESS))
                .restAddress(new URI(ZEEBE_REST_ADDRESS))
                 //.credentialsProvider(credentialsProvider) // Authenticated version
                .usePlaintext()  // Plaint text version
                .build()) {

            final Map<String, Object> variables = new HashMap<>();
            variables.put("reference", "CB_12345");
            variables.put("amount", Double.valueOf(100.00));
            variables.put("cardNumber", "1234567812345678");
            variables.put("cardExpiry", "12/2023");
            variables.put("cardCVC", "123");

            // Create and start and instance of the specified process
            client.newCreateInstanceCommand()
                .bpmnProcessId("paymentProcess")
                .latestVersion()
                .variables(variables)
                .send()
                .join();

            // Initial testing:
            // System.out.println("Connected to: " + client.newTopologyRequest().send().join());
            // Connected to: TopologyImpl{brokers=[BrokerInfoImpl{nodeId=0, host='camunda-platform-zeebe-0.camunda-platform-zeebe.default.svc', port=26501, version=8.5.4, partitions=[PartitionInfoImpl{partitionId=1, role=LEADER, health=HEALTHY}]}], clusterSize=1, partitionsCount=1, replicationFactor=1, gatewayVersion=8.5.4}

            // Register a new JobWorker for Jobs of a given type
            final JobWorker creditCardJobWorker = client.newWorker()
                    .jobType("chargeCreditCard")
                    .handler(new CreditCardServiceHandler())
                    .timeout(Duration.ofSeconds(10).toMillis())  // To handle long operations
                    .open();

            Thread.sleep(10000);

        } catch (Exception ex) {
            System.err.println("Invalid URI provided: " + ex.getMessage());
        }
    }
}