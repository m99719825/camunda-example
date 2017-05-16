package example.approval.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.slf4j.Logger;

import example.approval.model.ProcessConstant;
import example.approval.model.ImmutableTaskAssignmentRequest;
import example.approval.model.ImmutableTaskAssignmentResult;


public class TaskAssignmentService {

    private final Logger logger = getLogger(this.getClass());
    private DecisionService decisionService;

    public TaskAssignmentService(@Nonnull final DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    /**
     * Evaluate candidate users.
     *
     * @param taskAssignmentRequest ImmutableTaskAssignmentRequest
     * @return List of candidate users.
     */
    @Nonnull
    public ImmutableTaskAssignmentResult evaluate(@Nonnull ImmutableTaskAssignmentRequest taskAssignmentRequest) {
        logger.debug("Evaluate task assignment for {}", taskAssignmentRequest.stepId());
        return evaluateAssignmentDecisionTableWithContext(evaluateDecisionTableInput(taskAssignmentRequest), ProcessConstant.DMN_TASK_ASSIGNMENT_KEY, ProcessConstant.VAR_ASSIGNEE);
    }

    Map<String, Object> evaluateDecisionTableInput(@Nonnull ImmutableTaskAssignmentRequest taskAssignmentRequest) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put(ProcessConstant.VAR_STEP_ID, taskAssignmentRequest.stepId());
        vars.putAll(taskAssignmentRequest.variables());
        return vars;
    }

    ImmutableTaskAssignmentResult evaluateAssignmentDecisionTableWithContext(final Map<String, Object> context, final String dmnKey, final String resultName) {
        final DmnDecisionTableResult tableResult = decisionService.evaluateDecisionTableByKey(dmnKey, context);
        return ImmutableTaskAssignmentResult.builder().addAllCandidateUsers(tableResult.collectEntries(resultName)).build();
    }
}
