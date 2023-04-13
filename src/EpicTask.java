import java.util.ArrayList;

public class EpicTask extends Task{
    public EpicTask(String title, String description) {
        super(title, description);
    }
    private ArrayList<SubTask> subTasks = new ArrayList<>();

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }
    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

}
