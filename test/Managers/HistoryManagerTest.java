package Managers;

import Tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {
    private HistoryManager manager;
    private SimpleTask testTask1;
    private SimpleTask testTask2;
    private SimpleTask testTask3;

    @BeforeEach
    public void create() {
        manager = new InMemoryHistoryManager();
        testTask1 = new SimpleTask("test1", "test1", Status.NEW, null, null);
        testTask1.setId(1);
        testTask2 = new EpicTask("test2", "test2");
        testTask2.setId(3);
        testTask3 = new SubTask("test3", "test3", Status.NEW, testTask2.getId(),
                null, null);
        testTask3.setId(4);

    }

    @Test
    public void shouldBeEmptyHistory() {
        List<SimpleTask> test = manager.getHistory();
        assertEquals(0, test.size());
    }

    @Test
    public void shouldBeNoDuplicate() {

        manager.addTask(testTask1);
        manager.addTask(testTask1);
        manager.addTask(testTask1);

        assertEquals(1, manager.getHistory().size(), "Одна и та же задача добавлена несколько раз");

        manager.addTask(testTask2);
        manager.addTask(testTask3);
        manager.addTask(testTask2);
        manager.addTask(testTask1);
        manager.addTask(testTask3);
        manager.addTask(testTask2);

        assertEquals(3, manager.getHistory().size(), "Добавление работает некорректно");

        Set<SimpleTask> noDuplicate = new HashSet<>(manager.getHistory());

        assertEquals(noDuplicate.size(), manager.getHistory().size(), "В менеджере содержатся дупликаты");
        //Три раза - чтобы точно
    }

    @Test
    public void shouldRemoveTaskInBeginProperly() {
        manager.addTask(testTask1);
        manager.addTask(testTask2);
        manager.addTask(testTask3);
        manager.remove(testTask1.getId());

        assertEquals(2, manager.getHistory().size(), "После удаления список не уменьшился");

        boolean hasRemovedTask = false;

        for (SimpleTask task : manager.getHistory()) {
            if (testTask1 == task) {
                hasRemovedTask = true;
            }
        }

        assertFalse(hasRemovedTask, "Задача не была удалена");
    }

    @Test
    public void shouldRemoveTaskInMiddleProperly() {
        manager.addTask(testTask1);
        manager.addTask(testTask2);
        manager.addTask(testTask3);
        manager.remove(testTask2.getId());

        assertEquals(2, manager.getHistory().size(), "После удаления список не уменьшился");

        boolean hasRemovedTask = false;

        for (SimpleTask task : manager.getHistory()) {
            if (testTask2 == task) {
                hasRemovedTask = true;
            }
        }

        assertFalse(hasRemovedTask, "Задача не была удалена");
    }

    @Test
    public void shouldRemoveTaskInEndProperly() {
        manager.addTask(testTask1);
        manager.addTask(testTask2);
        manager.addTask(testTask3);
        manager.remove(testTask3.getId());

        assertEquals(2, manager.getHistory().size(), "После удаления список не уменьшился");

        boolean hasRemovedTask = false;

        for (SimpleTask task : manager.getHistory()) {
            if (testTask3 == task) {
                hasRemovedTask = true;
            }
        }

        assertFalse(hasRemovedTask, "Задача не была удалена");
    }

    @Test
    public void shouldSafeTasksInProperOrder() {
        manager.addTask(testTask1);
        manager.addTask(testTask2);
        manager.addTask(testTask3);
        manager.addTask(testTask2); //1
        manager.addTask(testTask3); //2
        manager.addTask(testTask1); //3

        boolean task2IsFirst = testTask2 == manager.getHistory().get(0);
        boolean task3IsSecond = testTask3 == manager.getHistory().get(1);
        boolean task1IsThird = testTask1 == manager.getHistory().get(2);

        assertTrue(task2IsFirst && task3IsSecond && task1IsThird,
                "История не хранит задачи в порядке вызова");
    }
}
