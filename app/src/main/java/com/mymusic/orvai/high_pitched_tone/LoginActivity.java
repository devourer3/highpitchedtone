package com.mymusic.orvai.high_pitched_tone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.URLs;
import com.mymusic.orvai.high_pitched_tone.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    public static GoogleSignInClient mGoogleSignInClient;
    public static FirebaseAuth mAuth;

    String user_id, user_name, user_pic;

    TextView register;
    ImageView login;
    EditText e_user_id, e_user_pw;
    SignInButton google_login;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) { // 이미 로그인 되어 있으면, 바로 로그인 되도록 함
            finish();
            startActivity(new Intent(this, MenuActivity.class));
        }


        /**
         * 구글 로그인
         */

        google_login = findViewById(R.id.google_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // 구글 로그인 인증
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance(); // 파이어베이스 권한

        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        /**
         * 일반 로그인
         */


        ImageView main_cartoon = findViewById(R.id.login_cartoon);

        Glide.with(getApplicationContext()).asGif().load(R.drawable.login_activity_title_gif).into(main_cartoon);

        register = findViewById(R.id.register);
        register.setPaintFlags(register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        login = findViewById(R.id.login_btn);
        e_user_id = findViewById(R.id.l_user_id);
        e_user_pw = findViewById(R.id.l_user_pw);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userLogin();

                User user = new User("테스트봇", "안기범", "https://lh3.googleusercontent.com/-jVBicQHuoaE/AAAAAAAAAAI/AAAAAAAAB8A/juel45ZShyA/s60-p-rw-no-il/photo.jpg", true);
                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                finish();
                startActivity(new Intent(getApplicationContext(), MenuActivity.class)); // AWS 2개 띄울 수 없어서 그냥 로그인으로 했음.
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            @SuppressLint("RestrictedApi") Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            user_id = user.getEmail();
                            user_name = user.getDisplayName();
                            user_pic = String.valueOf(user.getPhotoUrl());
                            User g_user = new User(user_id, user_name, user_pic, true);
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(g_user);
                            finish();
                            startActivity(new Intent(getApplicationContext(), MenuActivity.class)); // 로그인 하면 프로필로 이동
                        } else {
                        }
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }







    private void userLogin() {

        final String user_id = e_user_id.getText().toString();
        final String user_pw = e_user_pw.getText().toString();

        if (TextUtils.isEmpty(user_id)) {
            e_user_id.setError("아이디를 입력하세요.");
            e_user_id.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_pw)) {
            e_user_pw.setError("비밀번호를 입력하세요.");
            e_user_pw.requestFocus();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        JSONObject userJson = obj.getJSONObject("user");

                        User user = new User(userJson.getString("user_id"), userJson.getString("user_name"), userJson.getString("user_pic"), false);

                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        finish();

                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));

                    }else{
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { // 서버에 보낼 매개변수들(아이디와 비밀번호)
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("user_pw", user_pw);
                return params; // 해시맵에 저장한 것(리턴은 하나밖에 안되는데 맵의 형태면은 배열 형태로 저장 가능)
            }
        };

        Volley.newRequestQueue(this).add(request); // 서버에 전송


    }


}
