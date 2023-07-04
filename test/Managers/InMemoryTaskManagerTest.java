package Managers;

import Tasks.EpicTask;
import Tasks.SimpleTask;
import Tasks.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void createInMemoryManager() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void newManagerShouldNoSaveTasks() {
        manager.addEpicTask(new EpicTask("epic", "epic"));
        manager.addSimpleTask(new SimpleTask("simple", "simple", Status.NEW, null, null));

        InMemoryTaskManager manager2 = new InMemoryTaskManager();
        String erorrMessage = "Новый менеджер задач не пустой";

        assertEquals(0, manager2.getAllEpicTasks().size(), erorrMessage);
        assertEquals(0, manager2.getAllSimpleTasks().size(), erorrMessage);
        assertEquals(0, manager2.getAllSubTasks().size(), erorrMessage);
    }
}
