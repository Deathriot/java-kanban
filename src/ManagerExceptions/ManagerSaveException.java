package ManagerExceptions;


public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(){
        super("При сохранении файла произошла ошибка!");
    }
}
