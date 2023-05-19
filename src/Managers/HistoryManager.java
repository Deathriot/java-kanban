package Managers;

import Tasks.*;
import java.util.List;

public interface HistoryManager {
    void addTask(SimpleTask task);

    List<SimpleTask> getHistory();

    void remove(int id);
}
