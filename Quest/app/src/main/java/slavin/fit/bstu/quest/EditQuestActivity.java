package slavin.fit.bstu.quest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import slavin.fit.bstu.quest.ui.myQuests.myQuestsFragment;

public class EditQuestActivity extends AppCompatActivity {

    Button edit;
    ImageView ImageQuestEdit;
    EditText name, description, reward;
    Integer id;
    int questid;
    ArrayList<EditText> reginfoList;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageuri;
    FirebaseAuth mAuth;

    public boolean flag = false;

    String username;
    int imageId;
    int userid;
    boolean imagehave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editquest);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        flag = false;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        ImageQuestEdit = findViewById(R.id.imageQuestEdit);
        edit = findViewById(R.id.editQuest);
        name = findViewById(R.id.nameEdit);
        description = findViewById(R.id.descriptionEdit);
        reward = findViewById(R.id.rewardEdit);

        reginfoList = new ArrayList<>();
        reginfoList.add(name);
        reginfoList.add(description);
        reginfoList.add(reward);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("questid");
        userid = bundle.getInt("id");
        username = bundle.getString("username");

        NetworkService.getInstance()
                .getQuestApi()
                .GetQuest(id)
                .enqueue(new Callback<Quest>() {
                    @Override
                    public void onResponse(@NonNull Call<Quest> call, @NonNull Response<Quest> response) {
                        Quest quest = response.body();
                        String name1 = quest.getName();
                        String description1 = quest.getDescription();
                        String reward1 = quest.getReward();
                        questid = quest.getId();
                        name.setText(name1);
                        description.setText(description1);
                        reward.setText(reward1);

                        NetworkService.getInstance()
                                .getQuestApi()
                                .GetImages()
                                .enqueue(new Callback<List<Image>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                                        List<Image> list = response.body();
                                        for (Image image : list) {
                                            if(image.getQuestId() != null && image.getQuestId() == questid)
                                            {
                                                imagehave = true;
                                                imageId = image.getId();
                                                Glide.with(getApplicationContext()).load(image.getImageRef()).into(ImageQuestEdit);
                                            }

                                        }

                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
                                        Toast.makeText(EditQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                        t.printStackTrace();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Quest> call, @NonNull Throwable t) {
                        Toast.makeText(EditQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });

        ImageQuestEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
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

    public void edit() {

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

        NetworkService.getInstance()
                .getQuestApi()
                .EditQuest(id, quest)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Toast.makeText(EditQuestActivity.this, response.body(), Toast.LENGTH_LONG).show();
                        if (response.body().equals("Квест успешно изменён")) {
                            uploadImage();
                            try {
                                Thread.sleep(2000); //Приостанавливает поток на 1 секунду
                            } catch (Exception e) {

                            }
                            back();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(EditQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
                            if(imagehave == true) {
                                Image image = new Image();
                                image.setImageRef(imageuri);


                                NetworkService.getInstance()
                                        .getQuestApi()
                                        .EditImage(imageId, image)
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                Toast.makeText(EditQuestActivity.this, response.body(), Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                Toast.makeText(EditQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                                t.printStackTrace();
                                            }
                                        });
                            }
                            else {
                                Image image = new Image();
                                image.setImageRef(imageuri);
                                image.setQuestId(questid);
                                image.setFileName("quest");


                                NetworkService.getInstance()
                                        .getQuestApi()
                                        .AddImage(image)
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                Toast.makeText(EditQuestActivity.this, response.body(), Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                Toast.makeText(EditQuestActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                                t.printStackTrace();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditQuestActivity.this, "Ошибка "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                ImageQuestEdit.setImageBitmap(bitmap);
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
