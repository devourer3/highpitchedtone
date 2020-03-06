package com.mymusic.orvai.high_pitched_tone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
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
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.URLs;
import com.mymusic.orvai.high_pitched_tone.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText ed_user_id, ed_user_pw, ed_pw_confirm, ed_user_name;
    private ImageView select_user_pic;
    private Bitmap bitmap = null;
    public BitmapDrawable default_img;
    public Button reg_btn;
    private TextView id_duplicated_txt, password_match_txt;
    public FloatingActionButton back_btn;
    public AlertDialog.Builder builder;
    private SparseBooleanArray booleanArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }
        booleanArray = new SparseBooleanArray();
        id_duplicated_txt = findViewById(R.id.id_already_user_txt);
        password_match_txt = findViewById(R.id.password_confirm_txt);
        ed_user_id = findViewById(R.id.l_user_id);
        ed_user_pw = findViewById(R.id.user_pw);
        ed_user_name = findViewById(R.id.p_user_name);
        ed_pw_confirm = findViewById(R.id.user_pw_confirm);
        select_user_pic = findViewById(R.id.image_pic);
        reg_btn = findViewById(R.id.reg_btn);
        back_btn = findViewById(R.id.fab_back_btn);
        reg_btn.setBackgroundResource(R.drawable.radius6_1);

        ed_user_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    if (s.toString().equals("jackson")) {
                        id_duplicated_txt.setTextColor(Color.rgb(255,166,0));
                        id_duplicated_txt.setText("동일한 아이디가 존재합니다.");
                        booleanArray.delete(0);
                        reg_btn.setBackgroundResource(R.drawable.radius6_1);
                    } else {
                        id_duplicated_txt.setTextColor(Color.GREEN);
                        id_duplicated_txt.setText("사용 가능한 아이디 입니다.");
                        booleanArray.put(0, true);
                        if(booleanArray.size()==3){
                            reg_btn.setBackgroundResource(R.drawable.radius6);
                        }
                    }
                } else {
                    id_duplicated_txt.setTextColor(Color.RED);
                    id_duplicated_txt.setText("아이디는 최소 6자 이상입니다.");
                    booleanArray.delete(0);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ed_pw_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(ed_user_pw.getText().toString())){
                    password_match_txt.setTextColor(Color.GREEN);
                    password_match_txt.setText("일치합니다.");
                    booleanArray.put(1, true);
                    if(booleanArray.size()==3){
                        reg_btn.setBackgroundResource(R.drawable.radius6);
                    }
                } else {
                    password_match_txt.setTextColor(Color.RED);
                    password_match_txt.setText("비밀번호가 일치하지 않습니다.");
                    booleanArray.delete(1);
                    reg_btn.setBackgroundResource(R.drawable.radius6_1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ed_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>=1){
                    booleanArray.put(2, true);
                    if(booleanArray.size()==3){
                        reg_btn.setBackgroundResource(R.drawable.radius6);
                    }
                } else {
                    booleanArray.delete(2);
                    reg_btn.setBackgroundResource(R.drawable.radius6_1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap == null) {
                    registerUser();
                } else {
                    registerUser_image(bitmap);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }


        select_user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_show();
            }
        });
    }

    void dialog_show() {
        final List<String> itemslist = new ArrayList<>();
        itemslist.add("카메라");
        itemslist.add("앨범");
        final CharSequence[] item = itemslist.toArray(new String[itemslist.size()]);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필 사진 선택");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(RegisterActivity.this, Camera_facedetection.class);
                    startActivityForResult(intent, 200);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 100);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { // 매니페스트에서 android:configChanges="orientation" 하면, 화면 회전 시 destroy 되지 않는다.
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                //displaying selected image to imageview
//                select_user_pic.setImageBitmap(bitmap);
                select_user_pic.setImageURI(imageUri);


//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos); // 2번째 매개변수가 이미지 압축률 조절(0%~100%)
//                byte[] imgBytes = bos.toByteArray();
//                final String imageString_tmp = Base64.encodeToString(imgBytes, Base64.DEFAULT);
//                Log.d("kb_base64테스트",imageString_tmp);
                /**
                 * base64 인코딩 (bitmap -> String)
                 */


//                byte[] decodedString = Base64.decode(String값, Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                /**
                 * base64 디코딩 (String -> bitmap)
                 */

                //calling the method uploadBitmap to upload image
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            String picture = data.getStringExtra("picture");
            select_user_pic.setImageURI(Uri.parse(picture));
//            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            select_user_pic.setImageBitmap(bitmap);
        }
    }


    private void registerUser() {

        final String user_id = ed_user_id.getText().toString().trim();
        final String user_pw = ed_user_pw.getText().toString().trim();
        final String user_pw_confirm = ed_pw_confirm.getText().toString().trim();
        final String user_name = ed_user_name.getText().toString().trim();

        if (TextUtils.isEmpty(user_id)) {
            ed_user_id.setError("아이디를 입력해 주세요.");
            ed_user_id.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_pw)) {
            ed_user_pw.setError("비밀번호를 입력해 주세요.");
            ed_user_pw.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_name)) {
            ed_user_name.setError("이름을 입력해 주세요.");
            ed_user_name.requestFocus();
            return;
        }

        if (!TextUtils.equals(user_pw, user_pw_confirm)) {
            ed_pw_confirm.setError("비밀번호를 다시한번 확인해주세요.");
            ed_pw_confirm.requestFocus();
            return;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        default_img = (BitmapDrawable) getResources().getDrawable(R.drawable.user_silhouette);
        bitmap = default_img.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] imgBytes = bos.toByteArray();
        final String imageString = Base64.encodeToString(imgBytes, Base64.DEFAULT);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) { // 회원가입 하자마자 자동 로그인 되는 부분
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("user");
                        User user = new User(userJson.getString("user_id"), userJson.getString("user_name"), userJson.getString("user_pic"), false);
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        finish();
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                    } else {
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("user_pw", user_pw);
                params.put("user_name", user_name);
                params.put("user_pic", imageString);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }


    private void registerUser_image(final Bitmap bitmap) {

        final String user_id = ed_user_id.getText().toString().trim();
        final String user_pw = ed_user_pw.getText().toString().trim();
        final String user_pw_confirm = ed_pw_confirm.getText().toString().trim();
        final String user_name = ed_user_name.getText().toString().trim();

        if (TextUtils.isEmpty(user_id)) {
            ed_user_id.setError("아이디를 입력해 주세요.");
            ed_user_id.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_pw)) {
            ed_user_pw.setError("비밀번호를 입력해 주세요.");
            ed_user_pw.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_name)) {
            ed_user_name.setError("이름을 입력해 주세요.");
            ed_user_name.requestFocus();
            return;
        }

        if (!TextUtils.equals(user_pw, user_pw_confirm)) {
            ed_pw_confirm.setError("비밀번호를 다시한번 확인해주세요.");
            ed_pw_confirm.requestFocus();
            return;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] imgBytes = bos.toByteArray();
        final String imageString = Base64.encodeToString(imgBytes, Base64.DEFAULT);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REGISTER, new Response.Listener<String>() {

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
                    } else {
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("user_pw", user_pw);
                params.put("user_name", user_name);
                params.put("user_pic", imageString);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }


}