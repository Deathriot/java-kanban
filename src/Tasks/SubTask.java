package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends SimpleTask{
    private int epicId;
    public SubTask(String title, String description, Status status, int epicId,
                   LocalDateTime startTime, Duration duration) {
        super(title, description,status, startTime, duration);
        this.epicId = epicId;
        type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }
    public void setEpicId(int epicId){
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return id + "," + type + "," + title + "," + status + "," + description +","
                + startTime + "," + duration + "," + epicId;
    }

}
