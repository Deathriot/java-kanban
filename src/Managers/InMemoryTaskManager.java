package Managers;

import Tasks.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private int nextId = 1;
    private final Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }
    @Override
    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public SimpleTask getSimpleTask(int taskId){
        SimpleTask simpleTask = simpleTasks.get(taskId);

        historyManager.addTask(simpleTask);
        return simpleTask;
    }
    @Override
    public EpicTask getEpicTask(int taskId){
        EpicTask EpicTask = epicTasks.get(taskId);

        historyManager.addTask(EpicTask);
        return EpicTask;
    }
    @Override
    public SubTask getSubTask(int taskId){
        SubTask subTask = subTasks.get(taskId);

        historyManager.addTask(subTask);
        return subTask;
    }

    @Override
    public void removeAllSimpleTasks(){
        simpleTasks.clear();
    }
    @Override
    public void removeAllEpicTasks(){
        epicTasks.clear();
        subTasks.clear();
    }
    @Override
    public void removeAllSubTasks(){
        subTasks.clear();

        if (epicTasks.isEmpty()){
            return;
        }

        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.clearSubTasksId();
        }
    }

    @Override
    public void addSimpleTask (SimpleTask task){
        task.setId(nextId);
        nextId++;
        simpleTasks.put(task.getId(), task);
    }
    @Override
    public void addEpicTask (EpicTask task){
        task.setId(nextId);
        nextId++;
        epicTasks.put(task.getId(),task);
    }
    @Override
    public void addSubTask (SubTask task){
        EpicTask epic = epicTasks.get(task.getEpicId());

        if(epic == null){
            return;
        }

        task.setId(nextId);
        nextId++;
        subTasks.put(task.getId(),task);

        epic.addSubTaskId(task.getId());
        checkEpic(epic);
    }

    @Override
    public void updateSimpleTask(SimpleTask task){
        if(simpleTasks.containsKey(task.getId())){
            simpleTasks.put(task.getId(), task);
        }
    }
    @Override
    public void updateEpicTask(EpicTask task){
        if(epicTasks.containsKey(task.getId())){
            epicTasks.put(task.getId(),task);
        }
    }
    @Override
    public void updateSubTask(SubTask task){
        if(!subTasks.containsKey(task.getId())){
            return;
        }
        subTasks.put(task.getId(), task);

        EpicTask epic = epicTasks.get(task.getEpicId());
        checkEpic(epic);
    }

    @Override
    public void removeSimpleTask(int id){
        simpleTasks.remove(id);
    }
    @Override
    public void removeEpicTask(int id){
        EpicTask epic = epicTasks.remove(id);
        ArrayList<Integer> epicsSubTasksId = epic.getSubTasksId();

        for (Integer subTaskId : epicsSubTasksId) {
            subTasks.remove(subTaskId);
        }

    }
    @Override
    public void removeSubTask(int id){
        SubTask removedSubTask = subTasks.remove(id);

        EpicTask epic = epicTasks.get(removedSubTask.getEpicId());
        epic.removeSubTask(id);
        checkEpic(epic);
    }

    @Override
    public List<SubTask> getAnEpicSubTasks(EpicTask task){
        ArrayList<SubTask> epicsSubTasks = new ArrayList<>();

        for (Integer subTaskId : task.getSubTasksId()) {
            epicsSubTasks.add(subTasks.get(subTaskId));
        }
        return epicsSubTasks;
    }

    @Override
    public List<SimpleTask> getHistory() {
        return historyManager.getHistory();
    }
    private void checkEpic(EpicTask epic){
        boolean isAllNew = true;
        boolean isAllDone = true;
        ArrayList<SubTask> tasks = new ArrayList<>();

        for (Integer subTaskId : epic.getSubTasksId()) {
            tasks.add(subTasks.get(subTaskId));
        }

        if(tasks.isEmpty()){
            epic.setStatus(Status.NEW);
            return;
        }

        for (SubTask task : tasks) {
            if(task.getStatus().equals(Status.IN_PROGRESS)){
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }

            if(task.getStatus().equals(Status.NEW)){
                isAllDone = false;
            }
            else{
                isAllNew = false;
            }
        }
        if(isAllNew){
            epic.setStatus(Status.NEW);
        } else if (isAllDone) {
            epic.setStatus(Status.DONE);
        }else{
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
