package Managers;

import java.io.File;

public class Managers {

    private Managers(){

    }
    public static HttpTaskManager getDefault(String uri){
        return new HttpTaskManager(uri);
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBacked(File file){
        return FileBackedTasksManager.loadFromFile(file);
    }
}
