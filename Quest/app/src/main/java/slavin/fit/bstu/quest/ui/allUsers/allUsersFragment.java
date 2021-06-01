package slavin.fit.bstu.quest.ui.allUsers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import slavin.fit.bstu.quest.API.NetworkService;
import slavin.fit.bstu.quest.Adapter.DataAdapterComplete;
import slavin.fit.bstu.quest.Adapter.DataAdapterUser;
import slavin.fit.bstu.quest.CurrentQuestActivity;
import slavin.fit.bstu.quest.HomeActivity;
import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.Model.User;
import slavin.fit.bstu.quest.R;
import slavin.fit.bstu.quest.CurrentUserActivity;

public class allUsersFragment extends Fragment {

    View root;
    SearchView searchView;
    DataAdapterUser adapter;
    List<User> listUsers = new ArrayList<>();
    List<Image> listImages = new ArrayList<>();
    RecyclerView recyclerView;
    String[] sort = { "По убыванию", "По возрастанию"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_allusers, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        Spinner spinner = (Spinner) root.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sort);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                if(item.equals("По возрастанию"))
                    asc();
                else if (item.equals("По убыванию"))
                    desc();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);

        setHasOptionsMenu(true);

        display();
        return root;
    }

    public void desc(){
        Collections.sort(listUsers, new Comparator<User>() {
            @Override
            public int compare(final User object1, final User object2) {
                return object1.getCount_Complete().compareTo(object2.getCount_Complete());
            }
        } );
        Collections.reverse(listUsers);
        initViews();
    }

    public void asc(){
        Collections.sort(listUsers, new Comparator<User>() {
            @Override
            public int compare(final User object1, final User object2) {
                return object1.getCount_Complete().compareTo(object2.getCount_Complete());
            }
        } );
        initViews();
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
        List<User> temp = new ArrayList();
        for(User user : listUsers){
            if(user.getName().toLowerCase().contains(text.toLowerCase())
                    || user.getLogin().toLowerCase().contains(text.toLowerCase())
                    || (text.matches("((-|\\+)?[0-9])+") && user.getCount_Complete() > Integer.parseInt(text))){
                temp.add(user);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    public void display() {
        NetworkService.getInstance()
                .getQuestApi()
                .GetUsers()
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                        listUsers = response.body();
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
                    public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
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

        adapter = new DataAdapterUser(root.getContext(), listUsers, listImages);
        adapter.setOnItemClickListener(new DataAdapterUser.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                User user = listUsers.get(position);
                Intent intent = new Intent(root.getContext(), CurrentUserActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("username", user.getLogin());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

    }
}
