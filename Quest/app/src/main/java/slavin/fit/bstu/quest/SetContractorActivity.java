package slavin.fit.bstu.quest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.Adapter.DataAdapterUser;
import slavin.fit.bstu.quest.Adapter.DataAdapterUserSet;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.Model.Request;
import slavin.fit.bstu.quest.Model.User;

public class SetContractorActivity extends AppCompatActivity {

    int questId, userId;
    String user_name;
    List<User> listUsers = new ArrayList<User>();
    List<Request> listRequests = new ArrayList<Request>();
    List<Image> listImages = new ArrayList<Image>();
    RecyclerView recyclerView;
    DataAdapterUserSet adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setcontractor);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle id = getIntent().getExtras();
        questId = id.getInt("id");
        userId = id.getInt("userId");
        user_name = id.getString("username");

        display();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void display(){
        listImages.clear();
        listRequests.clear();
        listUsers.clear();
        NetworkService.getInstance()
                .getQuestApi()
                .GetRequests()
                .enqueue(new Callback<List<Request>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Request>> call, @NonNull Response<List<Request>> response) {
                        List<Request> requests = response.body();
                        for (Request request : requests)
                        {
                            if(request.getQuestId() == questId)
                                listRequests.add(request);
                        }
                        NetworkService.getInstance()
                                .getQuestApi()
                                .GetUsers()
                                .enqueue(new Callback<List<User>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                                        List<User> users = response.body();
                                        for (User user : users)
                                        {
                                            for(Request request : listRequests)
                                            {
                                                if(user.getId() == request.getUserId())
                                                    listUsers.add(user);
                                            }
                                        }
                                        NetworkService.getInstance()
                                                .getQuestApi()
                                                .GetImages()
                                                .enqueue(new Callback<List<Image>>() {
                                                    @Override
                                                    public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                                                        List<Image> list = response.body();
                                                        for (User user : listUsers) {
                                                            for (Image image : list) {
                                                                if(image.getUserId() != null && user.getId() == image.getUserId())
                                                                    listImages.add(image);
                                                            }
                                                        }
                                                        initViews();
                                                    }

                                                    @Override
                                                    public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
                                                        Toast.makeText(SetContractorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                                        t.printStackTrace();
                                                    }
                                                });
                                    }
                                    @Override
                                    public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                                        Toast.makeText(SetContractorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                        t.printStackTrace();
                                    }
                                });
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<Request>> call, @NonNull Throwable t) {
                        Toast.makeText(SetContractorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    public void EditQuest(int questid, String username)
    {
        Quest quest = new Quest();
        quest.setContractor(username);

        NetworkService.getInstance()
                .getQuestApi()
                .EditQuest(questid, quest)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Toast.makeText(SetContractorActivity.this, response.body(), Toast.LENGTH_LONG).show();
                        SetContractorActivity.this.finish();
                        Intent intent = new Intent(SetContractorActivity.this, HomeActivity.class);
                        intent.putExtra("id", userId);
                        intent.putExtra("username", user_name);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(SetContractorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        registerForContextMenu(recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new DataAdapterUserSet(this, listUsers, listImages, new DataAdapterUserSet.ImageClickListener() {
            @Override
            public void imageNoViewOnClick(View v, int position) {
                NetworkService.getInstance()
                        .getQuestApi()
                        .GetRequests()
                        .enqueue(new Callback<List<Request>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<Request>> call, @NonNull Response<List<Request>> response) {
                                List<Request> list = response.body();
                                for (Request request : list) {
                                    if(request.getUserId() == listUsers.get(position).getId()){
                                            NetworkService.getInstance()
                                                    .getQuestApi()
                                                    .DeleteRequest(request.getId())
                                                    .enqueue(new Callback<String>() {
                                                        @Override
                                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                            display();
                                                        }

                                                        @Override
                                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                            Toast.makeText(SetContractorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                                            t.printStackTrace();
                                                        }
                                                    });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<Request>> call, @NonNull Throwable t) {
                                Toast.makeText(SetContractorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                t.printStackTrace();
                            }
                        });
            }

            @Override
            public void imageYesViewOnClick(View v, int position) {
                User user = listUsers.get(position);
                EditQuest(questId, user.getLogin());
            }
        });
        adapter.setOnItemClickListener(new DataAdapterUserSet.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                User user = listUsers.get(position);
                Intent intent = new Intent(SetContractorActivity.this, CurrentUserActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("username", user.getLogin());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

    }
}
