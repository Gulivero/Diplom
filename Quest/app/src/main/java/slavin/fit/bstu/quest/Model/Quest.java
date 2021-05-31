package slavin.fit.bstu.quest.Model;

import com.google.gson.annotations.SerializedName;

public class Quest {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("reward")
    private String reward;
    @SerializedName("author")
    private String author;
    @SerializedName("contractor")
    private String contractor;
    @SerializedName("author_id")
    private int author_id;
    @SerializedName("status")
    private String status;

    public Quest() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getReward() { return reward; }

    public void setReward(String reward) { this.reward = reward; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getContractor() { return contractor; }

    public void setContractor(String contractor) { this.contractor = contractor; }

    public int getAuthorId() {
        return author_id;
    }

    public void setAuthorId(int author_id) {
        this.author_id = author_id;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

}
