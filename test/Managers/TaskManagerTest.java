package Managers;

import TaskValidatorExceptions.TaskTimeValidationException;
import Tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    private EpicTask testEpic;
    private SimpleTask testSimple;
    private SubTask testSub;

    @BeforeEach
    public void createTasks() {
        testEpic = new EpicTask("epicName", "epicDisc");
        testSimple = new SimpleTask("simpleName", "simpleDisc", Status.NEW, null, null);
        testSub = new SubTask("subName", "subDisc", Status.NEW, 0, null, null);
    }

    @Test
    public void subShouldHaveEpic() {
        testEpic.setId(1);
        SubTask testSub = new SubTask("sub", "sub", Status.NEW, 1,
                null, null);
        manager.addSubTask(testSub);
        SubTask nullExpected = manager.getSubTask(testSub.getId());
        assertNull(nullExpected, "Саб не может существовать без эпика в менеджере");

        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());
        manager.removeEpicTask(testEpic.getId());
        manager.addSubTask(testSub);

        nullExpected = manager.getSubTask(testSub.getId());
        assertNull(nullExpected, "Саб не может существовать без эпика в менеджере");
    }

    @Test
    public void TestEpicStatus() {
        manager.addEpicTask(testEpic);
        assertEquals(Status.NEW, testEpic.getStatus(), "Пустой эпик должен иметь статус NEW");

        testEpic = new EpicTask("epicName", "epicDisc");
        manager.addEpicTask(testEpic);
        manager.addSubTask(new SubTask("sub1", "sub1Desc", Status.NEW
                , testEpic.getId(), null, null));
        manager.addSubTask(new SubTask("sub2", "sub2Desc", Status.NEW
                , testEpic.getId(), null, null));
        assertEquals(Status.NEW, testEpic.getStatus(),
                "Когда все сабы имеют статус NEW, эпик должен иметь статус NEW");

        testEpic = new EpicTask("epicName", "epicDisc");
        manager.addEpicTask(testEpic);
        manager.addSubTask(new SubTask("sub1", "sub1Desc", Status.DONE
                , testEpic.getId(), null, null));
        manager.addSubTask(new SubTask("sub2", "sub2Desc", Status.DONE
                , testEpic.getId(), null, null));
        assertEquals(Status.DONE, testEpic.getStatus(),
                "Когда все сабы имеют статус DONE, эпик должен иметь статус DONE");

        testEpic = new EpicTask("epicName", "epicDisc");
        manager.addEpicTask(testEpic);
        manager.addSubTask(new SubTask("sub1", "sub1Desc", Status.NEW,
                testEpic.getId(), null, null));
        manager.addSubTask(new SubTask("sub2", "sub2Desc", Status.DONE,
                testEpic.getId(), null, null));
        assertEquals(Status.IN_PROGRESS, testEpic.getStatus(),
                "Если эпик имеет сабы со статусами NEW и DONE, статус эпика должен быть IN_PROGRESS");

        testEpic = new EpicTask("epicName", "epicDisc");
        manager.addEpicTask(testEpic);
        manager.addSubTask(new SubTask("sub1", "sub1Desc", Status.IN_PROGRESS
                , testEpic.getId(), null, null));
        manager.addSubTask(new SubTask("sub2", "sub2Desc", Status.IN_PROGRESS,
                testEpic.getId(), null, null));
        assertEquals(Status.IN_PROGRESS, testEpic.getStatus(),
                "Если эпик имеет саб со статусом IN_PROGRESS, эпик должен иметь статус IN_PROGRESS");
    }

    @Test
    public void addSimpleTaskTest() {
        manager.addSimpleTask(null);
        assertEquals(0, manager.getAllSimpleTasks().size(), "Нельзя добавить несуществующую задачу");

        manager.addSimpleTask(testSimple);
        SimpleTask testSimple2 = manager.getSimpleTask(testSimple.getId());
        assertNotNull(testSimple2, "Задача не найдена");
        assertSame(testSimple2, testSimple, "задачи не совпадают");

        final List<SimpleTask> listSimple = manager.getAllSimpleTasks();

        assertNotNull(listSimple, "Задачи не возвращаются");
        assertEquals(1, listSimple.size(), "Неверное количество задач");
        assertEquals(testSimple, listSimple.get(0), "Задачи не совпадают");
    }

    @Test
    public void addSubTaskTest() {
        manager.addSubTask(null);
        assertEquals(0, manager.getAllSubTasks().size(), "Нельзя добавить несуществующую задачу");

        manager.addEpicTask(testEpic); // нельзя добавлять сабы без добавление эпика (Тест на это уже есть)
        testSub.setEpicId(testEpic.getId());

        manager.addSubTask(testSub);
        SubTask testSub2 = manager.getSubTask(testSub.getId());
        assertNotNull(testSub2, "Задача не найдена");
        assertSame(testSub2, testSub, "задачи не совпадают");

        final List<SubTask> listSub = manager.getAllSubTasks();

        assertNotNull(listSub, "Задачи не возвращаются");
        assertEquals(1, listSub.size(), "Неверное количество задач");
        assertEquals(testSub, listSub.get(0), "Задачи не совпадают");

        assertEquals(testSub, manager.getSubTask(testEpic.getSubTasksId().get(0)), "В эпик не был добавлен саб");
    }

    @Test
    public void addEpicTaskTest() {
        manager.addEpicTask(null);
        assertEquals(0, manager.getAllEpicTasks().size(), "Нельзя добавить несуществующую задачу");

        manager.addEpicTask(testEpic);
        EpicTask testEpic2 = manager.getEpicTask(testEpic.getId());
        assertNotNull(testEpic2, "Задача не найдена");
        assertSame(testEpic2, testEpic, "задачи не совпадают");

        final List<EpicTask> listEpic = manager.getAllEpicTasks();

        assertNotNull(listEpic, "Задачи не возвращаются");
        assertEquals(1, listEpic.size(), "Неверное количество задач");
        assertEquals(testEpic, listEpic.get(0), "Задачи не совпадают");
        assertEquals(0, testEpic.getSubTasksId().size(), "У эпика не должно быть сабов");
    }

    @Test
    public void getAllSimpleTasksTest() {
        assertEquals(0, manager.getAllSimpleTasks().size(), "список задач должен быть пустым");

        manager.addSimpleTask(testSimple);
        SimpleTask testSimple2 = manager.getAllSimpleTasks().get(0);
        assertNotNull(testSimple2, "Задача не найдена");
        assertSame(testSimple2, testSimple, "задачи не совпадают");

        final List<SimpleTask> listSimple = manager.getAllSimpleTasks();

        assertNotNull(listSimple, "Задачи не возвращаются");
        assertEquals(1, listSimple.size(), "Неверное количество задач");
        assertEquals(testSimple, listSimple.get(0), "Задачи не совпадают");

        testSimple2 = new SimpleTask("simple2", "simple2", Status.DONE, null, null);
        manager.addSimpleTask(testSimple2);
        assertEquals(2, manager.getAllSimpleTasks().size(), "Выдается неполный список задач");
    }

    @Test
    public void getAllSubTasksTest() {
        assertEquals(0, manager.getAllSubTasks().size(), "список задач должен быть пустым");

        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());

        manager.addSubTask(testSub);
        SubTask testSub2 = manager.getAllSubTasks().get(0);
        assertNotNull(testSub2, "Задача не найдена");
        assertSame(testSub2, testSub, "задачи не совпадают");

        final List<SubTask> listSub = manager.getAllSubTasks();

        assertNotNull(listSub, "Задачи не возвращаются");
        assertEquals(1, listSub.size(), "Неверное количество задач");
        assertEquals(testSub, listSub.get(0), "Задачи не совпадают");

        testSub2 = new SubTask("sub2", "sub2", Status.NEW,
                testEpic.getId(), null, null);
        manager.addSubTask(testSub2);
        assertEquals(2, manager.getAllSubTasks().size(), "Выдается неполный список задач");
    }

    @Test
    public void getAllEpicTasksTest() {
        assertEquals(0, manager.getAllEpicTasks().size(), "список задач должен быть пустым");

        manager.addEpicTask(testEpic);
        EpicTask testEpic2 = manager.getAllEpicTasks().get(0);
        assertNotNull(testEpic2, "Задача не найдена");
        assertSame(testEpic2, testEpic, "задачи не совпадают");

        final List<EpicTask> listEpic = manager.getAllEpicTasks();

        assertNotNull(listEpic, "Задачи не возвращаются");
        assertEquals(1, listEpic.size(), "Неверное количество задач");
        assertEquals(testEpic, listEpic.get(0), "Задачи не совпадают");

        testEpic2 = new EpicTask("epic2", "epic2");
        manager.addEpicTask(testEpic2);
        assertEquals(2, manager.getAllEpicTasks().size(), "Выдается неполный список задач");
    }

    @Test
    public void getSimpleTaskByIdTest() {
        manager.addSimpleTask(testSimple);
        SimpleTask testSimple2 = manager.getSimpleTask(testSimple.getId());
        assertNotNull(testSimple2, "Задача не найдена");
        assertSame(testSimple2, testSimple, "задачи не совпадают");

        SimpleTask nullExpected = manager.getSimpleTask(100500);
        assertNull(nullExpected, "Менеджер некорректно выдает несуществующую задачу");
    }

    @Test
    public void getSubTaskByIdTest() {
        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());

        manager.addSubTask(testSub);
        SubTask testSub2 = manager.getSubTask(testSub.getId());
        assertNotNull(testSub2, "Задача не найдена");
        assertSame(testSub2, testSub, "задачи не совпадают");

        SubTask nullExpected = manager.getSubTask(100500);
        assertNull(nullExpected, "Менеджер некорректно выдает несуществующую задачу");
    }

    @Test
    public void getEpicTaskByIdTest() {
        manager.addEpicTask(testEpic);
        EpicTask testEpic2 = manager.getEpicTask(testEpic.getId());
        assertNotNull(testEpic2, "Задача не найдена");
        assertSame(testEpic2, testEpic, "задачи не совпадают");

        EpicTask nullExpected = manager.getEpicTask(100500);
        assertNull(nullExpected, "Менеджер некорректно выдает несуществующую задачу");
    }

    @Test
    public void removeAllSimpleTasksTest() {
        manager.addSimpleTask(testSimple);
        SimpleTask testSimple2 = new SimpleTask("simple2", "simple2,",
                Status.NEW, null, null);
        manager.addSimpleTask(testSimple2);

        manager.removeAllSimpleTasks();

        assertEquals(0, manager.getAllSimpleTasks().size(), "Список задач не очищен");
        assertNull(manager.getSimpleTask(testSimple.getId()), "Задача была не удалена");
        assertNull(manager.getSimpleTask(testSimple2.getId()), "Задача была не удалена");
    }

    @Test
    public void removeAllSubTasksTest() {
        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());
        SubTask testSub2 = new SubTask("sub2", "sub2", Status.NEW
                , testEpic.getId(), null, null);
        manager.addSubTask(testSub);
        manager.addSubTask(testSub2);

        manager.removeAllSubTasks();

        assertEquals(0, manager.getAllSubTasks().size(), "Список задач не очищен");
        assertNull(manager.getSubTask(testSub.getId()), "Задача была не удалена");
        assertNull(manager.getSubTask(testSub2.getId()), "Задача была не удалена");

        assertEquals(0, testEpic.getSubTasksId().size(), "Сабы не удаляются у Эпика");
    }

    @Test
    public void removeAllEpicTasksTest() {
        manager.addEpicTask(testEpic);
        EpicTask testEpic2 = new EpicTask("epic2", "epic2");
        SubTask epicsSub1 = new SubTask("epicsSub1", "epicsSub1", Status.NEW
                , testEpic.getId(), null, null);
        SubTask epicsSub2 = new SubTask("epicsSub2", "epicsSub2", Status.NEW
                , testEpic.getId(), null, null);
        manager.addSubTask(epicsSub1);
        manager.addSubTask(epicsSub2);
        manager.addEpicTask(testEpic2);

        manager.removeAllEpicTasks();

        assertEquals(0, manager.getAllEpicTasks().size(), "Список задач не очищен");
        assertNull(manager.getEpicTask(testEpic.getId()), "Задача была не удалена");
        assertNull(manager.getEpicTask(testEpic2.getId()), "Задача была не удалена");

        assertEquals(0, manager.getAllSubTasks().size(), "Все сабы должны быть удалены");
        assertNull(manager.getSubTask(epicsSub1.getId()), "Саб не был  удален");
        assertNull(manager.getSubTask(epicsSub2.getId()), "Саб не был  удален");
    }

    @Test
    public void removeSimpleTaskByIdTest() {
        manager.addSimpleTask(testSimple);
        SimpleTask testSimple2 = new SimpleTask("simple2", "simple2,",
                Status.NEW, null, null);
        manager.addSimpleTask(testSimple2);

        manager.removeSimpleTask(testSimple.getId());
        manager.removeSimpleTask(testSimple2.getId());

        assertEquals(0, manager.getAllSimpleTasks().size(), "Не все задачи были удалены");
        assertNull(manager.getSimpleTask(testSimple.getId()), "Задача была не удалена");
        assertNull(manager.getSimpleTask(testSimple2.getId()), "Задача была не удалена");
    }

    @Test
    public void removeSubTaskByIdTest() {
        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());
        SubTask testSub2 = new SubTask("sub2", "sub2", Status.NEW
                , testEpic.getId(), null, null);
        manager.addSubTask(testSub);
        manager.addSubTask(testSub2);

        manager.removeSubTask(testSub.getId());
        assertEquals(1, testEpic.getSubTasksId().size(), "У эпика должен был отстаться один саб");
        manager.removeSubTask(testSub2.getId());

        assertEquals(0, testEpic.getSubTasksId().size(), "У эпика не должно было остаться сабов");
        assertEquals(0, manager.getAllSubTasks().size(), "Не все задачи были удалены");
        assertNull(manager.getSubTask(testSub.getId()), "Задача была не удалена");
        assertNull(manager.getSubTask(testSub2.getId()), "Задача была не удалена");
    }

    @Test
    public void removeEpicTaskByIdTest() {
        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());
        manager.addSubTask(testSub);
        EpicTask testEpic2 = new EpicTask("epic2", "epic2");
        manager.addEpicTask(testEpic2);

        manager.removeEpicTask(testEpic.getId());
        manager.removeEpicTask(testEpic2.getId());

        assertEquals(0, manager.getAllEpicTasks().size(), "Не все задачи были удалены");
        assertNull(manager.getEpicTask(testEpic.getId()), "Задача была не удалена");
        assertNull(manager.getEpicTask(testEpic.getId()), "Задача была не удалена");

        assertNull(manager.getSubTask(testSub.getId()), "При удалении Эпика его сабы не удалились");
    }

    @Test
    public void updateSimpleTaskTest() {
        manager.addSimpleTask(testSimple);
        SimpleTask updatedSimple = new SimpleTask("update", "update", Status.DONE, null, null);
        updatedSimple.setId(testSimple.getId());

        manager.updateSimpleTask(updatedSimple);

        assertNotEquals(testSimple, manager.getSimpleTask(testSimple.getId()), "Задача не обновилась!");
        assertEquals(updatedSimple, manager.getSimpleTask(updatedSimple.getId()), "Задача не обновилась корректно");

        assertEquals(1, manager.getAllSimpleTasks().size(), "Количество задач не должно меняться");
    }

    @Test
    public void updateSubTaskTest() {
        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());
        manager.addSubTask(testSub);

        SubTask updatedSub = new SubTask("update", "update", Status.DONE
                , testEpic.getId(), null, null);
        updatedSub.setId(testSub.getId());

        manager.updateSubTask(updatedSub);

        assertNotEquals(testSub, manager.getSubTask(testSub.getId()), "Задача не обновилась!");
        assertEquals(updatedSub, manager.getSubTask(updatedSub.getId()), "Задача не обновилась корректно");

        assertEquals(1, manager.getAllSubTasks().size(), "Количество задач не должно меняться");

        assertEquals(updatedSub, manager.getSubTask(testEpic.getSubTasksId().get(0)), "У эпика не обновился саб");
    }

    @Test
    public void updateEpicTaskTest() {
        manager.addEpicTask(testEpic);
        EpicTask updatedEpic = new EpicTask("update", "update");
        updatedEpic.setId(testEpic.getId());
        testSub.setEpicId(testEpic.getId());
        manager.addSubTask(testSub);

        manager.updateEpicTask(updatedEpic);

        assertNotEquals(testEpic, manager.getEpicTask(testEpic.getId()), "Задача не обновилась");
        assertEquals(updatedEpic, manager.getEpicTask(updatedEpic.getId()), "Задача не обновилась корректно");

        assertEquals(1, manager.getAllEpicTasks().size(), "Количество задач не должно меняться");

        assertEquals(testSub, manager.getSubTask(updatedEpic.getSubTasksId().get(0)),
                "У обновленного эпика не сохранился саб");
    }

    @Test
    public void getAllEpicsSubTasksTest() {
        manager.addEpicTask(testEpic);
        testSub.setEpicId(testEpic.getId());
        SubTask testSub2 = new SubTask("sub2", "sub2", Status.NEW
                , testEpic.getId(), null, null);
        manager.addSubTask(testSub);
        manager.addSubTask(testSub2);

        assertEquals(2, manager.getAnEpicSubTasks(testEpic.getId()).size(), "Эпик некорректно хранит сабы");
        assertEquals(testSub, manager.getAnEpicSubTasks(testEpic.getId()).get(0), "Менеджер неверно возвращает сабы эпика");
        assertEquals(testSub2, manager.getAnEpicSubTasks(testEpic.getId()).get(1), "Менеджер неверно возвращает сабы эпика");
    }

    @Test
    public void TimeValidationTest() {
        testSimple.setStartTime(LocalDateTime.of(2020, 1, 1, 0, 0));
        testSimple.setDuration(Duration.ofMinutes(120));

        SimpleTask testSimple2 = new SimpleTask("simple2", "simple2", Status.NEW,
                LocalDateTime.of(2020, 1, 1, 1, 0), Duration.ofMinutes(70));
        // Происходит пересечение по времени

        manager.addSimpleTask(testSimple);

        TaskTimeValidationException ex = assertThrows(TaskTimeValidationException.class,
                () -> manager.addSimpleTask(testSimple2), "Задачи не могут пересекаться по времени");

        assertEquals("Активной может быть только одна задача.", ex.getMessage(),
                "Неверное сообщение об ошибке");

        SimpleTask testSimple3 = new SimpleTask("simple3", "simple3", Status.NEW, null, null);

        assertDoesNotThrow(() -> manager.addSimpleTask(testSimple3),
                "Валидатор должен пропускать задачи без заданного времени");

        testSimple3.setStartTime(LocalDateTime.of(2100, 10, 10, 10, 10));
        testSimple3.setDuration(Duration.ofMinutes(200));

        assertDoesNotThrow(() -> manager.addSimpleTask(testSimple3),
                "Валидатор должен пропускать задачи если они не пересекаются");
    }

    @Test
    public void TestEpicTime() {
        manager.addEpicTask(testEpic);
        assertNull(testEpic.getStartTime(), "У эпика без сабов время начала не задано");
        assertNull(testEpic.getEndTime(), "У эпика без сабов время окончания не задано");
        assertNull(testEpic.getDuration(), "У эпика без сабов нет длительности");

        testSub.setEpicId(testEpic.getId());
        manager.addSubTask(testSub);

        assertNull(testEpic.getStartTime(), "У эпика с сабом без начала не может быть начала");
        assertNull(testEpic.getEndTime(), "У эпика с сабом без окончания не может быть окончания");
        assertEquals(testEpic.getDuration(), Duration.ZERO, "У эпика с сабом без длительности не может быть длительности");

        SubTask testSub2 = new SubTask("sub2", "sub2", Status.NEW, testEpic.getId(),
                LocalDateTime.of(2023, 1, 1, 0, 0),
                Duration.ofMinutes(180));

        SubTask testSub3 = new SubTask("sub3", "sub3", Status.NEW, testEpic.getId(),
                LocalDateTime.of(2024, 1, 1, 0, 0),
                Duration.ofMinutes(10));

        SubTask testSub4 = new SubTask("sub4", "sub4", Status.NEW, testEpic.getId(),
                LocalDateTime.of(2024, 2, 1, 0, 0),
                Duration.ofMinutes(20));

        manager.addSubTask(testSub2);
        manager.addSubTask(testSub3);
        manager.addSubTask(testSub4);

        //начало - начало саба 2
        LocalDateTime startTimeExpected = LocalDateTime.of(2023, 1, 1, 0, 0);
        //длительность - сумма длительности трех сабов (у которых она есть)
        Duration durationExpected = Duration.ofMinutes(210);
        //Время окончания - самый поздний саб + длительность (По крайней мере в таком примере)
        LocalDateTime endTimeExpected = LocalDateTime.of(2024, 2, 1, 0, 0)
                .plus(Duration.ofMinutes(20));

        assertEquals(startTimeExpected, testEpic.getStartTime(), "Время начала эпика неверно");
        assertEquals(durationExpected, testEpic.getDuration(), "Длительность эпика неверно");
        assertEquals(endTimeExpected, testEpic.getEndTime(), "Время окончания эпика неверно");
    }

    @Test
    public void prioritizedTasksTest() {
        manager.addEpicTask(testEpic); //Не участвует

        testSub.setEpicId(testEpic.getId());
        testSub.setStartTime(LocalDateTime.of(2020, 1, 1, 1, 1));
        testSub.setDuration(Duration.ofMinutes(60));
        manager.addSubTask(testSub); // 1

        testSimple.setStartTime(LocalDateTime.of(2021, 1, 1, 1, 1));
        testSimple.setDuration(Duration.ofMinutes(30));
        manager.addSimpleTask(testSimple);//3

        EpicTask testEpic2 = new EpicTask("epic2", "epic2");
        manager.addEpicTask(testEpic2); // не участвует
        SubTask testSub2 = new SubTask("sub2", "sub2", Status.NEW, testEpic.getId(),
                LocalDateTime.of(2020, 2, 1, 1, 1), Duration.ofMinutes(20));
        SimpleTask testSimple2 = new SimpleTask("simple2", "simple2", Status.NEW, null, null);
        manager.addSubTask(testSub2); //2

        manager.addSimpleTask(testSimple2);//4

        String errorMessage = "Задачи по приоритетам стоят не в порядке начала времени";

        assertEquals(testSub, manager.getPrioritizedTasks().get(0), errorMessage);
        assertEquals(testSub2, manager.getPrioritizedTasks().get(1), errorMessage);
        assertEquals(testSimple, manager.getPrioritizedTasks().get(2), errorMessage);
        assertEquals(testSimple2, manager.getPrioritizedTasks().get(3), errorMessage);

        assertEquals(4, manager.getPrioritizedTasks().size(), "Эпики не хранятся в списке задач по приоритету");

        manager.removeSimpleTask(testSimple2.getId());
        manager.removeSubTask(testSub2.getId());

        assertEquals(2, manager.getPrioritizedTasks().size(), "Задачи не удаляются из списка задач по приоритету");
    }
}
