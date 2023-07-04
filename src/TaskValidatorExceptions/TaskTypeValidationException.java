package TaskValidatorExceptions;

import Tasks.TaskType;

public class TaskTypeValidationException extends RuntimeException{
    private static final String MESSAGE = "Неверный тип задачи, ожидалось: ";

    public TaskTypeValidationException(TaskType wrongType, TaskType correctType){
        super(MESSAGE + correctType + " ,а было получено: " + wrongType);
    }
}
