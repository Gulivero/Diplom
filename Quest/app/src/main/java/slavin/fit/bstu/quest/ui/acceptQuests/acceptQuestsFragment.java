package slavin.fit.bstu.quest.ui.acceptQuests;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import slavin.fit.bstu.quest.Adapter.DataAdapterComplete2;
import slavin.fit.bstu.quest.CurrentQuestActivity;
import slavin.fit.bstu.quest.Adapter.DataAdapter;
import slavin.fit.bstu.quest.Adapter.DataAdapterComplete;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.R;


public class acceptQuestsFragment extends Fragment {

    AdapterView.AdapterContextMenuInfo info;
    String username;
    int userid;
    DataAdapterComplete adapter;
    DataAdapterComplete2 adapter2;
    List<Quest> listQuests = new ArrayList<>();
    List<Quest> listQuestsComplete = new ArrayList<>();
    List<Image> listImages = new ArrayList<>();
    List<Image> listImagesComplete = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        root = inflater.inflate(R.layout.fragment_acceptquests, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.card_recycler_view);
        recyclerView2 = (RecyclerView) root.findViewById(R.id.card_recycler_view2);

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
                            if(quest.getContractor().equals(username) && quest.getStatus().equals("in process"))
                            {
                                listQuests.add(quest);
                            }
                            if(quest.getContractor().equals(username) && quest.getStatus().equals("complete"))
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
                                            for (Quest quest : listQuestsComplete) {
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

    private void initViews(){
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(root.getContext());
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setNestedScrollingEnabled(false);

        adapter = new DataAdapterComplete(root.getContext(), listQuests, listImages);
        adapter2 = new DataAdapterComplete2(root.getContext(), listQuestsComplete, listImagesComplete);
        adapter.setOnItemClickListener(new DataAdapterComplete.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Quest quest = listQuests.get(position);
                Intent intent = new Intent(root.getContext(), CurrentQuestActivity.class);
                intent.putExtra("id", quest.getId());
                Log.d("valera", "" + quest.getId());
                intent.putExtra("username", username);
                intent.putExtra("userId", userid);
                startActivity(intent);
            }
        });
        adapter2.setOnItemClickListener(new DataAdapterComplete2.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Quest quest = listQuestsComplete.get(position);
                Intent intent = new Intent(root.getContext(), CurrentQuestActivity.class);
                intent.putExtra("id", quest.getId());
                Log.d("valera", "" + quest.getId());
                intent.putExtra("username", username);
                intent.putExtra("userId", userid);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);
        for (Quest quest:listQuests) {
            Log.d("valera", quest.getName() + " " + quest.getId());
        }
        for (Quest quest:listQuestsComplete) {
            Log.d("valera2", quest.getName() + " " + quest.getId());
        }

    }
}
