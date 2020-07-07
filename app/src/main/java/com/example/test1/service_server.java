package com.example.test1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class service_server extends AppCompatActivity implements View.OnClickListener {
    public static Context mContext;
    Handler handler = new Handler();
    private ServerAdapter svAdpater;
    private Button btn_zone;
    private EditText txt_zone;

    int list_size;

    APIcall_main API = (APIcall_main) getApplication();
    APIcall_server api_server = new APIcall_server();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_server);

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF2CBBB6));


        init();
        list_size=0;
        mContext = this;

        final String[] state = new String[100];
        final String[] created = new String[100];
        final String[] name = new String[100];
        final String[] zonename = new String[100];
        final String[] osname = new String[100];
        final String[] id = new String[100];
        final String[] addr = new String[100];
        final String[] spec = new String[100];
        final String[] disk = new String[100];


        btn_zone = (Button)findViewById(R.id.btn_server_zone_search);
        btn_zone.setOnClickListener(this);

        txt_zone = (EditText)findViewById(R.id.txt_server_zone_search);
        txt_zone.setFocusable(false);
        txt_zone.setOnClickListener(this);
        //api_server.setZone("Seoul-M");
        txt_zone.setText(api_server.getZone());

        //사용자가 입력한 위치, 상태에 따른 서버 목록 가져오기
        new Thread(new Runnable() {
            ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList
            @Override
            public void run() {
                try {
                    api_server.setState("all");
                    list = api_server.listServers();
                    list_size = list.size();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "해당 존에 서버가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        for (int i = 0; i < list_size; i++) {//서버명, 스펙, 상태, 생성일시, 운영체제, 내부주소, CPU/메모리, 디스크
                            state[i] = list.get(i)[2];
                            created[i] = list.get(i)[3] ;
                            name[i] = list.get(i)[0];
                            zonename[i] = api_server.getZone();
                            osname[i] = list.get(i)[4];
                            id[i] = list.get(i)[5];
                            spec[i] = list.get(i)[1];
                            addr[i] = list.get(i)[7];

                        }
                        getData_service_s(state, created, name, zonename, osname, id, spec, addr, disk);
                    }
                });
            }
        }).start();
    }

    private void init() {
        // recyclerView = server list
        RecyclerView recyclerView = findViewById(R.id.recyclerView_service_server);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        svAdpater = new ServerAdapter();
        recyclerView.setAdapter(svAdpater);
    }

//    //액션버튼 메뉴 액션바에 집어 넣기
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    private void getData_service_s(String[] state, String[] created, String[] name, String[] zonename,
                                   String[] osname,String[] id, String[] spec, String[] addr, String[] disk) {
        // 임의의 데이터입니다.
        List<String> listState = Arrays.asList(state);
        List<String> listCreated = Arrays.asList(created);
        List<String> listName = Arrays.asList(name);
        List<String> listZone = Arrays.asList(zonename);
        List<String> listOsname = Arrays.asList(osname);
        List<String> listid = Arrays.asList(id);
        List<String> listspec = Arrays.asList(spec);
        List<String> listaddr = Arrays.asList(addr);
        List<String> listdisk = Arrays.asList(disk);

        Integer[] tmp = new Integer[list_size];

        for (int i = 0; i < tmp.length; i++) {
            if (osname[i].contains("centos"))
                tmp[i] = R.drawable.linux;
            else
                tmp[i] = R.drawable.window;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size; i++) {//가진 객체만큼으로 수정
            // 각 List의 값들을 data 객체에 set 해줍니다.
            ServerData sData = new ServerData();
            sData.setName(listName.get(i));
            sData.setState(listState.get(i));
            sData.setResId(listResId.get(i));
            sData.setZonename(listZone.get(i));
            sData.setOsname(listOsname.get(i));
            sData.setCreated(listCreated.get(i));
            sData.setId(listid.get(i));
            sData.setAddr(listaddr.get(i));
            sData.setCpu(listspec.get(i));
            sData.setDisk(listdisk.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            svAdpater.addItem(sData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        svAdpater.notifyDataSetChanged();
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
    /**
     * @brief 새로고침 버튼 클릭 처리 함수
     * @param v
     */
    public void onButtonClicked_refresh(View v) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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


    @Override
    public void onClick(View v) {
        if (v == btn_zone || v == txt_zone) {
            final CharSequence[] zoneItem = {"전체","Central-A", "Central-B", "Seoul-M", "Seoul-M2", "HA", "US-West"};

            AlertDialog.Builder oDialog = new AlertDialog.Builder(this);

            oDialog.setTitle("존(Zone) 위치 선택")
                    .setItems(zoneItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText tmp = (EditText)findViewById(R.id.txt_server_zone_search);
                            tmp.setText(zoneItem[which]);
                            api_server.setZone((String) zoneItem[which]);
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setCancelable(true)
                    .show();
        }
    }
}
