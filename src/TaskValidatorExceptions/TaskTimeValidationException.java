package TaskValidatorExceptions;

public class TaskTimeValidationException extends RuntimeException{
    public TaskTimeValidationException(){
        super("Активной может быть только одна задача.");
    }
}
