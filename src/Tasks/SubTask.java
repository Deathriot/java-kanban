package Tasks;

public class SubTask extends SimpleTask{
    private int epicId;
    public SubTask(String title, String description, String status, EpicTask epicTask) {
        super(title, description,status);
        this.epicId = epicTask.getId();
    }

    public int getEpicId() {
        return epicId;
    }
    public void setEpicId(int epicId){
        this.epicId = epicId;
    }

}
