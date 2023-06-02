package Tasks;

public class SubTask extends SimpleTask{
    private int epicId;
    public SubTask(String title, String description, Status status, EpicTask epicTask) {
        super(title, description,status);
        this.epicId = epicTask.getId();
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
        return id + "," + type + "," + title + "," + status + "," + description +"," + epicId;
    }

}
