package slavin.fit.bstu.quest.API;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

import retrofit2.http.Path;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Request;
import slavin.fit.bstu.quest.Model.User;
import slavin.fit.bstu.quest.Model.Quest;

public interface QuestAPI {
        //http://localhost:32024/api/

        //QUESTS
        @GET("/api/quests/{id}")
        @Headers("Content-Type: application/json")
        public Call<Quest> GetQuest(@Path("id") int id);

        @GET("/api/quests")
        @Headers("Content-Type: application/json")
        public Call<List<Quest>> GetQuests();

        @DELETE("/api/quests/{id}")
        @Headers("Content-Type: application/json")
        public Call<String> DeleteQuest(@Path("id") int id);

        @POST("/api/quests")
        @Headers("Content-Type: application/json")
        public Call<String> AddQuest(@Body Quest quest);

        @PATCH("/api/quests/{id}")
        @Headers("Content-Type: application/json")
        public Call<String> EditQuest(@Path("id") int id, @Body Quest quest);

        //USERS
        @GET("/api/users/{id}")
        @Headers("Content-Type: application/json")
        public Call<User> GetUser(@Path("id") int id);

        @GET("/api/users")
        @Headers("Content-Type: application/json")
        public Call<List<User>> GetUsers();

        @DELETE("/api/users/{id}")
        @Headers("Content-Type: application/json")
        public void DeleteUser(@Path("id") int id);

        @POST("/api/users")
        @Headers("Content-Type: application/json")
        public Call<String> AddUser(@Body User user);

        @PATCH("/api/users/{id}")
        @Headers("Content-Type: application/json")
        public Call<String> EditUser(@Path("id") int id, @Body User user);

        //LOGIN
        @POST("/api/login")
        @Headers("Content-Type: application/json")
        public Call<User> Login(@Body User user);

        //REQUEST
        @GET("/api/request/{id}")
        @Headers("Content-Type: application/json")
        public Call<Request> GetRequest(@Path("id") int id);

        @GET("/api/request")
        @Headers("Content-Type: application/json")
        public Call<List<Request>> GetRequests();

        @DELETE("/api/request/{id}")
        @Headers("Content-Type: application/json")
        public Call<String> DeleteRequest(@Path("id") int id);

        @POST("/api/request")
        @Headers("Content-Type: application/json")
        public Call<String> AddRequest(@Body Request request);

        @PATCH("/api/request/{id}")
        @Headers("Content-Type: application/json")
        public Call<String> EditRequest(@Path("id") int id, @Body Request request);

        //IMAGE
        @GET("/api/images/{id}")
        @Headers("Content-Type: application/json")
        public Call<Image> GetImage(@Path("id") int id);

        @GET("/api/images")
        @Headers("Content-Type: application/json")
        public Call<List<Image>> GetImages();

        @DELETE("/api/images/{id}")
        @Headers("Content-Type: application/json")
        public Call<String> DeleteImage(@Path("id") int id);

        @POST("/api/images")
        //@Headers("Content-Type: application/json")
        public Call<String> AddImage(@Body Image image);

        @PATCH("/api/images/{id}")
        @Headers("Content-Type: application/json")
        public Call<String> EditImage(@Path("id") int id, @Body Image image);

}
