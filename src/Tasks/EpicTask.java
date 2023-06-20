package Tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends SimpleTask{
    private final ArrayList<Integer> subTasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public EpicTask(String title, String description) {
        super(title, description,Status.NEW, null, null);
        type = TaskType.EPICTASK;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }
    public void addAllSubTasksId(List<Integer> subIdList){
        subTasksId.addAll(subIdList);
    }

    public void clearSubTasksId(){
        subTasksId.clear();
        setStatus(Status.NEW);
    }
    public void addSubTaskId(int id){
        subTasksId.add(id);
    }
    public void removeSubTask(Integer subTaskId){
        subTasksId.remove(subTaskId);
    }

    @Override
    public String toString() {
        return id + "," + type + "," + title + "," + status + "," + description +","
                + startTime + "," + duration + ",";
    }

    public void setEndTime(LocalDateTime endTime){
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

}
