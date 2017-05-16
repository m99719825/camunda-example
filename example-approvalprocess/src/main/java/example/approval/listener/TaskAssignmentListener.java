package example.approval.listener;

import static org.slf4j.LoggerFactory.getLogger;

import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import example.approval.model.ImmutableTaskAssignmentResult;
import example.approval.model.TaskAssignmentRequest;
import example.approval.service.TaskAssignmentService;


/**
 * This is a task listener implementation to illustrate an
 * extension point for the task handling.
 */
@Component("TaskAssignmentListener")
public class TaskAssignmentListener implements TaskListener {

    private final Logger logger = getLogger(this.getClass().getName());

    public void notify(DelegateTask delegateTask) {
        logger.info("trigger assignment for {}", delegateTask.getTaskDefinitionKey());
        ImmutableTaskAssignmentResult users = provideAssignmentService(
                delegateTask.getProcessEngineServices().getDecisionService())
                .evaluate(TaskAssignmentRequest.of(delegateTask));
        logger.info("candidate user {} for {}", users.candidateUsers(), delegateTask.getTaskDefinitionKey());
        delegateTask.addCandidateUsers(users.candidateUsers());
    }

    private TaskAssignmentService provideAssignmentService(final DecisionService decisionService) {
        return new TaskAssignmentService(decisionService);
    }
}