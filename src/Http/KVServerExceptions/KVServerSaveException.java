package Http.KVServerExceptions;

public class KVServerSaveException extends RuntimeException{
    private final static String MESSAGE = "Во время сохранения произошла ошибка";

    public KVServerSaveException(){
        super(MESSAGE);
    }
}
