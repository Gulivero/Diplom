package slavin.fit.bstu.quest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.API.QuestAPI;
import slavin.fit.bstu.quest.Model.User;

public class LoginActivity extends AppCompatActivity {

    Button register, login;
    EditText password, name;
    ArrayList<EditText> reginfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);


        init();

        reginfoList = new ArrayList<>();
        reginfoList.add(name);
        reginfoList.add(password);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(v);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register(v);
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

    public void Register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void Login(View view) {

        for (EditText edit : reginfoList) {
            if (TextUtils.isEmpty(edit.getText())) {
                edit.setError("Заполните поле!");
                return;
            }
        }

        User user = new User();

        user.setLogin(name.getText().toString());
        user.setPassword(password.getText().toString());

        NetworkService.getInstance()
                .getQuestApi()
                .Login(user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User loginUser = response.body();
                        if (loginUser.getLogin() != null) {
                            Integer id = loginUser.getId();
                            String username = loginUser.getLogin();
                            Toast.makeText(LoginActivity.this, "Авторизация прошла успешно", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("id", id);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Неверное имя пользователя или пароль", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Toast.makeText(LoginActivity.this, "Неверное имя пользователя или пароль", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    private void init (){
        register = (Button) findViewById(R.id.registerButton);
        login = (Button) findViewById(R.id.loginButton);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.username);
    }
}
