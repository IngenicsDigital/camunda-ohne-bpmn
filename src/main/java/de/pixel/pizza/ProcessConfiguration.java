package de.pixel.pizza;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.File;

@Configuration
@EnableProcessApplication
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ProcessConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessConfiguration.class);


    @Autowired
    private ProcessEngine processEngine;

    @EventListener
    public void onStart(PostDeployEvent event) {
        LOGGER.info("Deploy additional processes");

        BpmnModelInstance modelInstance = model();

        processEngine.getRepositoryService()
                .createDeployment()
                .addModelInstance("pizza2.bpmn", modelInstance)
                .name("pizza2")
                .deploy();

        Bpmn.writeModelToFile(new File("target/pizza2.bpmn"), modelInstance);
    }

    private BpmnModelInstance model() {
        return Bpmn.createProcess()
                    .id("OrderPizzaProcess2")
                    .executable()
                .startEvent()
                .manualTask("OrderPizza")
                    .name("Order Pizza")
                    .camundaExecutionListenerClass("start", PizzaExecutionListener.class)
                .receiveTask("WaitForPizza")
                    .name("Wait for Pizza")
                    .camundaExecutionListenerClass("start", PizzaExecutionListener.class)
                    .message("PizzaReceivedMessage")
                    .boundaryEvent()
                            .cancelActivity(false)
                            .timerWithDuration("PT45M")
                            .name("45m")
                        .manualTask("ComplainToDeliveryService")
                            .name("Complain to Delivery Service")
                            .camundaExecutionListenerClass("start", PizzaExecutionListener.class)
                        .endEvent()
                        .moveToActivity("WaitForPizza")
                    .boundaryEvent()
                            .timerWithDuration("PT1H")
                            .name("1h")
                        .manualTask("CancelOrder")
                            .name("Cancel Order")
                            .camundaExecutionListenerClass("start", PizzaExecutionListener.class)
                        .endEvent()
                        .moveToActivity("WaitForPizza")
                .manualTask("EatPizza")
                    .name("Eat Pizza")
                    .camundaExecutionListenerClass("start", PizzaExecutionListener.class)
                .endEvent()
                .done();
    }

}
