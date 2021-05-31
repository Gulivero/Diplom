package slavin.fit.bstu.quest.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParsePosition;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.HomeActivity;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.User;
import slavin.fit.bstu.quest.R;
import slavin.fit.bstu.quest.RegisterActivity;

public class ProfileFragment extends Fragment {
    
    private int userId;
    private TextView textView3, textView2;
    private EditText editText;
    private EditText editText1;
    private EditText editText2;
    private Button saveButton;
    private ImageView imageProfile;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageuri;
    FirebaseAuth mAuth;
    int imageId;
    boolean imagehave = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        textView3 = root.findViewById(R.id.text_profilecountquests);
        textView2 = root.findViewById(R.id.text_profilecountcompletequests);
        editText = root.findViewById(R.id.edittext_profilelogin);
        editText1 = root.findViewById(R.id.edittext_profilename);
        editText2 = root.findViewById(R.id.edittext_profilecommunication);
        imageProfile = root.findViewById(R.id.imageProfile);

        saveButton = root.findViewById(R.id.changeButton);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Bundle bundle = getActivity().getIntent().getExtras();
        userId = bundle.getInt("id");

        NetworkService.getInstance()
                .getQuestApi()
                .GetUser(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User loginUser = response.body();
                        String login = loginUser.getLogin();
                        String name = loginUser.getName();
                        String communication = loginUser.getCommunication();
                        Integer count = loginUser.getCount_Quests();
                        editText.setText(login);
                        editText1.setText(name);
                        editText2.setText(communication);
                        textView3.append(count.toString());
                        textView2.append(loginUser.getCount_Complete().toString());
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
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
                                imagehave = true;
                                imageId = image.getId();
                                Glide.with(getActivity().getApplicationContext()).load(image.getImageRef()).into(imageProfile);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });


        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save(userId);
            }
        });

        return root;
    }

    public void changeImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageProfile.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void Save(int id){
        User user = new User();
        user.setLogin(editText.getText().toString());
        user.setName(editText1.getText().toString());
        user.setCommunication(editText2.getText().toString());
        NetworkService.getInstance()
                .getQuestApi()
                .EditUser(id, user)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Toast.makeText(getActivity(), "Изменение прошло успешно",Toast.LENGTH_LONG).show();
                        uploadImage();
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
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
                                                Toast.makeText(getActivity(), response.body(), Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                                t.printStackTrace();
                                            }
                                        });
                            }
                            else {
                                Image image = new Image();
                                image.setImageRef(imageuri);
                                image.setUserId(userId);
                                image.setFileName("profile");


                                NetworkService.getInstance()
                                        .getQuestApi()
                                        .AddImage(image)
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                Toast.makeText(getActivity(), response.body(), Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                                t.printStackTrace();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Ошибка "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
