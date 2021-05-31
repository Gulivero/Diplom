package slavin.fit.bstu.quest.Model;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("id")
    private Integer id;
    @SerializedName("user_id")
    private Integer user_id;
    @SerializedName("quest_id")
    private Integer quest_id;
    @SerializedName("file_name")
    private String file_name;
    @SerializedName("image_ref")
    private String image_ref;

    public Image() {

    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public Integer getQuestId() {
        return quest_id;
    }

    public void setQuestId(Integer quest_id) {
        this.quest_id = quest_id;
    }

    public String getFileName() { return file_name; }

    public void setFileName(String file_name) { this.file_name = file_name; }

    public String getImageRef() { return image_ref; }

    public void setImageRef(String image_ref) { this.image_ref = image_ref; }


}
