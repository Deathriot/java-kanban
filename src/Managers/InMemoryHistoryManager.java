package Managers;

import Tasks.SimpleTask;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    private final ArrayList<SimpleTask> history = new ArrayList<>(10);
    @Override
    public void addTask(SimpleTask task) {
        if(history.size() == 10){
            history.remove(0);
        }

        history.add(task);
    }

    @Override
    public ArrayList<SimpleTask> getHistory() {
        return new ArrayList<>(history);
    }
}
