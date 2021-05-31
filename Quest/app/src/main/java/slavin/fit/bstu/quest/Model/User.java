package slavin.fit.bstu.quest.Model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int Id;
    @SerializedName("login")
    private String Login;
    @SerializedName("password")
    private String Password;
    @SerializedName("name")
    private String Name;
    @SerializedName("communication")
    private String Communication;
    @SerializedName("role")
    private String Role;
    @SerializedName("count_Quests")
    private Integer Count_Quests;
    @SerializedName("count_Complete")
    private Integer Count_Complete;
    @SerializedName("salt")
    private String Salt;

    public User() {

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        this.Login = login;
    }

    public String getPassword() {return Password; }

    public void setPassword(String password) { this.Password = password; }

    public String getName() { return Name; }

    public void setName(String name) { this.Name = name; }

    public String getCommunication() { return Communication; }

    public void setCommunication(String communication) { this.Communication = communication; }

    public String getRole() { return Role; }

    public void setRole(String role) { this.Role = role; }

    public Integer getCount_Quests() { return Count_Quests; }

    public void setCount_Quests(Integer count_Quests) { this.Count_Quests = count_Quests; }

    public Integer getCount_Complete() { return Count_Complete; }

    public void setCount_Complete(Integer count_Complete) { this.Count_Complete = count_Complete; }

    public String getSalt() {
        return Salt;
    }

    public void setSalt(String salt) {
        this.Salt = salt;
    }

}
