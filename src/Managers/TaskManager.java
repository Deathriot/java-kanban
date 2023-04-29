package Managers;

import Tasks.*;
import java.util.List;

public interface TaskManager {
    List<SimpleTask> getAllSimpleTasks();
    List<EpicTask> getAllEpicTasks();
    List<SubTask> getAllSubTasks();

    SimpleTask getSimpleTask(int taskId);
    EpicTask getEpicTask(int taskId);
    SubTask getSubTask(int taskId);

    void removeAllSimpleTasks();
    void removeAllEpicTasks();
    void removeAllSubTasks();

    void addSimpleTask (SimpleTask task);
    void addEpicTask (EpicTask task);
    void addSubTask (SubTask task);

    void updateSimpleTask(SimpleTask task);
    void updateEpicTask(EpicTask task);
    void updateSubTask(SubTask task);

    void removeSimpleTask(int id);
    void removeEpicTask(int id);
    void removeSubTask(int id);

    List<SubTask> getAnEpicSubTasks(EpicTask task);

    List<SimpleTask> getHistory();
}
