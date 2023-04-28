import Tasks.*;
import java.util.ArrayList;

public interface TaskManager {
    public ArrayList<SimpleTask> getAllSimpleTasks();
    public ArrayList<EpicTask> getAllEpicTasks();
    public ArrayList<SubTask> getAllSubTasks();

    public SimpleTask getSimpleTask(int taskId);
    public EpicTask getEpicTask(int taskId);
    public SubTask getSubTask(int taskId);

    public void removeAllSimpleTasks();
    public void removeAllEpicTasks();
    public void removeAllSubTasks();

    public void addSimpleTask (SimpleTask task);
    public void addEpicTask (EpicTask task);
    public void addSubTask (SubTask task);

    public void updateSimpleTask(SimpleTask task);
    public void updateEpicTask(EpicTask task);
    public void updateSubTask(SubTask task);

    public void removeSimpleTask(int id);
    public void removeEpicTask(int id);
    public void removeSubTask(int id);

    public ArrayList<SubTask> getAnEpicSubTasks(EpicTask task);

    public ArrayList<SimpleTask> getHistory();
}
