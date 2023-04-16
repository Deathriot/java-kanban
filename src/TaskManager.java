import Tasks.*;
import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int nextId = 1;
    private HashMap<Integer, SimpleTask> simpleTasks = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public ArrayList<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }
    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public SimpleTask getSimpleTask(int taskId){
        return simpleTasks.get(taskId);
    }
    public EpicTask getEpicTask(int taskId){
        return epicTasks.get(taskId);
    }
    public SubTask getSubTask(int taskId){
        return subTasks.get(taskId);
    }

    public void removeAllSimpleTasks(){
        simpleTasks.clear();
    }
    public void removeAllEpicTasks(){
        epicTasks.clear();
        subTasks.clear();
    }
    public void removeAllSubTasks(){
        subTasks.clear();

        if (epicTasks == null){
            return;
        }

        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.clearSubTasksId();
        }
    }

    public void addSimpleTask (SimpleTask task){
        task.setId(nextId);
        nextId++;
        simpleTasks.put(task.getId(), task);
    }
    public void addEpicTask (EpicTask task){
        task.setId(nextId);
        nextId++;
        epicTasks.put(task.getId(),task);
    }
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

    public void updateSimpleTask(SimpleTask task){
        if(simpleTasks.containsKey(task.getId())){
            simpleTasks.put(task.getId(), task);
        }
    }
    public void updateEpicTask(EpicTask task){
        if(epicTasks.containsKey(task.getId())){
            epicTasks.put(task.getId(),task);
        }
    }
    public void updateSubTask(SubTask task){
        if(!subTasks.containsKey(task.getId())){
            return;
        }
        subTasks.put(task.getId(), task);

        EpicTask epic = epicTasks.get(task.getEpicId());
        checkEpic(epic);
    }

    public void removeSimpleTask(int id){
        simpleTasks.remove(id);
    }
    public void removeEpicTask(int id){
        EpicTask epic = epicTasks.remove(id);
        ArrayList<Integer> epicsSubTasksId = epic.getSubTasksId();

        for (Integer subTaskId : epicsSubTasksId) {
            subTasks.remove(subTaskId);
        }

    }
    public void removeSubTask(int id){
        SubTask removedSubTask = subTasks.remove(id);

        EpicTask epic = epicTasks.get(removedSubTask.getEpicId());
        epic.removeSubTask(id);
        checkEpic(epic);
    }

    public ArrayList<SubTask> getAnEpicSubTasks(EpicTask task){
        ArrayList<SubTask> epicsSubTasks = new ArrayList<>();

        for (Integer subTaskId : task.getSubTasksId()) {
            epicsSubTasks.add(subTasks.get(subTaskId));
        }
        return epicsSubTasks;
    }

    private void checkEpic(EpicTask epic){
        boolean isAllNew = true;
        boolean isAllDone = true;
        ArrayList<SubTask> tasks = new ArrayList<>();

        for (Integer subTaskId : epic.getSubTasksId()) {
            tasks.add(subTasks.get(subTaskId));
        }

        if(tasks.isEmpty()){
            epic.setStatus("NEW");
            return;
        }

        for (SubTask task : tasks) {
            if(task.getStatus().equals("IN_PROGRESS")){
                epic.setStatus("IN_PROGRESS");
                return;
            }

            if(task.getStatus().equals("NEW")){
                isAllDone = false;
            }
            else{
                isAllNew = false;
            }
        }
        if(isAllNew){
            epic.setStatus("NEW");
        } else if (isAllDone) {
            epic.setStatus("DONE");
        }else{
            epic.setStatus("IN_PROGRESS");
        }
    }
}
