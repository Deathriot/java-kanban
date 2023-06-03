package Managers;

import ManagerExceptions.ManagerSaveException;
import Tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{
    private final File file;
    private final static String HEAD = "id,type,name,status,description,epic"; // Так удобно, надеюсь так можно :)

    public FileBackedTasksManager(File file){
        this.file = file;
    }

    private void save(){

        try(FileWriter writer = new FileWriter(file)){

            writer.write(HEAD);
            writer.write("\n");

            for(SimpleTask task : simpleTasks.values()){
                writer.write(task.toString());
                writer.write("\n");
            }

            for(EpicTask task : epicTasks.values()){
                writer.write(task.toString());
                writer.write("\n");
            }

            for(SubTask task : subTasks.values()){
                writer.write(task.toString());
                writer.write("\n");
            }

            writer.write("\n");
            writer.write(historyToString(historyManager));

        }catch (IOException ex){
            throw new ManagerSaveException();
        }
    }

   public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        int currentId = 1;

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            
            while(br.ready()){

                String inputString = br.readLine();

                if(inputString.equals(HEAD)){ // Игнорируем первую строчку
                    continue;
                }

                if(inputString.isBlank()){
                    String StringHistory = br.readLine(); // Поймали историю
                    List<Integer> historyId = historyFromString(StringHistory);

                    for(Integer id : historyId){
                        if(fileManager.simpleTasks.containsKey(id)){
                            fileManager.historyManager.addTask(fileManager.simpleTasks.get(id));
                        } else if(fileManager.subTasks.containsKey(id)){
                            fileManager.historyManager.addTask(fileManager.subTasks.get(id));
                        }else{
                            fileManager.historyManager.addTask(fileManager.epicTasks.get(id));
                        }
                    }
                    continue;
                }

                SimpleTask task = fileManager.taskFromString(inputString);

                if(task instanceof EpicTask){
                    fileManager.epicTasks.put(task.getId(), (EpicTask) task);
                    currentId = Math.max(currentId, task.getId());
                }else if(task instanceof SubTask){
                    currentId = Math.max(currentId, task.getId());
                    fileManager.subTasks.put(task.getId(), (SubTask) task);
                    EpicTask epic = fileManager.epicTasks.get(((SubTask) task).getEpicId()); // Получаем эпик сабтаска
                    epic.addSubTaskId(task.getId()); // Добавляем в него полученный сабтаск
                }else{
                    currentId = Math.max(currentId, task.getId());
                    fileManager.simpleTasks.put(task.getId(), task);
                }

            }
        } catch(IOException ex){
            ex.printStackTrace();
       }

        currentId++;
        fileManager.nextId = currentId; // восстанавливаем текущий свободный айдишник для будущих тасков
        return fileManager;
    }

    public static String historyToString(HistoryManager manager){
        List<SimpleTask> history = manager.getHistory();

        if(history.isEmpty()){
            return "";
        }

        StringBuilder tasksId = new StringBuilder();

        for(SimpleTask task : history){
            tasksId.append(task.getId()).append(",");
        }

        tasksId.deleteCharAt(history.size() * 2 - 1); // Удаляем последнюю не нужную запятую
        return tasksId.toString();
    }

    public static List<Integer> historyFromString(String value){
        List<Integer> history = new ArrayList<>();

        if(value == null){
            return history;
        }

        String[] split = value.split(",");

        for(String id : split){
            history.add(Integer.parseInt(id));
        }

        return history;
    }

    private SimpleTask taskFromString(String stringTask){

        String[] split = stringTask.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        if(type.equals(TaskType.SIMPLETASK)){
            SimpleTask simpleTask = new SimpleTask(name, description ,status);
            simpleTask.setId(id);
            return simpleTask;
        }else if(type.equals(TaskType.EPICTASK)){
            SimpleTask epicTask = new EpicTask(name, description);
            epicTask.setId(id);
            return epicTask;
        }else{
            int epicId = Integer.parseInt(split[5]);
            EpicTask epic = epicTasks.get(epicId);
            SubTask subTask = new SubTask(name, description, status, epic);
            subTask.setId(id);
            return subTask;
        }
    }
    @Override
    public SimpleTask getSimpleTask(int taskId) {
        SimpleTask task = super.getSimpleTask(taskId);
        save();
        return task;
    }

    @Override
    public EpicTask getEpicTask(int taskId) {
        EpicTask task = super.getEpicTask(taskId);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(int taskId) {
        SubTask task = super.getSubTask(taskId);
        save();
        return task;
    }

    @Override
    public void removeAllSimpleTasks() {
        super.removeAllSimpleTasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void addSimpleTask(SimpleTask task) {
        super.addSimpleTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask task) {
        super.addEpicTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask task) {
        super.addSubTask(task);
        save();
    }

    @Override
    public void updateSimpleTask(SimpleTask task) {
        super.updateSimpleTask(task);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask task) {
        super.updateEpicTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask task) {
        super.updateSubTask(task);
        save();
    }

    @Override
    public void removeSimpleTask(int id) {
        super.removeSimpleTask(id);
        save();
    }

    @Override
    public void removeEpicTask(int id) {
        super.removeEpicTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }
}
