package Http.KVServerExceptions;

public class KVServerRegisterException extends RuntimeException{
    private final static String MESSAGE = "Произошла ошибка во время регистрации";
    public KVServerRegisterException(){
        super(MESSAGE);
    }
}
