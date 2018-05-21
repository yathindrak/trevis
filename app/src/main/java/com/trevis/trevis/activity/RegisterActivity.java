package com.trevis.trevis.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.trevis.trevis.R;
import com.trevis.trevis.modal.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mMobile;
    private RadioGroup genderGroup;
    private RadioButton genderPick;
    private Button mCreateBtn;

    //Firebase Auth
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    //ProgressDialog
    ProgressDialog mRegProgress;

    // Instantiate the RequestQueue.
    RequestQueue queue;
    String url ="http://ec2-54-255-152-162.ap-southeast-1.compute.amazonaws.com:9000/add";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //url = url.replaceAll(" ", "%20");

        //Toolbar Set
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Android Fields
        mDisplayName = (TextInputLayout) findViewById(R.id.register_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.register_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mMobile = (TextInputLayout) findViewById(R.id.reg_mobile);
        genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);

        //sendPOST();
        mRegProgress = new ProgressDialog(this);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get inputs from the user
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                String mobile = mMobile.getEditText().getText().toString();

                // get selected radio button from radioGroup
                int selectedGender = genderGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                genderPick = (RadioButton) findViewById(selectedGender);
                //Get the text
                String gender = (String) genderPick.getText();

                System.out.println("Gender : "+gender);

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(display_name, email, password, mobile, gender);

                }



            }
        });
    }


    private void register_user(final String name, final String email, final String password, final String mobile, final String gender) {

        // Create a new volley request queue
        queue = Volley.newRequestQueue(getApplicationContext());

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // Task is successful means user has registered successfully
                if(task.isSuccessful()){

                    mRegProgress.hide();

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    // Get the user device token
                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

                    //Set user properties
                    User newUser = new User();
                    newUser.setUserId(uid);
                    newUser.setDeviceToken(device_token);
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setMobile(mobile);
                    newUser.setGender(gender);

                    final Gson gson = new Gson();
                    String json = gson.toJson(newUser);

                    Log.d("TAG", json);

                    JSONObject jsonBody = null;
                    try {
                        jsonBody = new JSONObject(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, jsonBody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("TAG", response.toString());
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            return params;
                        }
                    };
                    mQueue.add(jsonObjectRequest);

                    // Go to the main intent
                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    //Add new task and clear previous tasks: unless when we press back it will still go to the start page
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot register. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
