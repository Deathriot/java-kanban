package Managers;

import Tasks.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    protected int nextId = 1;
    protected final Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    protected final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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
        EpicTask epicTask = epicTasks.get(taskId);

        historyManager.addTask(epicTask);
        return epicTask;
    }
    @Override
    public SubTask getSubTask(int taskId){
        SubTask subTask = subTasks.get(taskId);

        historyManager.addTask(subTask);
        return subTask;
    }

    @Override
    public void removeAllSimpleTasks(){
        for(Integer id : simpleTasks.keySet()){
            historyManager.remove(id);
        }
        simpleTasks.clear();

    }
    @Override
    public void removeAllEpicTasks(){
        for(Integer id : epicTasks.keySet()){
            historyManager.remove(id);
        }

        for(Integer id : subTasks.keySet()){
            historyManager.remove(id);
        }

        epicTasks.clear();
        subTasks.clear();
    }
    @Override
    public void removeAllSubTasks(){

        for(Integer id : subTasks.keySet()){
            historyManager.remove(id);
        }

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
        historyManager.remove(id);
        simpleTasks.remove(id);
    }
    @Override
    public void removeEpicTask(int id){
        historyManager.remove(id);

        EpicTask epic = epicTasks.remove(id);
        ArrayList<Integer> epicsSubTasksId = epic.getSubTasksId();

        for (Integer subTaskId : epicsSubTasksId) {
            historyManager.remove(subTaskId);
            subTasks.remove(subTaskId);
        }

    }
    @Override
    public void removeSubTask(int id){
        historyManager.remove(id);

        SubTask removedSubTask = subTasks.remove(id);

        EpicTask epic = epicTasks.get(removedSubTask.getEpicId());
        epic.removeSubTask(id);
        checkEpic(epic);
    }

    @Override
    public List<SubTask> getAnEpicSubTasks(EpicTask task){
        List<SubTask> epicsSubTasks = new ArrayList<>();
        List <Integer> listSubTaskId = task.getSubTasksId();

        for (Integer subTaskId : listSubTaskId) {
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
