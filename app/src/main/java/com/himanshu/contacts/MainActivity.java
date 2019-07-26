package com.himanshu.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.himanshu.contacts.Room.MyAppDatabase;
import com.himanshu.contacts.Room.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAppDatabase myAppDatabase;
    SearchView searchView;
    List<User> users;
    List<User> searchuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myAppDatabase = Room.databaseBuilder(getApplicationContext(), MyAppDatabase.class, "userdb").allowMainThreadQueries().build();

        users = myAppDatabase.myDao().getAll();

        initViews();
        set_Recycler_View(users);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Add_Contact.class);
                startActivity(i);
            }
        });
    }

    public void set_Recycler_View(List<User> user){
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SimpleRecyclerAdapter adapter = new SimpleRecyclerAdapter(getApplicationContext(), user);
        recyclerView.setAdapter(adapter);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.contact_recycler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getApplicationContext(), "OnSubmit Call", Toast.LENGTH_LONG).show();
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    searchuser = myAppDatabase.myDao().findByName(query);
                    set_Recycler_View(searchuser);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchuser = myAppDatabase.myDao().findByName(s);
                set_Recycler_View(searchuser);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

}
