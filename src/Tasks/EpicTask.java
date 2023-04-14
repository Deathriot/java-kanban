package Tasks;

import java.util.ArrayList;

public class EpicTask extends SimpleTask{
    private ArrayList<Integer> subTasksId = new ArrayList<>();

    public EpicTask(String title, String description) {
        super(title, description,"NEW");
    }

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }
    public void setSubTasks(ArrayList<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }

    public void ClearSubTasksId(){
        subTasksId.clear();
        setStatus("NEW");
    }
    public void addSubTaskId(int id){
        subTasksId.add(id);
    }
    public void removeSubTask(Integer subTaskId){
        subTasksId.remove(subTaskId);
    }
}