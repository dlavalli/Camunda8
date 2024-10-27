package com.camunda.academy.handler;

import com.camunda.academy.service.CustomerService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Map;

public class NotifyCustomerTravelCancelledHandler implements JobHandler {

    private final CustomerService customerService = new CustomerService();

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {

        final Map<String, Object> inputVariables = job.getVariablesAsMap();
        final String travelRequestId = (String) inputVariables.get("travelRequestId");

        //Notify customer
        customerService.notifyTravelCancelled(travelRequestId);

        //Complete the Job
        client.newCompleteCommand(job.getKey()).send().join();
    }
}
