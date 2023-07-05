package Managers;

import Tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import TaskValidatorExceptions.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    protected final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Set<SimpleTask> prioritizedTasks = new TreeSet<>(
            (task1, task2) -> {
                LocalDateTime startTime1 = task1.getStartTime();
                LocalDateTime startTime2 = task2.getStartTime();

                if (task1 == task2) {
                    return 0; // Иначе удаление работает некорекктно
                }

                if (startTime1 == null) {
                    return 1;
                } else if (startTime2 == null) {
                    return -1;
                } else if (startTime1.isAfter(startTime2)) {
                    return 1;
                } else if (startTime1.isBefore(startTime2)) {
                    return -1;
                } else {
                    return 0;
                }
            }
    );

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
    public SimpleTask getSimpleTask(int taskId) {
        SimpleTask simpleTask = simpleTasks.get(taskId);

        historyManager.addTask(simpleTask);
        return simpleTask;
    }

    @Override
    public EpicTask getEpicTask(int taskId) {
        EpicTask epicTask = epicTasks.get(taskId);

        historyManager.addTask(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTask(int taskId) {
        SubTask subTask = subTasks.get(taskId);

        historyManager.addTask(subTask);
        return subTask;
    }

    @Override
    public void removeAllSimpleTasks() {
        for (Integer id : simpleTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(simpleTasks.get(id));
        }
        simpleTasks.clear();

    }

    @Override
    public void removeAllEpicTasks() {
        for (Integer id : epicTasks.keySet()) {
            historyManager.remove(id);
        }

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        }

        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        }

        subTasks.clear();

        for (EpicTask epic : epicTasks.values()) {
            epic.clearSubTasksId();
            setEpic(epic);
        }
    }

    @Override
    public void addSimpleTask(SimpleTask task) {
        if (task == null) {
            return;
        }

        validateTaskByType(task, TaskType.SIMPLETASK);

        task.setId(nextId);
        validateTaskByTime(task);
        nextId++;
        simpleTasks.put(task.getId(), task);
    }

    @Override
    public void addEpicTask(EpicTask task) {
        if (task == null) {
            return;
        }

        validateTaskByType(task, TaskType.EPICTASK);

        task.setId(nextId);
        nextId++;
        epicTasks.put(task.getId(), task);
    }

    @Override
    public void addSubTask(SubTask task) {
        if (task == null) {
            return;
        }

        validateTaskByType(task, TaskType.SUBTASK);

        EpicTask epic = epicTasks.get(task.getEpicId());

        if (epic == null) {
            return;
        }

        task.setId(nextId);
        validateTaskByTime(task);
        nextId++;
        subTasks.put(task.getId(), task);

        epic.addSubTaskId(task.getId());
        setEpic(epic);
    }

    @Override
    public void updateSimpleTask(SimpleTask task) {
        if (simpleTasks.containsKey(task.getId())) {
            validateTaskByType(task, TaskType.SIMPLETASK);
            validateTaskByTime(task);
            simpleTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpicTask(EpicTask task) {
        if (epicTasks.containsKey(task.getId())) {
            validateTaskByType(task, TaskType.EPICTASK);
            EpicTask epic = epicTasks.get(task.getId());
            List<Integer> subIdList = epic.getSubTasksId();
            task.addAllSubTasksId(subIdList);
            setEpic(epic);
            epicTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask task) {
        if (!subTasks.containsKey(task.getId())) {
            return;
        }

        validateTaskByType(task, TaskType.SUBTASK);
        validateTaskByTime(task);
        subTasks.put(task.getId(), task);

        EpicTask epic = epicTasks.get(task.getEpicId());
        setEpic(epic);
    }

    @Override
    public void removeSimpleTask(int id) {
        historyManager.remove(id);
        SimpleTask removedSimple = simpleTasks.remove(id);
        prioritizedTasks.remove(removedSimple);
    }

    @Override
    public void removeEpicTask(int id) {
        historyManager.remove(id);

        EpicTask epic = epicTasks.remove(id);
        List<Integer> epicsSubTasksId = epic.getSubTasksId();

        for (Integer subTaskId : epicsSubTasksId) {
            historyManager.remove(subTaskId);
            SubTask removedSub = subTasks.remove(subTaskId);
            prioritizedTasks.remove(removedSub);
        }

    }

    @Override
    public void removeSubTask(int id) {
        historyManager.remove(id);

        SubTask removedSubTask = subTasks.remove(id);
        prioritizedTasks.remove(removedSubTask);

        EpicTask epic = epicTasks.get(removedSubTask.getEpicId());
        epic.removeSubTask(id);
        setEpic(epic);
    }

    @Override
    public List<SubTask> getAnEpicSubTasks(int epicId) {
        List<SubTask> epicsSubTasks = new ArrayList<>();
        EpicTask epic = epicTasks.get(epicId);
        List<Integer> listSubTaskId = epic.getSubTasksId();

        for (Integer subTaskId : listSubTaskId) {
            epicsSubTasks.add(subTasks.get(subTaskId));
        }
        return epicsSubTasks;
    }

    @Override
    public List<SimpleTask> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<SimpleTask> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void setEpicStatus(EpicTask epic) {
        boolean isAllNew = true;
        boolean isAllDone = true;
        ArrayList<SubTask> tasks = new ArrayList<>();

        for (Integer subTaskId : epic.getSubTasksId()) {
            tasks.add(subTasks.get(subTaskId));
        }

        if (tasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (SubTask task : tasks) {
            if (task.getStatus().equals(Status.IN_PROGRESS)) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }

            if (task.getStatus().equals(Status.NEW)) {
                isAllDone = false;
            } else {
                isAllNew = false;
            }
        }
        if (isAllNew) {
            epic.setStatus(Status.NEW);
        } else if (isAllDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void setEpicTime(EpicTask epic) {
        if (epic.getSubTasksId().isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime startTime = epic.getStartTime();
        LocalDateTime endTime = epic.getEndTime();
        Duration totalDuration = Duration.ZERO;

        for (Integer subId : epic.getSubTasksId()) {
            SubTask sub = subTasks.get(subId);

            if (startTime == null || endTime == null) {
                startTime = sub.getStartTime();
                endTime = sub.getEndTime();
                continue;
            }

            if (sub.getDuration() != null) {
                totalDuration = totalDuration.plus(sub.getDuration());
            }

            if (sub.getStartTime() != null && startTime.isAfter(sub.getStartTime())) {
                startTime = sub.getStartTime();
            }

            if (sub.getEndTime() != null && endTime.isBefore(sub.getEndTime())) {
                endTime = sub.getEndTime();
            }
        }

        epic.setEndTime(endTime);
        epic.setDuration(totalDuration);
        epic.setStartTime(startTime);
    }

    private void validateTaskByTime(SimpleTask testTask) {
        LocalDateTime testStartTime = testTask.getStartTime();
        LocalDateTime testEndTime = testTask.getEndTime();

        if (testStartTime == null || testEndTime == null) {
            prioritizedTasks.add(testTask);
            return;
        }

        for (SimpleTask task : prioritizedTasks) {
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();

            if (startTime == null || endTime == null) {
                continue;
            }

            if(testTask.getId() == task.getId()){ //Сравниваем айди, чтоб не было ошибки при обновлении
                continue;
            }

            if (!(testStartTime.isAfter(endTime) || testEndTime.isBefore(startTime)))
                throw new TaskTimeValidationException();
            }

        prioritizedTasks.add(testTask); //Целесообразным добавлять задачу здесь же
    }

    private void validateTaskByType(SimpleTask task, TaskType correctType) {
        if (!task.getType().equals(correctType)) {
            throw new TaskTypeValidationException(task.getType(), correctType);
        }
    }

    protected void setEpic(EpicTask epic) {
        setEpicTime(epic);
        setEpicStatus(epic);
    }
}
