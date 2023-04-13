import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static TaskManager manager = new TaskManager();
     static void printSimpleTasks(HashMap<Integer, SimpleTask> tasks){
        for (SimpleTask simpleTask : tasks.values()) {
            Task task =  simpleTask;
            task.toString();
            System.out.println();
        }
    }
     static void printEpicTasks(HashMap<Integer, EpicTask> tasks){
        for (EpicTask epicTask : tasks.values()) {
            Task task = epicTask;
            System.out.println(task);
            System.out.println();
            System.out.println("В нем лежат подзадачи:");
            System.out.println(">>>");
            ArrayList<SubTask> subTasks = epicTask.getSubTasks();

            if(subTasks == null){
                System.out.println("Ничего в нем не лежит");
                return;
            }

            for (SubTask subTask : subTasks) {
                task = subTask;
                System.out.println(task);
                System.out.println();
            }
            System.out.println("<<<");
        }
    }
     static void printSubTasks(HashMap<Integer,SubTask> tasks){
        for (SubTask subTask : tasks.values()) {
            Task task = subTask;
            System.out.println(task);
            System.out.println("Принадлежит эпику с id " + subTask.getEpicId());
            System.out.println();
        }
    }
    public static void main(String[] args){
        System.out.println("Добавляем задачи:");
        SimpleTask simpleTask1 = new SimpleTask("номер 1","обычная задача","NEW");
        SimpleTask simpleTask2 = new SimpleTask("номер 2","обычная задача","NEW");
        manager.addSimpleTask(simpleTask1);
        manager.addSimpleTask(simpleTask2);

        EpicTask epicTask1 = new EpicTask("номер 1","Эпик");
        EpicTask epicTask2 = new EpicTask("номер 2","Эпик");
        manager.addEpicTask(epicTask1);
        manager.addEpicTask(epicTask2);

        SubTask subTask1 = new SubTask("номер 1, у первого эпика","подзадача","NEW", epicTask1);
        SubTask subTask2 = new SubTask("номер 2, у первого эпика","подзадача","NEW", epicTask1);
        SubTask subTask3 = new SubTask("номер 3, у второго эпика","подзадача","NEW", epicTask2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        printSimpleTasks(manager.getAllSimpleTasks());
        printEpicTasks(manager.getAllEpicTasks());
        printSubTasks(manager.getAllSubTasks());

        System.out.println("============");

        System.out.println("Обновляем задачи:");
        simpleTask1.setStatus("DONE");
        simpleTask2.setStatus("IN_PROGRESS");
        manager.updateSimpleTask(simpleTask1);
        manager.updateSimpleTask(simpleTask2);

        epicTask1.setDescription("эпик, в процессе");
        manager.updateEpicTask(epicTask1);
        subTask1.setStatus("IN_PROGRESS");
        subTask2.setStatus("DONE");
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);

        subTask3.setStatus("DONE");
        manager.updateSubTask(subTask3);
        epicTask2.setDescription("эпик, уже сделали");
        manager.updateEpicTask(epicTask2);

        printSimpleTasks(manager.getAllSimpleTasks());
        printEpicTasks(manager.getAllEpicTasks());
        printSubTasks(manager.getAllSubTasks());

        System.out.println("============");

        System.out.println("удаляем задачи:");
        manager.removeEpicTask(epicTask2.getId());
        manager.removeSubTask(subTask1.getId());

        printEpicTasks(manager.getAllEpicTasks());
        printSubTasks(manager.getAllSubTasks());

        System.out.println("============");

        System.out.println("Делаем невозможные действия:");
        EpicTask inCorrectEpicTask = new EpicTask("Не должно обновиться","меня тут нет!");
        manager.updateEpicTask(inCorrectEpicTask);
        SubTask inCorrectSubTask = new SubTask("Ошибка?","Точно ошибка!","NEW", inCorrectEpicTask);
        manager.addSubTask(inCorrectSubTask);
        manager.updateSubTask(inCorrectSubTask);
        SimpleTask inCorrectSimpleTask = new SimpleTask("И здесь","Нет","DONE");
        manager.updateSimpleTask(inCorrectSimpleTask);

        printSimpleTasks(manager.getAllSimpleTasks());
        printEpicTasks(manager.getAllEpicTasks());
        printSubTasks(manager.getAllSubTasks());

        System.out.println("============");

        System.out.println("Проверяем корректность удаления всех элементов:");
        manager.removeAllSubTasks();
        manager.removeAllSimpleTasks();

        printEpicTasks(manager.getAllEpicTasks());
        printSubTasks(manager.getAllSubTasks());
        printSimpleTasks(manager.getAllSimpleTasks());


    }
}
