import Tasks.*;
import Managers.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args){
        String path = "src\\Tasks.csv"; //Относительный путь
        File file = new File(path);
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        SimpleTask simple1 = new SimpleTask("test1", "test1", Status.DONE,
                LocalDateTime.of(2000,10,10,10,10),
                Duration.ofMinutes(30));

        manager.addSimpleTask(simple1);

        SimpleTask simple2 = new SimpleTask("test2", "test2", Status.NEW, null, null);

        manager.addSimpleTask(simple2);

        TaskManager manager2 = Managers.getFileBacked(file);
        System.out.println(manager2.getAllSimpleTasks());

        HistoryManager history = new InMemoryHistoryManager();
        history.addTask(simple1);
        history.addTask(simple2);
        history.addTask(simple1);
        System.out.println(history.getHistory());
    }
}

