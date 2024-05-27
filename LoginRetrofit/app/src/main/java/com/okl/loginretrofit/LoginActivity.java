package com.okl.loginretrofit;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonElement;
import com.okl.loginretrofit.model.data;
import com.okl.loginretrofit.remote.MyResponse;
import com.okl.loginretrofit.remote.RestClient;
import com.okl.loginretrofit.remote.RestMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
private static String device_id;
    private final String TAG = LoginActivity.class.getSimpleName();

    EditText edtId;
    EditText edtPhone;
    Button btnLogin;
    RestMethods restMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Builds HTTP Client for API Calls
        restMethods = RestClient.buildHTTPClient();

         setContent();

        btnLogin.setOnClickListener(v -> {
            doLogin();
        });

    }

    private void doLogin(){
       restMethods.login(edtId.getText().toString(), edtPhone.getText().toString())
               .enqueue(new Callback<data>() {
                   @SuppressLint("HardwareIds")
                   @Override
                   public void onResponse(@NonNull Call<data> call, @NonNull Response<data> response) {
//                       MyResponse myResponse = response.body();
                       Log.e(TAG, "onResponse: "+response.body());
                       /*if(response.isSuccessful()) {
                           try {
//                               Log.e(TAG, "onResponse: raw"+response.raw());
                               assert response.body() != null;
                               device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                               JSONObject json = new JSONObject(response.body().string());
                               Log.e(TAG, "onResponse: " + json);
                               MyResponse myResponse = new JSONObject()
                                       .put()

                               Log.e(TAG, "onResponse ID: " + device_id);
                           } catch (JSONException | IOException e) {
                               e.printStackTrace();
                           }
                       }*/
                       Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onFailure(@NonNull Call<data> call, @NonNull Throwable t) {
                       t.printStackTrace();

                       //Response failed
                       Log.e(TAG, "Response: " + t.getMessage());
                       Toast.makeText(LoginActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                   }
               });
    }
    void setContent() {
        edtId = findViewById(R.id.id);
        edtPhone = findViewById(R.id.phone);
        btnLogin = findViewById(R.id.btnLogin);
    }

}