import Tasks.*;
import Managers.*;

import java.io.File;

public class Main {

    public static void main(String[] args){
        String path = "src\\Tasks.csv"; //Относительный путь
        File file = new File(path);

        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        SimpleTask simpleTask = new SimpleTask("Обычная задача", "Что-то", Status.NEW);
        manager.addSimpleTask(simpleTask);
        manager.addSimpleTask(new SimpleTask("Еще одна просто задача", "еще что-то", Status.NEW));

        EpicTask epic = new EpicTask("крутая задача", "поспать");
        manager.addEpicTask(epic);

        manager.addSubTask(new SubTask("Подзача спать", "Спать сильно", Status.NEW,epic));

        manager.getEpicTask(epic.getId());
        manager.getSimpleTask(simpleTask.getId());
        manager.getEpicTask(epic.getId());

        TaskManager manager2 = Managers.getFileBacked(file);

        System.out.println(manager2.getHistory());
        System.out.println();
        System.out.println(manager2.getAllSimpleTasks());
        manager2.addSimpleTask(new SimpleTask("simple", "my id should be 5",Status.IN_PROGRESS));

        epic = manager2.getEpicTask(epic.getId()); // шаманим с ссылками

        System.out.println(manager2.getAnEpicSubTasks(epic));

        manager2.addSubTask(new SubTask( "Sub","my epic has 2 subs!", Status.NEW, epic));

        System.out.println();

        System.out.println(manager2.getAnEpicSubTasks(epic));
    }
}

