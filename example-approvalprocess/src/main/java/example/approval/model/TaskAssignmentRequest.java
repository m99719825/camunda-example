package example.approval.model;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.immutables.value.Value;

@Value.Immutable
public abstract class TaskAssignmentRequest {

    public static ImmutableTaskAssignmentRequest of(@Nonnull final DelegateTask delegateTask) {
        return ImmutableTaskAssignmentRequest.builder().stepId(delegateTask.getTaskDefinitionKey()).variables(delegateTask.getVariables()).build();
    }

    @NotNull
    public abstract String stepId();

    @NotNull
    public abstract Map<String, Object> variables();

}
