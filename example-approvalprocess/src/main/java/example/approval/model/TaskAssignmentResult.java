package example.approval.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

@Value.Immutable
public abstract class TaskAssignmentResult {

    @NotNull
    public abstract List<String> candidateUsers();
}
