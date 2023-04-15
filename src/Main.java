import Tasks.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args){
        TaskManager manager = new TaskManager();
        EpicTask testEpic = new EpicTask("1","1");
        manager.addEpicTask(testEpic);

        SubTask subTask1 = new SubTask("1","1","DONE",testEpic);
        SubTask subTask2 = new SubTask("1","1","DONE",testEpic);
        SubTask subTask3 = new SubTask("1","1","DONE",testEpic);
        SubTask subTask4 = new SubTask("1","1","NEW",testEpic);

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
        manager.addSubTask(subTask4);

        System.out.println(testEpic.getStatus());

        subTask4.setStatus("DONE");
        manager.updateSubTask(subTask4);
        System.out.println(testEpic.getStatus());

        subTask1.setStatus("IN_PROGRESS");
        manager.updateSubTask(subTask1);
        manager.removeSubTask(subTask4.getId());
        System.out.println(testEpic.getStatus());
        System.out.println(manager.getAnEpicSubTasks(testEpic));
        manager.removeEpicTask(testEpic.getId());
        System.out.println(manager.getAnEpicSubTasks(testEpic));
    }
}

