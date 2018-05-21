//package com.trevis.trevis.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.TextView;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.squareup.picasso.Picasso;
//import com.trevis.trevis.R;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class UsersActivity extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener {
//
//    private Toolbar mToolbar;
//    private RecyclerView mUsersList;
//    private DatabaseReference mUsersDatabase;
//    //There should be a layout to dispaly users details
//    //So I created a new one in layouts folder
//    private LinearLayoutManager mLayoutManager;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_users);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        //Added code
//        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("All Users");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        //Database reference
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
//
//        //Set up the layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//
//        //Set up the recycler view properties
//        mUsersList = (RecyclerView) findViewById(R.id.users_list);
//        mUsersList.setHasFixedSize(true);
//        mUsersList.setLayoutManager(mLayoutManager);
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.users, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//
//
//    //Added  code
//    //Since we need to get data realtime we add that with onStart()
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
//                //Class
//                Users.class,
//                //Layout
//                R.layout.users_single_layout,
//                //View Holder
//                UsersViewHolder.class,
//                //Database
//                mUsersDatabase
//
//        ) {
//            //Populate items
//            @Override
//            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {
//
//                usersViewHolder.setDisplayName(users.getName());
//                usersViewHolder.setUserStatus(users.getStatus());
//                usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());
//
//                final String user_id = getRef(position).getKey();
//
//                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
//                        profileIntent.putExtra("user_id", user_id);
//                        startActivity(profileIntent);
//                    }
//                });
//
//            }
//        };
//
//
//        mUsersList.setAdapter(firebaseRecyclerAdapter);
//
//    }
//
//    // View holder class for users
//    public static class UsersViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//
//        public UsersViewHolder(View itemView) {
//            super(itemView);
//
//            mView = itemView;
//
//        }
//
//        public void setDisplayName(String name){
//
//            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
//            userNameView.setText(name);
//
//        }
//
//        public void setUserStatus(String status){
//
//            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
//            userStatusView.setText(status);
//
//
//        }
//
//        public void setUserImage(String thumb_image, Context ctx){
//
//            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
//
//            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
//
//        }
//
//
//    }
//}
