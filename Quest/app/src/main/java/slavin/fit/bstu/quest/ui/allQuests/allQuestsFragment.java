package slavin.fit.bstu.quest.ui.allQuests;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import slavin.fit.bstu.quest.Adapter.DataAdapterComplete;
import slavin.fit.bstu.quest.CurrentQuestActivity;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.R;

public class allQuestsFragment extends Fragment {

    View root;
    DataAdapterComplete adapter;
    SearchView searchView;
    String username;
    int userId;
    List<Quest> listQuests = new ArrayList<>();
    List<Image> listImages;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_allquests, container, false);

        Bundle id = getActivity().getIntent().getExtras();
        username = id.getString("username");
        userId = id.getInt("id");

        setHasOptionsMenu(true);
        display();

        return root;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        // Здесь можно указать будет ли строка поиска изначально развернута или свернута в значок
        searchView.setIconifiedByDefault(true);

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String cs) {
                filter(cs);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        super.onCreateOptionsMenu(menu, inflater);

    }

    void filter(String text){
        List<Quest> temp = new ArrayList();
        for(Quest quest: listQuests){
            if(quest.getName().toLowerCase().contains(text.toLowerCase()) || quest.getDescription().toLowerCase().contains(text.toLowerCase())){
                temp.add(quest);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }


    public void display() {
        NetworkService.getInstance()
                .getQuestApi()
                .GetQuests()
                .enqueue(new Callback<List<Quest>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Quest>> call, @NonNull Response<List<Quest>> response) {
                        for (Quest quest : response.body()) {
                            if(quest.getStatus().equals("in process"))
                                listQuests.add(quest);
                        }
                        NetworkService.getInstance()
                                .getQuestApi()
                                .GetImages()
                                .enqueue(new Callback<List<Image>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                                        listImages = response.body();
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
        recyclerView = (RecyclerView) root.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new DataAdapterComplete(root.getContext(), listQuests, listImages);
        adapter.setOnItemClickListener(new DataAdapterComplete.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Quest quest = listQuests.get(position);
                Intent intent = new Intent(root.getContext(), CurrentQuestActivity.class);
                intent.putExtra("id", quest.getId());
                intent.putExtra("username", username);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

    }
}
