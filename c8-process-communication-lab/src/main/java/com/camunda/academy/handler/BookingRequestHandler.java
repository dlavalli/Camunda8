package com.camunda.academy.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class BookingRequestHandler implements JobHandler {

    private static final String MESSAGE_NAME = "msg_startBookingRequest";

    private final static String ZEEBE_GRPC_ADDRESS = "http://127.0.0.1:26500";
    private final static String ZEEBE_REST_ADDRESS = "http://127.0.0.1:8080";

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        final Map<String, Object> inputVariables = job.getVariablesAsMap();
        final String travelRequestId = (String) inputVariables.get("travelRequestId");
        final String travelDestination = (String) inputVariables.get("travelDestination");
        final String travelDate = (String) inputVariables.get("travelDate");
        final String travelFlight = (String) inputVariables.get("travelFlight");
        final String travelHotel = (String) inputVariables.get("travelHotel");

        try (ZeebeClient travelAgencyClient = ZeebeClient.newClientBuilder()
                .grpcAddress(new URI(ZEEBE_GRPC_ADDRESS))
                .restAddress(new URI(ZEEBE_REST_ADDRESS))
                //.credentialsProvider(credentialsProvider) // Authenticated version
                .usePlaintext()  // Plaint text version
                .build()) {

            final Map<String, Object> messageVariables = new HashMap<String, Object>();

            messageVariables.put("travelRequestId", travelRequestId);
            messageVariables.put("travelDestination", travelDestination);
            messageVariables.put("travelDate", travelDate);
            messageVariables.put("travelFlight", travelFlight);
            messageVariables.put("travelHotel", travelHotel);

            travelAgencyClient.newPublishMessageCommand()
                    .messageName(MESSAGE_NAME)
                    .correlationKey(travelRequestId)
                    .variables(messageVariables)
                    .send()
                    .join();

            System.out.println(travelRequestId + " Travel Request started");

            client.newCompleteCommand(job.getKey()).send().join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
