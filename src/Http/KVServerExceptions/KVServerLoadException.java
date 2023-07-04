package Http.KVServerExceptions;

public class KVServerLoadException extends RuntimeException{
    private static final String MESSAGE = "Во время загрузки данных в сервере произошла ошибка";

    public KVServerLoadException(){
        super(MESSAGE);
    }
}
