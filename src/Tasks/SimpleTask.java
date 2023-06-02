package Tasks;

public class SimpleTask{

    protected TaskType type;
    protected String title;
    protected String description;
    protected Status status;
    protected int id;
    public SimpleTask(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        type = TaskType.SIMPLETASK; // Пойдет?
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

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + "," + type + "," + title + "," + status + "," + description +",";
    }
}
