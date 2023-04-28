import Tasks.*;
import java.util.ArrayList;

public interface HistoryManager {
    public void addTask(SimpleTask task);

    public ArrayList<SimpleTask> getHistory();
}
