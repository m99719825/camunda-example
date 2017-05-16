package example.approval.process;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.mockito.DelegateExpressions;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Before;
import org.junit.Test;

import example.approval.model.ProcessConstant;


/**
 * Test case starting an in-memory database.
 */
@Deployment(resources = "processes/vacationRequest.bpmn")
public class VacationRequestProcessTest extends AbstractProcessEngineRuleTest {

    private static final String BPMN_FILE_RESOURCE_PATH = "processes/";
    private static final String PROCESS_KEY = "vacationRequest";

    private ProcessDriver processDriver;

    @Before
    public void setup() {
        processDriver = new ProcessDriver();
        DelegateExpressions.autoMock(BPMN_FILE_RESOURCE_PATH + PROCESS_KEY + ".bpmn");
    }

    @Test
    public void deploy() {
        // nothing is done here, as we just want to check for exceptions during deployment
    }

    @Test
    public void startToApproveRequest() {
        ProcessInstance processInstance = processDriver.startProcessForUsername("torben");
        assertThat(processInstance).isStarted();
        assertThat(processInstance).task()
                .hasDefinitionKey(ProcessConstant.TASK_DEFINITION_KEY_APPROVAL)
                .hasCandidateGroup(ProcessConstant.CANDIDATE_GROUP_MGT)
                .isNotAssigned();
    }

    @Test
    public void requestToApproved() {
        ProcessInstance processInstance = processDriver.startProcessForUsername("torben");
        assertThat(processInstance).isStarted();
        processDriver.approveRequest();
        assertThat(processInstance).task()
                .hasDefinitionKey(ProcessConstant.TASK_DEFINITION_KEY_APPROVED_HR)
                .hasCandidateGroup(ProcessConstant.CANDIDATE_GROUP_HR)
                .isNotAssigned();
    }

    @Test
    public void requestToReject() {
        ProcessInstance processInstance = processDriver.startProcessForUsername("torben");
        assertThat(processInstance).isStarted();
        processDriver.rejectRequest();
        assertThat(processInstance).isEnded();
    }

    @Test
    public void approvedToBooked() {
        ProcessInstance processInstance = processDriver.startProcessForUsername("torben");
        assertThat(processInstance).isStarted();
        processDriver.approveRequest();
        processDriver.rejectRequest();
        assertThat(processInstance).isEnded();
    }

    /**
     * Glue between test and process.
     */
    class ProcessDriver {

        ProcessInstance startProcessForUsername(String username) {
            ProcessInstance instance = runtimeService().startProcessInstanceByKey(PROCESS_KEY, createVacationRequestVariables(username));
            assertThat(instance).isNotNull();
            return instance;
        }

        void approveRequest() {
            taskService().complete(task().getId(),
                    withVariables(ProcessConstant.VAR_APPROVED, true));
        }

        void rejectRequest() {
            taskService().complete(task().getId(),
                    withVariables(ProcessConstant.VAR_APPROVED, false));
        }

        void bookRequest() {
            taskService().complete(task().getId(),
                    withVariables(ProcessConstant.VAR_BOOKED, true));
        }

        private Map<String, Object> createVacationRequestVariables(String username) {
            // init variables
            Map<String, Object> variables = new HashMap<>();
            variables.put(ProcessConstant.VAR_USERNAME, username);
            variables.put(ProcessConstant.VAR_DURATION, 5);
            variables.put(ProcessConstant.VAR_START_DATE, new Date());
            return variables;
        }
    }
}
