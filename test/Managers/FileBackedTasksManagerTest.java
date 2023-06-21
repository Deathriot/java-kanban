package Managers;

import ManagerExceptions.ManagerSaveException;
import Tasks.EpicTask;
import Tasks.SimpleTask;
import Tasks.Status;
import Tasks.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    private static final String ERROR_MESSAGE = "При сохранении файла произошла ошибка!";
    private final String path = "src\\TasksTest.csv";
    @BeforeEach
    public void createFileBackedManager(){
        manager = new FileBackedTasksManager(new File(path));
        manager.removeAllEpicTasks();
        manager.removeAllSimpleTasks();
    }

    @Test
    public void shouldBeNoTasksWhenLoaded(){
        //менеджер уже создался
        manager.addSimpleTask(new SimpleTask("simple","simple1", Status.NEW,null, null));
        manager.removeSimpleTask(1);
        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File(path));
        assertEquals(0, manager2.getAllSimpleTasks().size());
    }

    @Test
    public void shouldLoadProperlyEpicWithNoSubs(){
        EpicTask testEpic = new EpicTask("epic", "epic");
        manager.addEpicTask(testEpic);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File(path));

        assertEquals(testEpic, manager2.getEpicTask(testEpic.getId()));
        assertEquals(0, manager2.getAnEpicSubTasks(testEpic).size());
    }

    @Test
    public void shouldLoadEmptyHistoryCorrectly(){
        EpicTask testEpic = new EpicTask("epic", "epic");
        manager.addEpicTask(testEpic);
        manager.getEpicTask(testEpic.getId());
        manager.getEpicTask(testEpic.getId());
        manager.getEpicTask(testEpic.getId());

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File(path));

        assertEquals(testEpic, manager2.getHistory().get(0));
    }

    @Test
    public void loadFromFileTest(){
        EpicTask testEpic = new EpicTask("epic", "epic");
        manager.addEpicTask(testEpic);
        SubTask testSub = new SubTask("sub", "sub", Status.NEW, testEpic, null, null);
        SimpleTask testSimple = new SimpleTask("simple", "simple", Status.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofMinutes(100));

        manager.addSubTask(testSub);
        manager.addSimpleTask(testSimple);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File(path));

        assertEquals(testEpic, manager2.getEpicTask(testEpic.getId()));
        assertEquals(testSub, manager2.getAnEpicSubTasks(testEpic).get(0));
        assertEquals(testSub, manager2.getSubTask(testSub.getId()));
        assertEquals(testSimple, manager2.getSimpleTask(testSimple.getId()));

        assertEquals(3, manager2.getHistory().size());
    }

    @Test
    public void shouldReturnCustomExWhenLoadError(){
        String wrongPath = "abcd";

        ManagerSaveException ex =  assertThrows(ManagerSaveException.class,
                ()-> FileBackedTasksManager.loadFromFile(new File(wrongPath)));

        assertEquals(ERROR_MESSAGE, ex.getMessage());
    }
}
