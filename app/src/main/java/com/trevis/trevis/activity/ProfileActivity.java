package com.trevis.trevis.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.trevis.trevis.R;


import android.app.ProgressDialog;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;
    private String device_token;

    public static final String KEY_FCM_SENDER_ID = "sender_id";
    public static final String KEY_FCM_TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Get extra
        final String profile_name = getIntent().getStringExtra("tappedUserName");
        final String profile_status = getIntent().getStringExtra("tappedUserStatus");










//        mRootRef = FirebaseDatabase.getInstance().getReference();
//
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
//        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
//        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
//        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = (Button) findViewById(R.id.profile_decline_btn);

        //Default state
        mCurrent_state = "not_friends";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mProfileName.setText(profile_name);
        mProfileStatus.setText(profile_status);

        mProgressDialog.dismiss();


//        //Should change below code
//        mUsersDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                //Set properties of selected user
//
//                String display_name = dataSnapshot.child("name").getValue().toString();
//                String status = dataSnapshot.child("status").getValue().toString();
//                String image = dataSnapshot.child("image").getValue().toString();
//                device_token = dataSnapshot.child("device_token").getValue().toString();
//
//                mProfileName.setText(display_name);
//                mProfileStatus.setText(status);
//
//                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);
//
//                // If own account
//                if(mCurrent_user.getUid().equals(user_id)){
//                    //Remove decline button
//                    mDeclineBtn.setEnabled(false);
//                    mDeclineBtn.setVisibility(View.INVISIBLE);
//                    //Remove req send button
//                    mProfileSendReqBtn.setEnabled(false);
//                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);
//                }
//
//
//                //Send Friend Requests Database by current user's UID
//                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        //Check whether selected user id is in the friend requests set
//                        if(dataSnapshot.hasChild(user_id)){
//
//                            //Get req type
//                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
//
//                            if(req_type.equals("received")){
//                                mCurrent_state = "req_received";
//                                mProfileSendReqBtn.setText("Accept Friend Request");
//
//                                //Display decline btn
//                                mDeclineBtn.setVisibility(View.VISIBLE);
//                                mDeclineBtn.setEnabled(true);
//                            }
//                            else if(req_type.equals("sent")) {
//                                mCurrent_state = "req_sent";
//                                mProfileSendReqBtn.setText("Cancel Friend Request");
//
//                                //Hide decline btn
//                                mDeclineBtn.setVisibility(View.INVISIBLE);
//                                mDeclineBtn.setEnabled(false);
//                            }
//
//                            mProgressDialog.dismiss();
//
//                        } else {
//
//                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                    if(dataSnapshot.hasChild(user_id)){
//
//                                        mCurrent_state = "friends";
//                                        mProfileSendReqBtn.setText("Unfriend this Person");
//
//                                        mDeclineBtn.setVisibility(View.INVISIBLE);
//                                        mDeclineBtn.setEnabled(false);
//
//                                    }
//                                    mProgressDialog.dismiss();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    mProgressDialog.dismiss();
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //Disable After clicking on that
//                mProfileSendReqBtn.setEnabled(false);
//
//                // If not friends
//                if(mCurrent_state.equals("not_friends")){
//
//                    //push() will create a push id, means a random id
//                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
//                    String newNotificationId = newNotificationref.getKey();
//
//                    //HashMap for Notification data
//                    HashMap<String, String> notificationData = new HashMap<>();
//                    notificationData.put("from", mCurrent_user.getUid());
//                    notificationData.put("type", "request");
//
////                    mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            String device_token = dataSnapshot.child("device_token").getValue().toString();
////                            Log.d("Token ekaaa",device_token);
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
//
//
//
//
//
//                    mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            sendNotification();
//                        }
//                    });
//                    //Map for requests
//                    Map requestMap = new HashMap();
//                    // Adding values by dividing forward slashes
//                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
//                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
//                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);
//
//                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                            if(databaseError != null){
//                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
//                            }
//                            else {
//                                mCurrent_state = "req_sent";
//                                mProfileSendReqBtn.setText("Cancel Friend Request");
//                            }
//
//                            mProfileSendReqBtn.setEnabled(true);
//                        }
//                    });
//
//                }
//
//
//                //Cancel requests state
//                if(mCurrent_state.equals("req_sent")){
//
//                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            // Remove values in requests
//                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                    mProfileSendReqBtn.setEnabled(true);
//                                    mCurrent_state = "not_friends";
//                                    mProfileSendReqBtn.setText("Send Friend Request");
//
//                                    mDeclineBtn.setVisibility(View.INVISIBLE);
//                                    mDeclineBtn.setEnabled(false);
//
//
//                                }
//                            });
//
//                        }
//                    });
//
//                }
//
//
//                //Request Received state
//                if(mCurrent_state.equals("req_received")){
//
//                    //Get date
//                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
//
//                    Map friendsMap = new HashMap();
//                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
//                    friendsMap.put("Friends/" + user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);
//
//                    friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
//                    friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);
//
//
//                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//
//                            if(databaseError == null){
//
//                                mProfileSendReqBtn.setEnabled(true);
//                                mCurrent_state = "friends";
//                                mProfileSendReqBtn.setText("Unfriend this Person");
//
//                                mDeclineBtn.setVisibility(View.INVISIBLE);
//                                mDeclineBtn.setEnabled(false);
//
//                            } else {
//
//                                String error = databaseError.getMessage();
//                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }
//
//
//                //Unfriend state
//                if(mCurrent_state.equals("friends")){
//
//                    Map unfriendMap = new HashMap();
//                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
//                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);
//
//                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//
//                            if(databaseError == null){
//
//                                mCurrent_state = "not_friends";
//                                mProfileSendReqBtn.setText("Send Friend Request");
//
//                                mDeclineBtn.setVisibility(View.INVISIBLE);
//                                mDeclineBtn.setEnabled(false);
//
//                            } else {
//
//                                String error = databaseError.getMessage();
//
//                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
//
//
//                            }
//
//                            mProfileSendReqBtn.setEnabled(true);
//
//                        }
//                    });
//
//                }
//
//
//            }
//        });
//
//        //Decline Button listener
//        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //create a map
//                Map friendsDeclineMap = new HashMap();
//                friendsDeclineMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
//                friendsDeclineMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);
//
//                mRootRef.updateChildren(friendsDeclineMap, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                        if(databaseError == null){
//                            mProfileSendReqBtn.setEnabled(true);
//                            mCurrent_state = "not_friends";
//                            mProfileSendReqBtn.setText("Send Friend Request");
//                            mDeclineBtn.setVisibility(View.INVISIBLE);
//                            mDeclineBtn.setEnabled(false);
//                        }
//                        else {
//
//                            String error = databaseError.getMessage();
//                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
    }

    private void sendNotification() {
//        //send Push Notification
//        HttpsURLConnection connection = null;
//        try {
//
//            URL url = new URL("https://fcm.googleapis.com/fcm/send");
//            connection = (HttpsURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json");
//            //Put below you FCM API Key instead
//            connection.setRequestProperty("Authorization", "key="
//                    + "AAAA2U0NWhk:APA91bEIyK83CJMgM4NkA5JlO42laoT6VRAmVp4kv-DVdQB01-Eraam02B6tUiA6rYiBYrP2A08VZhgpFi2qDv4knkPi5_S2Ug7XHxzl5ILGN3-2h3tc17LMMhcMJRtqP_6KRBb_Ub2U");
//
//            JSONObject root = new JSONObject();
//            JSONObject data = new JSONObject();
//            data.put(KEY_FCM_TEXT, text);
//            data.put(KEY_FCM_SENDER_ID, senderId);
//            root.put("data", data);
//            root.put("to", receiverId);
//
//
//            byte[] outputBytes = root.toString().getBytes("UTF-8");
//            OutputStream os = connection.getOutputStream();
//            os.write(outputBytes);
//            os.flush();
//            os.close();
//            connection.getInputStream(); //do not remove this line. request will not work without it gg
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) connection.disconnect();
//        }




        String authKey = "AAAA2U0NWhk:APA91bEIyK83CJMgM4NkA5JlO42laoT6VRAmVp4kv-DVdQB01-Eraam02B6tUiA6rYiBYrP2A08VZhgpFi2qDv4knkPi5_S2Ug7XHxzl5ILGN3-2h3tc17LMMhcMJRtqP_6KRBb_Ub2U"; // You FCM AUTH key
        String FMCurl = "https://fcm.googleapis.com/fcm/send";

        try {
            URL url = new URL(FMCurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + authKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject data = new JSONObject();
            data.put("to", device_token.trim());
            JSONObject info = new JSONObject();
            info.put("title", "FCM Notification Title"); // Notification title
            info.put("body", "Hello First Test notification"); // Notification body
            data.put("notification", info);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data.toString());
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}