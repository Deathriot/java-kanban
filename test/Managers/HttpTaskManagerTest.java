package Managers;

import Http.HttpTaskServer;
import Http.KVServer;
import Tasks.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final String START_URI = "http://localhost:8080/tasks/";
    private static final String KVSERVER_URI = "http://localhost:8081/register";
    private final static Gson gson = new GsonBuilder().serializeNulls().create();
    private KVServer kvServer;
    private HttpTaskServer server;
    private HttpTaskManager testManager;
    private HttpClient client;
    private EpicTask testEpic;
    private SimpleTask testSimple;
    private SubTask testSub;


    @BeforeEach
    public void create() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
        manager = Managers.getDefault(KVSERVER_URI);
        client = HttpClient.newHttpClient();

        //Так намного проще тестировать все эндпоинты
        testManager = server.getManager();
    }

    @BeforeEach
    public void createJsonTasks() {
        testEpic = new EpicTask("epicName", "epicDisc");
        testSimple = new SimpleTask("simpleName", "simpleDisc", Status.NEW
                , LocalDateTime.of(1000, 1, 1, 1, 1), Duration.ofMinutes(40));
        testSub = new SubTask("subName", "subDisc",
                Status.NEW, 2, null, null);
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        server.stop();
    }

    @Test
    public void getEndpointsTest() throws IOException, InterruptedException {
        testManager.addSimpleTask(testSimple);
        testManager.addEpicTask(testEpic);
        testManager.addSubTask(testSub);

        final int simpleId = testSimple.getId();
        final int subId = testSub.getId();
        final int epicId = testEpic.getId();

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "task/?id=" + simpleId)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(testSimple), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "task/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SimpleTask> simpleList = List.of(testSimple);
        assertEquals(gson.toJson(simpleList), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "epic/?id=" + epicId)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(testEpic), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "epic/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<EpicTask> epicList = List.of(testEpic);
        assertEquals(gson.toJson(epicList), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "sub/?id=" + subId)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(testSub), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "sub/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> subList = List.of(testSub);
        assertEquals(gson.toJson(subList), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "history/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SimpleTask> historyList = List.of(testSimple, testEpic, testSub);
        assertEquals(gson.toJson(historyList), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SimpleTask> AllTaskList = List.of(testSimple, testSub); //getPrioritized не показывает эпики
        assertEquals(gson.toJson(AllTaskList), response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "subtask/epic/?id=" + epicId)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> epicsSubList = List.of(testSub);
        assertEquals(gson.toJson(epicsSubList), response.body());


        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "task/?id=20")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Простая задача с таким id не найдена", response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "epic/?id=10")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Эпик с таким id не найден", response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "sub/?id=1")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Саб с таким id не найден", response.body());

        request = HttpRequest.newBuilder().GET().uri(URI.create(START_URI + "subtask/epic/?id=5")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Эпик с таким id не найден", response.body());
    }

    @Test
    public void postEndpointsTest() throws IOException, InterruptedException{
        String jsonSimple = gson.toJson(testSimple);
        String jsonEpic = gson.toJson(testEpic);
        String jsonSub = gson.toJson(testSub);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonSimple);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "task/")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Простая задача успешно добавлена", response.body());
        testSimple.setId(1);
        assertEquals(testSimple, testManager.getSimpleTask(1));

        body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "epic/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Эпик успешно добавлен", response.body());
        testEpic.setId(2);
        assertEquals(testEpic, testManager.getEpicTask(2));

        body = HttpRequest.BodyPublishers.ofString(jsonSub);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "sub/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Саб успешно добавлен", response.body());
        testSub.setId(3);
        assertEquals(testSub, testManager.getSubTask(3));

        testSimple.setTitle("updated");
        testSub.setTitle("updated");
        testEpic.setTitle("updated");
        testEpic.setDuration(Duration.ZERO);

        jsonSimple = gson.toJson(testSimple);
        jsonEpic = gson.toJson(testEpic);
        jsonSub = gson.toJson(testSub);

        body = HttpRequest.BodyPublishers.ofString(jsonSimple);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "update/task/?id=1")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Простая задача успешно обновлена", response.body());
        assertEquals(testSimple, testManager.getSimpleTask(1));

        body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "update/epic/?id=2")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Эпик успешно обновлен", response.body());
        assertEquals(testEpic, testManager.getEpicTask(2));

        body = HttpRequest.BodyPublishers.ofString(jsonSub);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "update/sub/?id=3")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Саб успешно обновлен", response.body());
        assertEquals(testSub, testManager.getSubTask(3));


        String wrongJsonSimple = "i am simpleTask, trust me";
        body = HttpRequest.BodyPublishers.ofString(wrongJsonSimple);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "task/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("В тело запроса была неверно передана простая задача:", response.body());

        String wrongJsonEpic = "{\"subTasksId\":[],\"type\":\"EPICTASK\",\"title\":\"i am correct!\",\"description\":" +
                "\"and YOU are broken!\",\"stus\":\"NEW\",\"id\":0}";
        body = HttpRequest.BodyPublishers.ofString(wrongJsonEpic);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "epic/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("В тело запроса была неверно передан эпик:", response.body());

        String wrongJsonSub = "SubTask: not sus";
        body = HttpRequest.BodyPublishers.ofString(wrongJsonSub);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "sub/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("В тело запроса была неверно передан саб:", response.body());

        SimpleTask crossTimeSimple = new SimpleTask("name", "desc", Status.NEW
                ,LocalDateTime.of(1000,1,1,0,0), Duration.ofMinutes(200));
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(crossTimeSimple));
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "task/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("Невозможно добавить задачу, задача пересекается по времени с другой", response.body());


        String wrongTypeSimple = gson.toJson(testSimple).replace("SIMPLETASK", "SUBTASK");
        body = HttpRequest.BodyPublishers.ofString(wrongTypeSimple);
        request = HttpRequest.newBuilder().POST(body).uri(URI.create(START_URI + "task/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("Неверный тип задачи, ожидалось: SIMPLETASK ,а было получено: SUBTASK", response.body());
    }

    @Test
    public void deleteEndPointsTest() throws IOException, InterruptedException{
        testManager.addSimpleTask(testSimple);
        testManager.addEpicTask(testEpic);
        testManager.addSubTask(testSub);

        final List<SimpleTask> emptyList = List.of();

        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "task/")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все простые задачи успешно удалены", response.body());
        assertEquals(emptyList, testManager.getAllSimpleTasks());

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "sub/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все сабы успешно удалены", response.body());
        assertEquals(emptyList, testManager.getAllSubTasks());

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "epic/")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все эпики успешно удалены", response.body());
        assertEquals(emptyList, testManager.getAllEpicTasks());

        testManager.addSimpleTask(testSimple);
        testManager.addEpicTask(testEpic);
        testSub.setEpicId(5);
        testManager.addSubTask(testSub);

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "task/?id=4")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Простая задача успешно удалена", response.body());
        assertEquals(emptyList, testManager.getAllSimpleTasks());

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "sub/?id=6")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Саб успешно удален", response.body());
        assertEquals(emptyList, testManager.getAllSubTasks());

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "epic/?id=5")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Эпик успешно удален", response.body());
        assertEquals(emptyList, testManager.getAllEpicTasks());

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "task/?id=4")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Удаляемой задачи не существует.", response.body());

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "sub/?id=6")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Удаляемой задачи не существует.", response.body());

        request = HttpRequest.newBuilder().DELETE().uri(URI.create(START_URI + "epic/?id=5")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Удаляемой задачи не существует.", response.body());
    }

    @Test
    public void shouldBe404WhenWrongEndpoint() throws IOException, InterruptedException{
        final String wrongEndpoint = "http://localhost:8080/taskstask";

        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(wrongEndpoint)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Указан неверный Эндпоинт: " +"/taskstask", response.body());
    }

    @Test
    public void shouldBe501WhenUnknownHTTPMethod() throws IOException, InterruptedException{
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSimple));

        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(START_URI)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(501, response.statusCode());
        assertEquals("Был вызван неизвестный метод: PUT", response.body());
    }

    @Test
    public void loadFromServerTest(){
        manager.addSimpleTask(testSimple);
        manager.addEpicTask(testEpic);
        manager.addSubTask(testSub);

        manager.getSimpleTask(testSimple.getId());
        manager.getSimpleTask(testSimple.getId());
        manager.getEpicTask(testEpic.getId());
        manager.getSubTask(testSub.getId());
        List<SimpleTask> history = manager.getHistory();

        HttpTaskManager newManager = HttpTaskManager.loadData();

        assertEquals(history, newManager.getHistory());
        assertEquals(testSimple, newManager.getSimpleTask(testSimple.getId()));
        assertEquals(1, newManager.getAllSimpleTasks().size());
        assertEquals(testEpic, newManager.getEpicTask(testEpic.getId()));
        assertEquals(1, newManager.getAllSubTasks().size());
        assertEquals(testSub, newManager.getSubTask(testSub.getId()));
        assertEquals(1, newManager.getAllEpicTasks().size());
    }

}
