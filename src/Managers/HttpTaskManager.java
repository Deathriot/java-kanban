package Managers;

import Http.KVTaskClient;
import com.google.gson.*;
import Tasks.*;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kVClient;
    private final static Gson gson = new GsonBuilder().serializeNulls().create();
    private final static String key = "httpTaskManager";

    public HttpTaskManager(String uri) {
        super(new File("src\\Tasks.scv"));
        kVClient = new KVTaskClient(URI.create(uri));
    }

    @Override
    protected void save() {
        final StringBuilder sb = new StringBuilder();

        for (SimpleTask simple : simpleTasks.values()) {
            String stringSimple = gson.toJson(simple);
            sb.append(stringSimple);
            sb.append("\n");
        }

        for (EpicTask epic : epicTasks.values()) {
            String stringEpic = gson.toJson(epic);
            sb.append(stringEpic);
            sb.append("\n");
        }

        for (SubTask sub : subTasks.values()) {
            String stringSub = gson.toJson(sub);
            sb.append(stringSub);
            sb.append("\n");
        }

        List<Integer> tasksId = new ArrayList<>();
        for (SimpleTask task : getHistory()) {
            tasksId.add(task.getId());
        }

        String stringHistory = gson.toJson(tasksId);
        sb.append(stringHistory);

        kVClient.put(key, sb.toString());
    }

    public static HttpTaskManager loadData() {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8081/register");
        String json = manager.kVClient.load(key);
        int currentNextId = 0;

        String[] splitJson = json.split("\r?\n");

        for (int i = 0; i < splitJson.length - 1; i++) {
            JsonObject task = JsonParser.parseString(splitJson[i]).getAsJsonObject();

            if ((task.get("type")).getAsString().equals("SIMPLETASK")) {
                SimpleTask simple = gson.fromJson(task, SimpleTask.class);
                manager.simpleTasks.put(simple.getId(), simple);
                currentNextId = Math.max(currentNextId, simple.getId());

            } else if ((task.get("type")).getAsString().equals("EPICTASK")) {
                EpicTask epic = gson.fromJson(task, EpicTask.class);
                manager.epicTasks.put(epic.getId(), epic);
                currentNextId = Math.max(currentNextId, epic.getId());

            } else {
                SubTask sub = gson.fromJson(task, SubTask.class);
                manager.subTasks.put(sub.getId(), sub);
                EpicTask epic = manager.epicTasks.get(sub.getEpicId());
                epic.addSubTaskId(sub.getId());
                manager.setEpicStatus(epic);
                manager.setEpicTime(epic);
                currentNextId = Math.max(currentNextId, sub.getId());
            }
        }

        JsonArray jsonHistory = JsonParser.parseString(splitJson[splitJson.length - 1]).getAsJsonArray();

        for(JsonElement jsonId : jsonHistory){
            int id = jsonId.getAsInt();
            if(manager.simpleTasks.containsKey(id)){
                manager.historyManager.addTask(manager.simpleTasks.get(id));
            }else if(manager.epicTasks.containsKey(id)){
                manager.historyManager.addTask(manager.epicTasks.get(id));
            }else{
                manager.historyManager.addTask(manager.subTasks.get(id));
            }
        }

        currentNextId++;
        manager.nextId = currentNextId;
        return manager;
    }
}
