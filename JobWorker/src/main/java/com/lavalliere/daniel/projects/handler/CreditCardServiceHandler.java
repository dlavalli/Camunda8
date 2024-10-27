package com.lavalliere.daniel.projects.handler;

import com.lavalliere.daniel.projects.service.CreditCardService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.HashMap;
import java.util.Map;

public class CreditCardServiceHandler implements JobHandler{

    private CreditCardService creditCardService = new CreditCardService();


    // The handler will invoke the CreditCardService each time a Process Instance reaches a Service Task
    // with the type of  chargeCreditCard. The CreditCardServiceHandler will implement the JobHandler interface
    // and will supply an implementation for the handle method which will invoke the chargeCreditCard method
    // in the CreditCardSdervice

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {

        // To validate we can connect without doing any operations
        // creditCardService.chargeCreditCard();
        // client.newCompleteCommand(job.getKey()).send().join();

        final Map<String, Object> inputVariables = job.getVariablesAsMap();

        final String confirmation = creditCardService.chargeCreditCard(
            (String)inputVariables.get("reference"),
            (Double)inputVariables.get("amount"),
            (String)inputVariables.get("cardNumber"),
            (String)inputVariables.get("cardExpiry"),
            (String)inputVariables.get("cardCVC")
        );

        final Map<String, Object> outputVariables = new HashMap<>();
        outputVariables.put("confirmation", confirmation);

        // Inform Zeebe that the job has been completed
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
    }
}
