package slavin.fit.bstu.quest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.Model.Request;
import slavin.fit.bstu.quest.Model.User;

public class CurrentQuestActivity extends AppCompatActivity {
    TextView name;
    TextView description;
    TextView reward;
    TextView author;
    TextView contractor;
    Button request, check;
    String username;
    int questId, userId;
    ImageView ImageQuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentquest);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);


        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        reward = findViewById(R.id.reward);
        author = findViewById(R.id.author);
        contractor = findViewById(R.id.contractor);
        request = findViewById(R.id.takeQuestButton);
        check = findViewById(R.id.checkContractorsButton);
        ImageQuest = findViewById(R.id.imageQuest);
        request.setVisibility(View.VISIBLE);
        check.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle id = getIntent().getExtras();
        questId = id.getInt("id");
        username = id.getString("username");
        userId = id.getInt("userId");

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addrequest();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setcontractor();
            }
        });

        NetworkService.getInstance()
                .getQuestApi()
                .GetQuest(questId)
                .enqueue(new Callback<Quest>() {
                    @Override
                    public void onResponse(@NonNull Call<Quest> call, @NonNull Response<Quest> response) {
                        Quest quest = response.body();
                        name.setText(quest.getName());
                        description.setText(quest.getDescription());
                        reward.setText(quest.getReward());
                        author.setText(quest.getAuthor());
                        contractor.setText(quest.getContractor());
                        if(quest.getContractor().equals("Не выбран")) {
                            request.setVisibility(View.VISIBLE);
                            check.setVisibility(View.GONE);
                        }
                        if(quest.getAuthor().equals(username)) {
                            request.setVisibility(View.GONE);
                            check.setVisibility(View.VISIBLE);
                        }
                        if(quest.getStatus().equals("complete")) {
                            request.setVisibility(View.GONE);
                            check.setVisibility(View.GONE);
                        }
                        NetworkService.getInstance()
                                .getQuestApi()
                                .GetImages()
                                .enqueue(new Callback<List<Image>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                                        List<Image> list = response.body();
                                        for (Image image : list) {
                                            if(image.getQuestId() != null && image.getQuestId() == quest.getId())
                                            {
                                                Glide.with(getApplicationContext()).load(image.getImageRef()).into(ImageQuest);
                                            }

                                        }

                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
                                        Toast.makeText(CurrentQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                        t.printStackTrace();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Quest> call, @NonNull Throwable t) {
                        Toast.makeText(CurrentQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        NetworkService.getInstance()
                .getQuestApi()
                .GetQuest(questId)
                .enqueue(new Callback<Quest>() {
                    @Override
                    public void onResponse(@NonNull Call<Quest> call, @NonNull Response<Quest> response) {
                        Quest quest = response.body();
                        name.setText(quest.getName());
                        description.setText(quest.getDescription());
                        reward.setText(quest.getReward());
                        author.setText(quest.getAuthor());
                        contractor.setText(quest.getContractor());
                        if(quest.getContractor().equals("Не выбран")) {
                            request.setVisibility(View.VISIBLE);
                            check.setVisibility(View.GONE);
                        }
                        if(quest.getAuthor().equals(username)) {
                            request.setVisibility(View.GONE);
                            check.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Quest> call, @NonNull Throwable t) {
                        Toast.makeText(CurrentQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });

    }

    public void setcontractor(){
        Intent intent = new Intent(this, SetContractorActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        intent.putExtra("id", questId);
        startActivity(intent);
        this.finish();
    }

    public void addrequest(){
        Request newrequest = new Request();
        newrequest.setQuestId(questId);
        newrequest.setUserId(userId);
        NetworkService.getInstance()
                .getQuestApi()
                .AddRequest(newrequest)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Toast.makeText(CurrentQuestActivity.this, response.body(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(CurrentQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
}
