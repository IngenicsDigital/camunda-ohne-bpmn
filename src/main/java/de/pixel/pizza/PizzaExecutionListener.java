package de.pixel.pizza;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PizzaExecutionListener implements ExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PizzaExecutionListener.class);

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        LOGGER.info("{}", delegateExecution.getCurrentActivityName());
    }
}
