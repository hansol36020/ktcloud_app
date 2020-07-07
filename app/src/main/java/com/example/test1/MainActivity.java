package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private String apikey = "arzhGT3dZvMRrjDU0AS8N6-4flyotixlxSVLeF0w3V8oPJknhrxoCV8aLnry-yBnxe2DqOW7Hp-eQl-GHCyoqA";
    private String secretKey = "9W4Kjkh-k9_59vjsYAyz9K_t44NB6D2rcFUeNp6PThq2364ijxi-0mNXSVbnhRJ4cOXQhQyNmOO8EZJ5ns9YLw";

    TextView apikeyText, secretkeyText;//사용자가 입력한 값 받아오기위한 변수
    CheckBox checkbox;
    boolean login_check = true;//로그인 성공 여부
    private boolean saveLogin;

    private SharedPreferences appData;

    APIcall_main API = (APIcall_main) getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, Loading.class);//로딩화면 띄우기
        startActivity(intent);

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF2CBBB6));

//        appData = getSharedPreferences("appData", MODE_PRIVATE);
//        load();
//
//        apikeyText = (TextView) findViewById(R.id.txt_user_apikey);
//        secretkeyText = (TextView) findViewById(R.id.txt_user_secretkey);
//        checkbox = (CheckBox) findViewById(R.id.checkbox_auto_login);
        Button login = (Button) findViewById(R.id.btn_login);

//        if (saveLogin) {//자동로그인 선택시 - 시연 시 로그인은 생략
//            apikeyText.setText(apikey);
//            secretkeyText.setText(secretKey);
//            checkbox.setChecked(saveLogin);
//        }

        login.setOnClickListener(new View.OnClickListener() {//로그인 버튼 클릭시 처리
            @Override
            public void onClick(View v) {
                //check_key();
                if (login_check == true) {
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                    //save();
//                    apikey = apikeyText.getText().toString().trim();
//                    secretKey = secretkeyText.getText().toString().trim();
                    API.setApikey(apikey);
                    API.setSecretKey(secretKey);
                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void save() {
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean("SAVE_LOGIN_DATA", checkbox.isChecked());
        editor.putString("apikey", apikeyText.getText().toString().trim());
        editor.putString("secretkey", secretkeyText.getText().toString().trim());
        editor.apply();
    }

    /**
     * 저장된 로그인 데이터 불러오기
     */
    private void load() {
        saveLogin = appData.getBoolean("SAVE_LOGIN_DATA", false);
        apikey = appData.getString("apikey", "");
        secretKey = appData.getString("secretkey", "");
    }

    public void check_key() {
        //api,secret key 체크하는 과정 들어가야함 - 회의 후 생략하기로함
        BackgroundThread thread = new BackgroundThread();
        thread.start();
    }

    class BackgroundThread extends Thread {
        public void run() {
            System.out.println(apikey);
            API.setApikey(apikey);
            API.setSecretKey(secretKey);
        }
    }


}
