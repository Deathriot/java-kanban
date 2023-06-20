package Managers;

import org.junit.jupiter.api.BeforeEach;


public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @BeforeEach
    public void createInMemoryManager(){
        manager = new InMemoryTaskManager();
    }
}
