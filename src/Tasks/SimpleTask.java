package Tasks;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Objects;

public class SimpleTask{

    protected TaskType type;
    protected String title;
    protected String description;
    protected Status status;
    protected int id;
    protected LocalDateTime startTime;
    protected Duration duration;
    public SimpleTask(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        type = TaskType.SIMPLETASK;
    }

    public LocalDateTime getEndTime(){
        if(startTime == null){
            return null;
        }
        return startTime.plus(duration);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType(){
        return type;
    }

    @Override
    public String toString() {
        return id + "," + type + "," + title + "," + status + "," + description +","
                + startTime + "," + duration + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTask that = (SimpleTask) o;

        if (id != that.id) return false;
        if (type != that.type) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(description, that.description)) return false;
        if (status != that.status) return false;
        if (!Objects.equals(startTime, that.startTime)) return false;
        return Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        return result;
    }
}
