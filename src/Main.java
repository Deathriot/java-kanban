import Tasks.*;
import Managers.*;
public class Main {

    public static void main(String[] args){
        TaskManager manager = Managers.getDefault();
        EpicTask testEpic1 = new EpicTask("Эпик 1","1");
        manager.addEpicTask(testEpic1);

        SubTask subTask1 = new SubTask("Саб 1","1",Status.DONE,testEpic1);
        SubTask subTask2 = new SubTask("Саб 2","1",Status.DONE,testEpic1);
        SubTask subTask3 = new SubTask("Саб 3","1",Status.DONE,testEpic1);

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        EpicTask testEpic2 = new EpicTask("Эпик 2","1");
        manager.addEpicTask(testEpic2);

        SimpleTask simpleTask1 = new SimpleTask("Обычный 1","1",Status.IN_PROGRESS);
        manager.addSimpleTask(simpleTask1);

        SimpleTask simpleTask2 = new SimpleTask("Обычный 2","1",Status.IN_PROGRESS);
        manager.addSimpleTask(simpleTask2);

        manager.getEpicTask(testEpic1.getId());
        manager.getEpicTask(testEpic1.getId());
        manager.getEpicTask(testEpic2.getId());
        manager.getEpicTask(testEpic2.getId());
        manager.getSubTask(subTask3.getId());
        System.out.println();
        System.out.println(manager.getHistory()); // Эпик 1 Эпик 2 Саб 3
        System.out.println();
        manager.getSimpleTask(simpleTask1.getId());
        manager.getSubTask(subTask2.getId());
        manager.getSubTask(subTask2.getId());
        manager.getEpicTask(testEpic2.getId());
        manager.getEpicTask(testEpic1.getId());

        System.out.println(manager.getHistory()); // Саб 3 Обычный 1 Саб 2 Эпик 2 Эпик 1

        System.out.println();
        System.out.println(">>>>>>");
        System.out.println();

        manager.removeSimpleTask(simpleTask1.getId());
        System.out.println(manager.getHistory()); // Саб 3 Саб 2 Эпик 2 Эпик 1
        System.out.println();
        manager.removeSubTask(subTask3.getId());
        System.out.println();
        System.out.println(manager.getHistory()); // Саб 2 Эпик 2 Эпик 1

        System.out.println();
        System.out.println(">>>>>>");
        System.out.println();

        manager.removeEpicTask(testEpic1.getId());
        System.out.println(manager.getHistory()); // Эпик 2

        manager.removeEpicTask(testEpic2.getId());
        System.out.println(manager.getHistory()); // Все тлен, ничего нет...
    }
}

