package io.camunda.example;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import io.camunda.example.dto.MyConnectorRequest;
import io.camunda.example.dto.MyConnectorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OutboundConnector(
    name = "MYCONNECTOR",
    // DL: Something like: {"message": "My Message", "authentication": {"user": "My User", "token:": "My Token"}}
    // See video:  https://www.youtube.com/watch?v=wG6sqmMUo30 : Tutorial: How to Build a Custom Camunda Connector, Using Java
    inputVariables = {"authentication", "message"},
    // DL: Name of the event fired by he camunda engine, picked up by the worker, get context and do some work
    // type = "io.camunda:template:1")
    type = "MakeNiallDoWork")
@ElementTemplate(
    id = "io.camunda.connector.Template.v1",
    name = "Template connector",
    version = 1,
    description = "Describe this connector",
    icon = "icon.svg",
    documentationRef = "https://docs.camunda.io/docs/components/connectors/out-of-the-box-connectors/available-connectors-overview/",
    propertyGroups = {
      @ElementTemplate.PropertyGroup(id = "authentication", label = "Authentication"),
      @ElementTemplate.PropertyGroup(id = "compose", label = "Compose")
    },
    inputDataClass = MyConnectorRequest.class)
public class MyConnectorFunction implements OutboundConnectorFunction {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyConnectorFunction.class);

  @Override
  public Object execute(OutboundConnectorContext context) {
    final var connectorRequest = context.bindVariables(MyConnectorRequest.class);
    return executeConnector(connectorRequest);
  }

  private MyConnectorResult executeConnector(final MyConnectorRequest connectorRequest) {
    // TODO: implement connector logic
    // See :  https://github.com/camunda/connector-template-outbound

    // DL : Would put your business logic here
    //      This is the work that the worker should do for the specific connector

    // Default implementation
    LOGGER.info("Executing my connector with request {}", connectorRequest);
    String message = connectorRequest.message();
    if (message != null && message.toLowerCase().startsWith("fail")) {
      throw new ConnectorException("FAIL", "My property started with 'fail', was: " + message);
    }
    return new MyConnectorResult("Message received: " + message);
  }
}
