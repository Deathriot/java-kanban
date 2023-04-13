import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int nextId = 1;
    private HashMap<Integer, SimpleTask> simpleTasks = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public HashMap<Integer, SimpleTask> getAllSimpleTasks() {
        return simpleTasks;
    }
    public HashMap<Integer, EpicTask> getAllEpicTasks() {
        return epicTasks;
    }
    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    public SimpleTask getSimpleTask(int taskId){
        if(simpleTasks.containsKey(taskId)) {
            return simpleTasks.get(taskId);
        }
        return null;
    }
    public EpicTask getEpicTask(int taskId){
        if(epicTasks.containsKey(taskId)) {
            return epicTasks.get(taskId);
        }
        return null;
    }
    public SubTask getSubTask(int taskId){
        if(subTasks.containsKey(taskId)) {
            return subTasks.get(taskId);
        }
        return null;
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
            epicTask.setSubTasks(null);
            checkAndAddEpic(epicTask);
        }
    }

    public void addSimpleTask (SimpleTask task){
        task.setId(nextId);
        simpleTasks.put(task.getId(), task);
        nextId++;
    }
    public void addEpicTask (EpicTask task){
        task.setId(nextId);
        checkAndAddEpic(task);
        nextId++;
    }
    public void addSubTask (SubTask task){
        EpicTask anEpicTask = epicTasks.get(task.getEpicId());

        if(anEpicTask == null){
            return;
        }

        task.setId(nextId);
        subTasks.put(task.getId(),task);
        nextId++;


        ArrayList<SubTask> epicsSubTasks = anEpicTask.getSubTasks();
        epicsSubTasks.add(task);
        anEpicTask.setSubTasks(epicsSubTasks);
        checkAndAddEpic(anEpicTask);
    }

    public void updateSimpleTask(SimpleTask task){
        if(simpleTasks.containsKey(task.getId())){
            simpleTasks.put(task.getId(), task);
        }
    }
    public void updateEpicTask(EpicTask task){
        if(epicTasks.containsKey(task.getId())){
            checkAndAddEpic(task);
        }
    }
    public void updateSubTask(SubTask task){
        if(!subTasks.containsKey(task.getId())){
            return;
        }
        subTasks.put(task.getId(), task);

        EpicTask epicTask = epicTasks.get(task.getEpicId());
        ArrayList<SubTask> epicsSubTasks = epicTask.getSubTasks();

        for (SubTask epicsSubTask : epicsSubTasks) {
            if(epicsSubTask.getId() == task.getId()){
                epicsSubTasks.remove(epicsSubTask);
                epicsSubTasks.add(task);
                break;
            }
        }

        checkAndAddEpic(epicTask);
    }

    public void removeSimpleTask(int id){
        simpleTasks.remove(id);
    }
    public void removeEpicTask(int id){
        ArrayList<SubTask> epicsSubTasks = epicTasks.get(id).getSubTasks();

        for (SubTask subTask : epicsSubTasks) {
            subTasks.remove(subTask.getId());
        }

        epicTasks.remove(id);
    }
    public void removeSubTask(int id){
        SubTask removedSubTask = subTasks.get(id);
        subTasks.remove(id);

        EpicTask epicTask = epicTasks.get(removedSubTask.getEpicId());
        ArrayList<SubTask> epicsSubTasks = epicTask.getSubTasks();
        epicsSubTasks.remove(removedSubTask);
        checkAndAddEpic(epicTask);
    }

    public ArrayList<SubTask> getAnEpicSubTasks(EpicTask task){
        return task.getSubTasks();
    }

    private void checkAndAddEpic(EpicTask epic){
        boolean isNew = false;
        ArrayList<SubTask> tasks = epic.getSubTasks();

        if(tasks == null){
            epic.status = "NEW";
            epicTasks.put(epic.getId(),epic);
            return;
        }

        for (SubTask task : tasks) {
            if(task.status.equals("IN_PROGRESS")){
                epic.status = "IN_PROGRESS";
                epicTasks.put(epic.getId(),epic);
                return;
            }

            if(task.status.equals("NEW")){
                isNew = true;
            }

            if(isNew){
                epic.status = "NEW";
            }else{
                epic.status = "DONE";
            }
        }
        epicTasks.put(epic.getId(),epic);
    }
}
