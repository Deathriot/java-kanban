package ManagerExceptions;

import java.io.IOException;

public class ManagerSaveException extends IOException{
    public ManagerSaveException(){
        super("При сохранении файла произошла ошибка!");
    }
}
