package slavin.fit.bstu.quest.ui.myQuests;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.AddQuestActivity;
import slavin.fit.bstu.quest.CurrentQuestActivity;
import slavin.fit.bstu.quest.Adapter.DataAdapter;
import slavin.fit.bstu.quest.Adapter.DataAdapterComplete;
import slavin.fit.bstu.quest.EditQuestActivity;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.Model.User;
import slavin.fit.bstu.quest.R;



public class myQuestsFragment extends Fragment {

    AdapterView.AdapterContextMenuInfo info;
    String username;
    int userid;
    DataAdapter adapter;
    DataAdapterComplete adapter2;
    List<Quest> listQuests = new ArrayList<>();
    List<Quest> listQuestsComplete = new ArrayList<>();
    List<Image> listImages = new ArrayList<>();
    List<Image> listImagesComplete = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_myquests, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        recyclerView = (RecyclerView) root.findViewById(R.id.card_recycler_view);
        recyclerView2 = (RecyclerView) root.findViewById(R.id.card_recycler_view2);
        registerForContextMenu(recyclerView);

        Bundle bundle = getActivity().getIntent().getExtras();
        username = bundle.getString("username");
        userid = bundle.getInt("id");

        display();
        setHasOptionsMenu(true);
        return root;
    }

    public void display() {
        NetworkService.getInstance()
                .getQuestApi()
                .GetQuests()
                .enqueue(new Callback<List<Quest>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Quest>> call, @NonNull Response<List<Quest>> response) {
                        List<Quest> list = response.body();
                        listQuests.clear();
                        listQuestsComplete.clear();
                        for (Quest quest : list)
                        {
                            if(quest.getAuthor().equals(username) && quest.getStatus().equals("in process"))
                            {
                                listQuests.add(quest);
                            }
                            if(quest.getAuthor().equals(username) && quest.getStatus().equals("complete"))
                            {
                                listQuestsComplete.add(quest);
                            }
                        }
                        NetworkService.getInstance()
                                .getQuestApi()
                                .GetImages()
                                .enqueue(new Callback<List<Image>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                                        List<Image> list = response.body();
                                        listImages.clear();
                                        listImagesComplete.clear();
                                        for (Image image : list)
                                        {
                                            for (Quest quest : listQuests) {
                                                if (image.getQuestId() != null && image.getQuestId() == quest.getId() && quest.getStatus().equals("in process")) {
                                                    listImages.add(image);
                                                }
                                            }
                                            for(Quest quest : listQuestsComplete){
                                                if(image.getQuestId() != null && image.getQuestId() == quest.getId() && quest.getStatus().equals("complete"))
                                                {
                                                    listImagesComplete.add(image);
                                                }
                                            }
                                        }
                                        initViews();
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
                                        Toast.makeText(root.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                        t.printStackTrace();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Quest>> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
      info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.complete:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(getContext());
                builder3.setTitle("Внимание").setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int a = item.getGroupId();
                        Quest quest = listQuests.get(a);
                        String contractor = quest.getContractor();
                        if (!quest.getContractor().equals("Не выбран")) {
                            quest.setStatus("complete");
                            Integer needingId = quest.getId();
                            NetworkService.getInstance()
                                    .getQuestApi()
                                    .EditQuest(needingId, quest)
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            Toast.makeText(getActivity(), "Квест завершён", Toast.LENGTH_LONG).show();
                                            NetworkService.getInstance()
                                                    .getQuestApi()
                                                    .GetUsers()
                                                    .enqueue(new Callback<List<User>>() {
                                                        @Override
                                                        public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                                                            for (User user : response.body()) {
                                                                if(user.getLogin().equals(contractor)){
                                                                    user.setCount_Complete(user.getCount_Complete() + 1);
                                                                    NetworkService.getInstance()
                                                                            .getQuestApi()
                                                                            .EditUser(user.getId(), user)
                                                                            .enqueue(new Callback<String>() {
                                                                                @Override
                                                                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                                                    display();
                                                                                }

                                                                                @Override
                                                                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                                                                    t.printStackTrace();
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                                                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                                            t.printStackTrace();
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                            t.printStackTrace();
                                        }
                                    });
                        }
                        else
                            Toast.makeText(getActivity(),
                                    "Невозможно завершить квест, т.к. у квеста нет исполнителя",
                                    Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setMessage("Завершить квест?");
                AlertDialog dialog3 = builder3.create();
                dialog3.show();
                return true;
            case R.id.edit:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle("Внимание").setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int a = item.getGroupId();
                        Quest quest = listQuests.get(a);
                        Integer needingId = quest.getId();
                        EditQuest(needingId);

                    }
                }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setMessage("Изменить квест?");
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;

            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Внимание").setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int a = item.getGroupId();
                        Quest quest = listQuests.get(a);
                        Integer needingId = quest.getId();
                        DeleteQuest(needingId);
                    }
                }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setMessage("Удалить квест?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void DeleteQuest(Integer id){
        NetworkService.getInstance()
                .getQuestApi()
                .DeleteQuest(id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Toast.makeText(getActivity(), response.body(), Toast.LENGTH_LONG).show();
                        display();
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    public void EditQuest(Integer id){
        Intent intent = new Intent(getContext(), EditQuestActivity.class);
        intent.putExtra("questid", id);
        intent.putExtra("id", userid);
        intent.putExtra("username", username);
        startActivity(intent);
        getActivity().finish();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.myquests_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_add :
                getActivity().finish();
                Intent intent = new Intent(getContext(), AddQuestActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userid", userid);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(root.getContext());
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setNestedScrollingEnabled(false);

        adapter = new DataAdapter(root.getContext(), listQuests, listImages);
        adapter2 = new DataAdapterComplete(root.getContext(), listQuestsComplete, listImagesComplete);
        adapter.setOnItemClickListener(new DataAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Quest quest = listQuests.get(position);
                Intent intent = new Intent(root.getContext(), CurrentQuestActivity.class);
                intent.putExtra("id", quest.getId());
                intent.putExtra("username", username);
                intent.putExtra("userId", userid);
                startActivity(intent);
            }
        });
        adapter2.setOnItemClickListener(new DataAdapterComplete.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Quest quest = listQuestsComplete.get(position);
                Intent intent = new Intent(root.getContext(), CurrentQuestActivity.class);
                intent.putExtra("id", quest.getId());
                intent.putExtra("username", username);
                intent.putExtra("userId", userid);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);

    }
}
