public abstract class Task {
    private String title;
    private String description;
    private int id = 0;
    protected String status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
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

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getStatus(){
        return status;
    }

    @Override
    public String toString() {
        return
                "Название - " + title + "\n" +
                "Описание - " + description + "\n" +
                "Идентификатор - " + id + "\n" +
                "Статус - " + status;
    }
}
