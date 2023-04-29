package Managers;

import Tasks.*;
import java.util.ArrayList;

public interface HistoryManager {
    void addTask(SimpleTask task);

    ArrayList<SimpleTask> getHistory();
}
