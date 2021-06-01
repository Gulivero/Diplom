package slavin.fit.bstu.quest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.User;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    EditText username, password, name, communication;
    ArrayList<EditText> reginfoList;
    ImageView imageView;
    private final int PICK_IMAGE_REQUEST = 71;
    int userid;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageuri;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        init();

        reginfoList = new ArrayList<>();
        reginfoList.add(username);
        reginfoList.add(password);
        reginfoList.add(name);
        reginfoList.add(communication);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
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

    public void Register()
    {
        for (EditText edit : reginfoList) {
            if (TextUtils.isEmpty(edit.getText())) {
                edit.setError("Заполните поле!");
                return;
            }
            if (username.getText().toString().length() < 6) {
                username.setError("Длина логина должна быть не менее 6 символов!");
                return;
            }
            if (password.getText().toString().length() < 6) {
                password.setError("Длина пароля должна быть не менее 6 символов!");
                return;
            }
        }

        final User user = new User();

        user.setLogin(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.setName(name.getText().toString());
        user.setCommunication(communication.getText().toString());
        user.setCount_Quests(null);

        NetworkService.getInstance()
                .getQuestApi()
                .AddUser(user)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Toast.makeText(RegisterActivity.this, response.body(), Toast.LENGTH_LONG).show();
                        if (response.body().equals("Регистрация прошла успешно")) {
                            NetworkService.getInstance()
                                    .getQuestApi()
                                    .GetUsers()
                                    .enqueue(new Callback<List<User>>() {
                                        @Override
                                        public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                                            List<User> list = response.body();
                                            for (User user1 : list)
                                            {
                                                if (user1.getLogin().equals(user.getLogin()))
                                                    userid = user1.getId();
                                            }
                                            uploadImage();

                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                                            Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                            t.printStackTrace();
                                        }
                                    });
                            back();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });


    }

    public void back(){
        this.finish();
    }

    private void uploadImage() {

        if(filePath != null)
        {
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uri = ref.getDownloadUrl();
                            while ((!uri.isComplete()));
                            imageuri = uri.getResult().toString();
                            Image image = new Image();

                            image.setFileName("profile");
                            image.setUserId(userid);
                            image.setImageRef(imageuri);


                            NetworkService.getInstance()
                                    .getQuestApi()
                                    .AddImage(image)
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            Toast.makeText(RegisterActivity.this, response.body(), Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                            Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                            t.printStackTrace();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Ошибка "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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

    private void init (){
        register = (Button) findViewById(R.id.registerButton);
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        communication = (EditText) findViewById(R.id.communication);
        name = (EditText) findViewById(R.id.name);
        imageView = (ImageView) findViewById(R.id.imageView);
    }
}
