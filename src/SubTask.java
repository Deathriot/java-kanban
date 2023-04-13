public class SubTask extends Task{
    private int epicId;
    public SubTask(String title, String description, String status, EpicTask epicTask) {
        super(title, description);
        this.status = status;
        this.epicId = epicTask.getId();
    }

    public int getEpicId() {
        return epicId;
    }
    public void setStatus(String status){
        this.status = status;
    }
}
