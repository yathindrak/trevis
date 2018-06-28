package com.trevis.trevis.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trevis.trevis.R;
import com.trevis.trevis.modal.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private CommunityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<User> friendslist;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar = (Toolbar) findViewById(R.id.people_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("People");
        getAllPeople();

        mRecyclerView = (RecyclerView) findViewById(R.id.friends_recycler_view);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        //Toast.makeText(getApplicationContext(),"Clicked "+position,Toast.LENGTH_SHORT).show();

                        //Get the user which clicked on
                        User tappedUser = friendslist.get(position);

                        Intent profileIntent = new Intent(CommunityActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("tappedUserUID", tappedUser.getUserId());
                        profileIntent.putExtra("tappedUserName", tappedUser.getName());
                        profileIntent.putExtra("tappedUserStatus", tappedUser.getStatus());
                        profileIntent.putExtra("tappedUserDevToken", tappedUser.getDeviceToken());
                        profileIntent.putExtra("tappedUserImage", tappedUser.getThumbimage());
                        startActivity(profileIntent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CommunityAdapter(friendslist, this);
        //mFriendsAdapter = new CommunityAdapter(friendslist, this);
        mRecyclerView.setAdapter(mAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.people_search, menu);

        //Get the search item
        MenuItem menuItem = menu.findItem(R.id.people_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    String text = null;
    @Override
    public boolean onQueryTextChange(String newText) {
        //This method will be triggered when search data changed

        text = newText.toLowerCase();
        ArrayList<User> newList = new ArrayList<>();

        if(friendslist != null){
            for (User user: friendslist){
                String name = user.getName().toLowerCase();

                if (name.contains(newText)){
                    System.out.println(user.getName());
                    newList.add(user);
                }
            }
        }

        mAdapter.setFilter(newList);
        return false;
    }

    public synchronized void getAllPeople(){
        //String GET_All_USERS_URL ="http://192.168.8.100:9000/getAll";

        // Create a new volley request queue
        queue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                getString(R.string.GET_All_USERS_URL),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        //mTextView.setText(response.toString());
                        //System.out.println(response.toString());
                        // Process the JSON
                        try{
                            // Loop through the array elements
                            for(int i=0;i<response.length();i++){
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<User>>(){}.getType();
                                friendslist = gson.fromJson(response.toString(), type);
                                System.out.println(friendslist.get(0));
                                // specify an adapter (see also next example)
                                mAdapter = new CommunityAdapter(friendslist, getApplicationContext());
                                //mFriendsAdapter = new CommunityAdapter(friendslist, this);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        System.out.println("An error occuered");
                        System.out.println(error);
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }
}
