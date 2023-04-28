import Tasks.*;

public class Main {

    public static void main(String[] args){
        TaskManager manager = Managers.getDefault();
        EpicTask testEpic = new EpicTask("1","1");
        manager.addEpicTask(testEpic);

        SubTask subTask1 = new SubTask("1","1",Status.DONE,testEpic);
        SubTask subTask2 = new SubTask("1","1",Status.DONE,testEpic);
        SubTask subTask3 = new SubTask("1","1",Status.DONE,testEpic);
        SubTask subTask4 = new SubTask("1","1",Status.NEW,testEpic);

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
        manager.addSubTask(subTask4);

        SimpleTask simpleTask = new SimpleTask("1","1",Status.IN_PROGRESS);
        manager.addSimpleTask(simpleTask);
        manager.getSimpleTask(simpleTask.getId());

        System.out.println(manager.getHistory());

        manager.getSubTask(subTask1.getId());

        System.out.println(manager.getHistory());

        System.out.println(">>>");

        for(int i = 1; i<=9; i++){
            manager.getEpicTask(testEpic.getId());
        }

        System.out.println(manager.getHistory());
    }
}

