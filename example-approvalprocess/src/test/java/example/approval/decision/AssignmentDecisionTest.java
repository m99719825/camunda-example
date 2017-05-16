package example.approval.decision;


import static example.approval.model.ProcessConstant.DMN_TASK_ASSIGNMENT_KEY;
import static example.approval.model.ProcessConstant.TASK_DEFINITION_KEY_APPROVAL;
import static example.approval.model.ProcessConstant.TASK_DEFINITION_KEY_APPROVED_HR;
import static example.approval.model.ProcessConstant.VAR_ASSIGNEE;
import static example.approval.model.ProcessConstant.VAR_STEP_ID;
import static example.approval.model.ProcessConstant.VAR_USERNAME;
import static org.junit.Assert.assertEquals;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.MockExpressionManager;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.FluentIterable;


public class AssignmentDecisionTest {

    private static final String DMN_FILE_RESOURCE_PATH = "processes/";
    private static final String DMN_ASSIGNMENT_KEY = "taskAssignment";
    private static final String DMN_ASSIGNMENT_RESOURCE = DMN_FILE_RESOURCE_PATH + DMN_ASSIGNMENT_KEY + ".dmn";

    private final ProcessEngineConfigurationImpl configuration = new StandaloneInMemProcessEngineConfiguration() {
        {
            databaseSchemaUpdate = DB_SCHEMA_UPDATE_DROP_CREATE;
            expressionManager = new MockExpressionManager();
            jobExecutorActivate = false;
            historyLevel = HistoryLevel.HISTORY_LEVEL_FULL;
        }
    };

    @Rule
    public final ProcessEngineRule processEngineRule = new ProcessEngineRule(configuration.buildProcessEngine());

    @Test
    public void deploy() {
        // nothing is done here, as we just want to check for exceptions during deployment
    }

    @Test
    @Deployment(resources = DMN_ASSIGNMENT_RESOURCE)
    public void evaluateApprovalAssignmenForTorben() {
        final DmnDecisionTableResult results = processEngineRule.getDecisionService().evaluateDecisionTableByKey(
                DMN_TASK_ASSIGNMENT_KEY,
                Variables.createVariables().putValue(VAR_STEP_ID, TASK_DEFINITION_KEY_APPROVAL)
                        .putValue(VAR_USERNAME, "Torben"));

        assertEquals(1, FluentIterable.from(results).size());
        assertEquals("frank", FluentIterable.from(results).get(0).get(VAR_ASSIGNEE));
    }

    @Test
    @Deployment(resources = DMN_ASSIGNMENT_RESOURCE)
    public void evaluateApprovalAssignmenForUli() {
        final DmnDecisionTableResult results = processEngineRule.getDecisionService().evaluateDecisionTableByKey(
                DMN_TASK_ASSIGNMENT_KEY,
                Variables.createVariables().putValue(VAR_STEP_ID, TASK_DEFINITION_KEY_APPROVAL)
                        .putValue(VAR_USERNAME, "uli"));

        assertEquals(1, FluentIterable.from(results).size());
        assertEquals("torben", FluentIterable.from(results).get(0).get(VAR_ASSIGNEE));
    }

    @Test
    @Deployment(resources = DMN_ASSIGNMENT_RESOURCE)
    public void evaluateApprovalAssignmenForOther() {
        final DmnDecisionTableResult results = processEngineRule.getDecisionService().evaluateDecisionTableByKey(
                DMN_TASK_ASSIGNMENT_KEY,
                Variables.createVariables().putValue(VAR_STEP_ID, TASK_DEFINITION_KEY_APPROVAL)
                        .putValue(VAR_USERNAME, "hansgeorg"));

        assertEquals(1, FluentIterable.from(results).size());
        assertEquals("frank", FluentIterable.from(results).get(0).get(VAR_ASSIGNEE));
    }

    @Test
    @Deployment(resources = DMN_ASSIGNMENT_RESOURCE)
    public void evaluateHumanResourceAssignment() {
        final DmnDecisionTableResult results = processEngineRule.getDecisionService().evaluateDecisionTableByKey(
                DMN_TASK_ASSIGNMENT_KEY,
                Variables.createVariables().putValue(VAR_STEP_ID, TASK_DEFINITION_KEY_APPROVED_HR)
                        .putValue(VAR_USERNAME, "torben"));

        assertEquals(1, FluentIterable.from(results).size());
        assertEquals("rene", FluentIterable.from(results).get(0).get(VAR_ASSIGNEE));
    }
}
