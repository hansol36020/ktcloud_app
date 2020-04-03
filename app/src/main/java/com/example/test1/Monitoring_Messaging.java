package com.example.test1;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Monitoring_Messaging extends AppCompatActivity {
    private TopicAdapter tpAdapter;
    private List<TopicData> tpData;
    APIcall_Messaging apIcall_messaging = new APIcall_Messaging();
    final int[] list_size = new int[1];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moni_messaging);
        final Handler handler = new Handler();

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF2CBBB6));


        init();

        final String[] name = new String[200];
        final String[] pub = new String[200];
        final String[] pro = new String[200];
        final String[] rec = new String[200];

        new Thread(new Runnable() {
            ArrayList<String[]> list = new ArrayList<String[]>();//ALARM 정보를 받아올 ArrayList
            @Override
            public void run() {
                try {
                    list = apIcall_messaging.listSubscriptions();
                    list_size[0] = list.size();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "알람이 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        for (int i = 0; i < list.size(); i++) {
                            pub[i] = list.get(i)[1];
                            pro[i] = list.get(i)[2];
                            name[i] = list.get(i)[0];
                            rec[i] = list.get(i)[4];
                        }
                        getData(name, pub, pro, rec);
                    }
                });
            }
        }).start();

    }

    //액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.techcenter, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.web:
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cloud.kt.com/portal/portal.notice.html?type="));//문의하기 웹으로 전환
//                startActivity(intent);
//                break;
            case R.id.tel:
                String num ="080-2580-005";
                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));//자동 전화하기 화면으로 전환
                startActivity(intent2);
                break;
            default:
                break;
        }
        return true;
    }
    private void init() {
        // recyclerView = server list
        RecyclerView recyclerView = findViewById(R.id.recyclerView_topic_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        tpAdapter = new TopicAdapter();
        recyclerView.setAdapter(tpAdapter);
    }


    private void getData(String[] name, String[] pub, String[] pro, String[] rec) {
        // 임의의 데이터입니다.
        List<String> listName = Arrays.asList(name);
        List<String> listPub = Arrays.asList(pub);
        List<String> listPro = Arrays.asList(pro);
        List<String> listRec = Arrays.asList(rec);

        Integer [] tmp = new Integer[list_size[0]];
        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = R.drawable.alarm;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size[0]; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            TopicData data = new TopicData();
            data.setName(listName.get(i));
            data.setPub(listPub.get(i));
            data.setResId(listResId.get(i));
            data.setProtocol(listPro.get(i));
            data.setReceiver(listRec.get(i));

            // 각 값이 들어간 data를 tpAdapter에 추가합니다.
            tpAdapter.addItem(data);
        }

        // tpAdapter의 값이 변경되었다는 것을 알려줍니다.
        tpAdapter.notifyDataSetChanged();
    }

    /**
     * 하단바의 Dashboard 버튼 클릭 처리 함수
     */
    public void DashboardClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
    }

    /**
     * 하단바의 service 버튼 클릭 처리 함수
     */
    public void ServiceClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_main.class);
        startActivity(intent);
    }

    /**
     * 하단바의 Monitoring 버튼 클릭 처리 함수
     */
    public void MonitoringClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), Monitoring.class);
        startActivity(intent);
    }

    /**
     * 하단바의 Payment 버튼 클릭 처리 함수
     */
    public void PaymentClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), Payment.class);
        startActivity(intent);
    }
}
