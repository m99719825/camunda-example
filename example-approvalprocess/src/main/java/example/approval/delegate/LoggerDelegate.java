package example.approval.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * This is a service implementation illustrating how to use component
 * as a BPMN 2.0 Service Task delegate.
 */
@Component("LoggerDelegate")
public class LoggerDelegate implements JavaDelegate {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void execute(DelegateExecution execution) throws Exception {
        logger.info("\n\n  ... LoggerDelegate invoked by "
                + "processDefinitionId=" + execution.getProcessDefinitionId()
                + ", activtyId=" + execution.getCurrentActivityId()
                + ", activtyName='" + execution.getCurrentActivityName() + "'"
                + ", processInstanceId=" + execution.getProcessInstanceId()
                + ", businessKey=" + execution.getProcessBusinessKey()
                + ", executionId=" + execution.getId() + " \n\n");
    }
}