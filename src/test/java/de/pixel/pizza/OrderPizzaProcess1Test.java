package de.pixel.pizza;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class OrderPizzaProcess1Test {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPizzaProcess1Test.class);

    @Autowired
    public RuntimeService runtimeService;

    @Test
    @Order(1)
    public void complete_pizza_order() throws Exception {
        // given
        String processDefinitionKey = "OrderPizzaProcess1";

        // when
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        assertThat(processInstance).isStarted();
        Thread.sleep(2 * 1000);
//        Execution execution = runtimeService.createExecutionQuery()
//                .processInstanceId(processInstance.getId()).activityId("WaitForPizza").singleResult();
//        runtimeService.signal(execution.getId());

        EventSubscription subscription = runtimeService.createEventSubscriptionQuery()
                .processInstanceId(processInstance.getId()).activityId("WaitForPizza").eventType("message").singleResult();

        runtimeService.messageEventReceived("PizzaReceivedMessage", subscription.getExecutionId());

//        runtimeService.createMessageCorrelation(subscription.getEventName())
//                .processInstanceId(processInstance.getId())
//                .correlate();

        // then
        Thread.sleep(2 * 1000);
        assertThat(processInstance).isEnded().hasPassed("OrderPizza", "WaitForPizza", "EatPizza")
                .hasNotPassed("ComplainToDeliveryService", "CancelOrder");
    }

    @Test
    @Order(2)
    public void complain_pizza_order() throws Exception {
        // given
        String processDefinitionKey = "OrderPizzaProcess1";

        // when
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        assertThat(processInstance).isStarted();
        Thread.sleep(20 * 1000);

        EventSubscription subscription = runtimeService.createEventSubscriptionQuery()
                .processInstanceId(processInstance.getId()).activityId("WaitForPizza").eventType("message").singleResult();
        LOGGER.info("PizzaReceivedMessage");
        runtimeService.messageEventReceived("PizzaReceivedMessage", subscription.getExecutionId());

        // then
        Thread.sleep(2 * 1000);
        assertThat(processInstance).isEnded().hasPassed("OrderPizza", "WaitForPizza", "ComplainToDeliveryService", "EatPizza")
                .hasNotPassed("CancelOrder");
    }


    @Test
    @Order(3)
    public void cancel_pizza_order() throws Exception {
        // given
        String processDefinitionKey = "OrderPizzaProcess1";

        // when
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        assertThat(processInstance).isStarted();

        // then
        Thread.sleep(30 * 1000);

        assertThat(processInstance).isEnded().hasPassed("OrderPizza", "WaitForPizza", "ComplainToDeliveryService", "CancelOrder")
                .hasNotPassed("EatPizza");
    }

}
