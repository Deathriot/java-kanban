import Tasks.*;
import Managers.*;

import java.io.File;

public class Main {

    public static void main(String[] args){
        String path = "D:\\work\\dev\\TaskManager_hw\\java-kanban\\src\\Tasks.csv";
        File file = new File(path);

        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        SimpleTask simpleTask = new SimpleTask("Task", "just Task", Status.NEW);
        manager.addSimpleTask(simpleTask);
        manager.addSimpleTask(new SimpleTask("Task 2", "just Task 2", Status.NEW));

        EpicTask epic = new EpicTask("epic 1", "just epic 1");
        manager.addEpicTask(epic);

        manager.addSubTask(new SubTask("SubTask 1", "just sub 2", Status.NEW,epic));

        manager.getEpicTask(epic.getId());
        manager.getSimpleTask(simpleTask.getId());
        manager.getEpicTask(epic.getId());

        TaskManager manager2 = Managers.getFileBacked(file);
        System.out.println(manager2.getHistory());
        System.out.println();
        System.out.println(manager2.getAllSimpleTasks());
    }
}

