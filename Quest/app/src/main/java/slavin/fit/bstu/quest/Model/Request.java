package slavin.fit.bstu.quest.Model;

import com.google.gson.annotations.SerializedName;

public class Request {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("quest_id")
    private int quest_id;

    public Request() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() { return user_id; }

    public void setUserId(int user_id) { this.user_id = user_id; }

    public int getQuestId() { return quest_id; }

    public void setQuestId(int quest_id) { this.quest_id = quest_id; }

}
