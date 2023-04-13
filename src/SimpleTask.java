public class SimpleTask extends Task{
    public SimpleTask(String title, String description, String status) {
        super(title, description);
        this.status = status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
