package slavin.fit.bstu.quest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.User;

public class CurrentUserActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView3, textView2;
    private EditText editText;
    private EditText editText1;
    private EditText editText2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentuser);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);
        textView3 = findViewById(R.id.text_profilecountquests);
        textView2 = findViewById(R.id.text_profilecountcompletequests);
        editText = findViewById(R.id.edittext_profilelogin);
        editText1 = findViewById(R.id.edittext_profilename);
        editText2 = findViewById(R.id.edittext_profilecommunication);

        editText.setEnabled(false);
        editText1.setEnabled(false);
        editText2.setEnabled(false);
        editText.setTextColor(Color.BLACK);
        editText1.setTextColor(Color.BLACK);
        editText2.setTextColor(Color.BLACK);


        Bundle id = getIntent().getExtras();
        int userId = id.getInt("id");



        NetworkService.getInstance()
                .getQuestApi()
                .GetUser(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User user = response.body();
                        editText.setText(user.getLogin());
                        editText1.setText(user.getName());
                        editText2.setText(user.getCommunication());
                        textView3.append(user.getCount_Quests().toString());
                        textView2.append(user.getCount_Complete().toString());
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Toast.makeText(CurrentUserActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
        NetworkService.getInstance()
                .getQuestApi()
                .GetImages()
                .enqueue(new Callback<List<Image>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                        List<Image> list = response.body();
                        for (Image image : list) {
                            if(image.getUserId() != null && image.getUserId() == userId)
                            {
                                Glide.with(getApplicationContext()).load(image.getImageRef()).into(imageView);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
                        Toast.makeText(CurrentUserActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
