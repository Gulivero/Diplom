package slavin.fit.bstu.quest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.Model.User;

public class AddQuestActivity extends AppCompatActivity {

    Button add;
    ImageView imageQuest;
    EditText name, description, reward;
    String username;
    int userid, questid;
    ArrayList<EditText> reginfoList;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageuri;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addquest);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        add = findViewById(R.id.addQuest);
        imageQuest = findViewById(R.id.imageQuest);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        reward = findViewById(R.id.reward);

        reginfoList = new ArrayList<>();
        reginfoList.add(name);
        reginfoList.add(description);
        reginfoList.add(reward);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        userid = bundle.getInt("userid");

        imageQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
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

    public void add() {
        for (EditText edit : reginfoList) {
            if (TextUtils.isEmpty(edit.getText())) {
                edit.setError("Заполните поле!");
                return;
            }
        }

        Quest quest = new Quest();
        quest.setName(name.getText().toString());
        quest.setDescription(description.getText().toString());
        quest.setReward(reward.getText().toString());
        quest.setContractor("Не выбран");
        quest.setAuthor(username);
        quest.setAuthorId(userid);
        quest.setStatus("in process");

        NetworkService.getInstance()
                .getQuestApi()
                .AddQuest(quest)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Toast.makeText(AddQuestActivity.this, response.body(), Toast.LENGTH_LONG).show();
                        if (response.body().equals("Квест успешно добавлен")) {
                            NetworkService.getInstance()
                                    .getQuestApi()
                                    .GetQuests()
                                    .enqueue(new Callback<List<Quest>>() {
                                        @Override
                                        public void onResponse(@NonNull Call<List<Quest>> call, @NonNull Response<List<Quest>> response) {
                                            List<Quest> list = response.body();
                                            for (Quest quest1 : list)
                                            {
                                                if (quest1.getAuthor().equals(quest.getAuthor()) &&
                                                        quest1.getName().equals(quest.getName()) &&
                                                        quest1.getDescription().equals(quest.getDescription()))
                                                    questid = quest1.getId();
                                            }
                                            uploadImage();
                                            try {
                                                Thread.sleep(3000); //Приостанавливает поток на 1 секунду
                                            } catch (Exception e) {

                                            }

                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<List<Quest>> call, @NonNull Throwable t) {
                                            Toast.makeText(AddQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                            t.printStackTrace();
                                        }
                                    });
                            back();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(AddQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    public void back(){
        this.finish();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("id", userid);
        intent.putExtra("username", username);
        startActivity(intent);
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

                            image.setFileName("quest");
                            image.setQuestId(questid);
                            image.setImageRef(imageuri);


                            NetworkService.getInstance()
                                    .getQuestApi()
                                    .AddImage(image)
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            Toast.makeText(AddQuestActivity.this, response.body(), Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                            Toast.makeText(AddQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                            t.printStackTrace();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddQuestActivity.this, "Ошибка "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                imageQuest.setImageBitmap(bitmap);
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
                back();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
