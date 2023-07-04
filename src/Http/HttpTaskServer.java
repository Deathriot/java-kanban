package Http;

import Managers.*;
import TaskValidatorExceptions.TaskTimeValidationException;
import TaskValidatorExceptions.TaskTypeValidationException;
import Tasks.*;

import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final String START_URI = "/tasks/"; // с этого начинаются все запросы
    private final HttpTaskManager manager;
    private final HttpServer server;
    private final Gson gson;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer() throws IOException {
        GsonBuilder builder = new GsonBuilder().serializeNulls();
        gson = builder.create();

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::manage);
        manager = Managers.getDefault("http://localhost:8081/register");
    }

    private EndPoint getEndpoint(String uri, String method) {
        final String post = "POST";
        final String get = "GET";
        final String delete = "DELETE";

        if (!method.equals(post) && !method.equals(get) && !method.equals(delete)) {
            return EndPoint.WRONG_METHOD; // Так проще ловить все ошибки и обрабатывать их
        }

        if (!uri.contains(START_URI)) {
            return EndPoint.WRONG_URI;
        }

        final String requestUri = uri.substring(START_URI.length()); // оставляем только нужную часть

        if (method.equals(get)) {
            switch (requestUri) {
                case "":
                    return EndPoint.GET_ALL_TASKS;
                case "task/":
                    return EndPoint.GET_ALL_SIMPLE;
                case "epic/":
                    return EndPoint.GET_ALL_EPIC;
                case "sub/":
                    return EndPoint.GET_ALL_SUB;
                case "history/":
                    return EndPoint.GET_HISTORY;
            }

            if (requestUri.contains("subtask/epic/id=")) {
                return EndPoint.GET_ALL_EPIC_SUB;
            }
            if (requestUri.contains("task/id=")) {
                return EndPoint.GET_SIMPLE_BY_ID;
            }
            if (requestUri.contains("epic/id=")) {
                return EndPoint.GET_EPIC_BY_ID;
            }
            if (requestUri.contains("sub/id=")) {
                return EndPoint.GET_SUB_BY_ID;
            }
        }

        if (method.equals(post)) {
            switch (requestUri) {
                case "task/":
                    return EndPoint.POST_ADD_SIMPLE;
                case "epic/":
                    return EndPoint.POST_ADD_EPIC;
                case "sub/":
                    return EndPoint.POST_ADD_SUB;
            }

            //По тз на каждый(!) метод TaskManager должен быть отдельный(!) эндпоинт
            if (requestUri.contains("update/task/id=")) {
                return EndPoint.POST_UPDATE_SIMPLE;
            }
            if (requestUri.contains("update/epic/id=")) {
                return EndPoint.POST_UPDATE_EPIC;
            }
            if (requestUri.contains("update/sub/id=")) {
                return EndPoint.POST_UPDATE_SUB;
            }
        }

        if (method.equals(delete)) {
            switch (requestUri) {
                case "task/":
                    return EndPoint.DELETE_ALL_SIMPLE;
                case "sub/":
                    return EndPoint.DELETE_ALL_SUB;
                case "epic/":
                    return EndPoint.DELETE_ALL_EPIC;
            }

            if (requestUri.contains("task/id=")) {
                return EndPoint.DELETE_SIMPLE_BY_ID;
            }
            if (requestUri.contains("epic/id=")) {
                return EndPoint.DELETE_EPIC_BY_ID;
            }
            if (requestUri.contains("sub/id=")) {
                return EndPoint.DELETE_SUB_BY_ID;
            }
        }

        return EndPoint.UNKNOWN;
    }

    private void manage(HttpExchange exchange) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath() + exchange.getRequestURI().getQuery();

        if (uri.contains("null")) {
            // У меня все сломалось и я три часа все чинил, пожалуйста, не заставляйте меня переделывать эту проверку((
            sb.append(uri);
            sb.delete(sb.indexOf("null"), sb.length());
            uri = sb.toString();
        }

        EndPoint endPoint = getEndpoint(uri, method);
        System.out.println(endPoint);
        String responseBody;
        int statusCode = 200;
        int id = parseTaskId(uri);

        if (id == -1 && uri.contains("/id=")) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("id задачи передан неверно".getBytes(DEFAULT_CHARSET));
            }
            return;
        }

        switch (endPoint) {
            case GET_ALL_TASKS:
                List<SimpleTask> tasks = manager.getPrioritizedTasks();
                responseBody = gson.toJson(tasks);
                break;
            case GET_ALL_SIMPLE:
                List<SimpleTask> simpleTasks = manager.getAllSimpleTasks();
                responseBody = gson.toJson(simpleTasks);
                break;
            case GET_ALL_EPIC:
                List<EpicTask> epicTasks = manager.getAllEpicTasks();
                responseBody = gson.toJson(epicTasks);
                break;
            case GET_ALL_SUB:
                List<SubTask> subTasks = manager.getAllSubTasks();
                responseBody = gson.toJson(subTasks);
                break;
            case GET_SIMPLE_BY_ID:
                SimpleTask simple = manager.getSimpleTask(id);

                if (simple == null) {
                    statusCode = 404;
                    responseBody = "Простая задача с таким id не найдена";
                    break;
                }

                responseBody = gson.toJson(simple);
                break;
            case GET_EPIC_BY_ID:
                EpicTask epic = manager.getEpicTask(id);

                if (epic == null) {
                    statusCode = 404;
                    responseBody = "Эпик с таким id не найден";
                    break;
                }

                responseBody = gson.toJson(epic);
                break;
            case GET_SUB_BY_ID:
                SubTask sub = manager.getSubTask(id);

                if (sub == null) {
                    statusCode = 404;
                    responseBody = "Саб с таким id не найден";
                    break;
                }

                responseBody = gson.toJson(sub);
                break;
            case GET_HISTORY:
                List<SimpleTask> history = manager.getHistory();
                responseBody = gson.toJson(history);
                break;
            case GET_ALL_EPIC_SUB:
                if (manager.getEpicTask(id) == null) {
                    statusCode = 404;
                    responseBody = "Эпик с таким id не найден";
                }else{
                    List<SubTask> epicsSub = manager.getAnEpicSubTasks(id);
                    responseBody = gson.toJson(epicsSub);
                }
                break;
            case POST_ADD_SIMPLE:
                try {
                    SimpleTask simpleTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()
                            , DEFAULT_CHARSET), SimpleTask.class);

                    if (simpleTask.getStatus() == null) { //Если в теле запроса не указать статус задачи, оно будет null
                        throw new JsonSyntaxException("Поле статуса не может быть null");
                    }

                    manager.addSimpleTask(simpleTask);

                    responseBody = "Простая задача успешно добавлена";
                } catch (JsonSyntaxException exception) {
                    statusCode = 400;
                    responseBody = "В тело запроса была неверно передана простая задача:";
                } catch (TaskTimeValidationException exception) {
                    statusCode = 405;
                    responseBody = "Невозможно добавить задачу, задача пересекается по времени с другой";
                } catch (TaskTypeValidationException exception) {
                    statusCode = 405;
                    responseBody = exception.getMessage();
                }
                break;
            case POST_ADD_EPIC:
                try {
                    EpicTask epicTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(),
                            DEFAULT_CHARSET), EpicTask.class);

                    if (epicTask.getStatus() == null) {
                        throw new JsonSyntaxException("Поле статуса не может быть null");
                    }

                    manager.addEpicTask(epicTask);

                    responseBody = "Эпик успешно добавлен";
                } catch (JsonSyntaxException exception) {
                    statusCode = 400;
                    responseBody = "В тело запроса была неверно передан эпик:";
                } catch (TaskTimeValidationException exception) {
                    statusCode = 405;
                    responseBody = "Невозможно добавить задачу, задача пересекается по времени с другой";
                } catch (TaskTypeValidationException exception) {
                    statusCode = 405;
                    responseBody = exception.getMessage();
                }
                break;
            case POST_ADD_SUB:
                try {
                    SubTask subTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()
                            , DEFAULT_CHARSET), SubTask.class);

                    if (subTask.getStatus() == null) {
                        throw new JsonSyntaxException("Поле статуса не может быть null");
                    }

                    manager.addSubTask(subTask); //если у саба нет эпика - задача не добавится

                    if (manager.getSubTask(subTask.getId()) == null) {
                        statusCode = 405;
                        responseBody = "Нельзя добавить саб без эпика";
                    } else {
                        responseBody = "Саб успешно добавлен";
                    }

                } catch (JsonSyntaxException exception) {
                    statusCode = 400;
                    responseBody = "В тело запроса была неверно передан саб:";
                } catch (TaskTimeValidationException exception) {
                    statusCode = 405;
                    responseBody = "Невозможно добавить задачу, задача пересекается по времени с другой";
                } catch (TaskTypeValidationException exception) {
                    statusCode = 405;
                    responseBody = exception.getMessage();
                }
                break;
            case POST_UPDATE_SIMPLE:
                try {
                    SimpleTask simpleTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()
                            , DEFAULT_CHARSET), SimpleTask.class);

                    manager.updateSimpleTask(simpleTask);//если задачи не было изначально, менеджер ничего не сделает

                    if (!manager.getAllSimpleTasks().contains(simpleTask)) {
                        statusCode = 404;
                        responseBody = "Простой задачи с таким id не найдено";
                        break;
                    }

                    responseBody = "Простая задача успешно обновлена";
                } catch (JsonSyntaxException exception) {
                    statusCode = 400;
                    responseBody = "В тело запроса была неверно передана простая задача";
                } catch (TaskTimeValidationException exception) {
                    statusCode = 405;
                    responseBody = "Невозможно обновить задачу, задача пересекается по времени с другой";
                } catch (TaskTypeValidationException exception) {
                    statusCode = 405;
                    responseBody = exception.getMessage();
                }
                break;
            case POST_UPDATE_EPIC:
                try {
                    EpicTask epicTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(),
                            DEFAULT_CHARSET), EpicTask.class);
                    manager.updateEpicTask(epicTask);

                    if (!manager.getAllEpicTasks().contains(epicTask)) {
                        statusCode = 404;
                        responseBody = "Эпика с таким id не найдено";
                        break;
                    }

                    responseBody = "Эпик успешно обновлен";
                } catch (JsonSyntaxException exception) {
                    statusCode = 400;
                    responseBody = "В тело запроса была неверно передан эпик";
                } catch (TaskTypeValidationException exception) {
                    statusCode = 405;
                    responseBody = exception.getMessage();
                }
                break;
            case POST_UPDATE_SUB:
                try {
                    SubTask subTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()
                            , DEFAULT_CHARSET), SubTask.class);
                    manager.updateSubTask(subTask);

                    if (!manager.getAllSubTasks().contains(subTask)) {
                        statusCode = 404;
                        responseBody = "Эпика с таким id не найдено";
                        break;
                    }

                    responseBody = "Саб успешно обновлен";

                } catch (JsonSyntaxException exception) {
                    statusCode = 400;
                    responseBody = "В тело запроса была неверно передан саб";
                } catch (TaskTimeValidationException exception) {
                    statusCode = 405;
                    responseBody = "Невозможно обновить задачу, задача пересекается по времени с другой";
                } catch (TaskTypeValidationException exception) {
                    statusCode = 405;
                    responseBody = exception.getMessage();
                }
                break;
            case DELETE_ALL_SIMPLE:
                manager.removeAllSimpleTasks();
                responseBody = "Все простые задачи успешно удалены";
                break;
            case DELETE_ALL_EPIC:
                manager.removeAllEpicTasks();
                responseBody = "Все эпики успешно удалены";
                break;
            case DELETE_ALL_SUB:
                manager.removeAllSubTasks();
                responseBody = "Все сабы успешно удалены";
                break;
            case DELETE_SIMPLE_BY_ID:
                if (manager.getSimpleTask(id) == null) {
                    responseBody = "Удаляемой задачи не существует.";
                    statusCode = 404;
                    break;
                }

                manager.removeSimpleTask(id);
                responseBody = "Простая задача успешно удалена";
                break;
            case DELETE_EPIC_BY_ID:
                if (manager.getEpicTask(id) == null) {
                    responseBody = "Удаляемой задачи не существует.";
                    statusCode = 404;
                    break;
                }

                manager.removeEpicTask(id);
                responseBody = "Эпик успешно удален";
                break;
            case DELETE_SUB_BY_ID:
                if (manager.getSubTask(id) == null) {
                    responseBody = "Удаляемой задачи не существует.";
                    statusCode = 404;
                    break;
                }

                manager.removeSubTask(id);
                responseBody = "Саб успешно удален";
                break;
            case WRONG_METHOD:
                statusCode = 501;
                responseBody = "Был вызван неизвестный метод: " + method;
                break;
            case WRONG_URI:
                statusCode = 404;
                responseBody = "Указан неверный Эндпоинт: " + uri;
                break;
            default:
                statusCode = 400;
                responseBody = "Ошибка в запросе: " + uri;
                break;
        }

        exchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody.getBytes(DEFAULT_CHARSET));
        }
    }

    // возвращает -1 Если айди нельзя распарсить или айди был меньше еденицы
    private int parseTaskId(String uri) {

        String[] split = uri.split("/");
        String taskId = split[split.length - 1].substring("id=".length());

        try {
            int id = Integer.parseInt(taskId);

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    public HttpTaskManager getManager() {
        return manager; // Для простоты тестов
    }
}
