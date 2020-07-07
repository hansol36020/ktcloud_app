package com.example.test1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class service_db extends AppCompatActivity implements View.OnClickListener {

    private DBAdapter DBAdapter;
    private DBHAgroupAdapter DBHAgroupAdapter;
    RecyclerView recyclerView;

    private List<DBData> dbData;
    private List<DBHAgroupData> dbHAgroupData;

    private Button btn_zone;
    private EditText txt_zone;

    private RadioGroup Rgroup_category;
    private RadioButton Rbnt_all, Rbnt_hagroup;

    APIcall_DB api_db = new APIcall_DB();
    final int[] list_size = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_db);

        final Handler handler = new Handler();

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF2CBBB6));


        init();

        btn_zone = (Button)findViewById(R.id.btn_db_zone_search);
        btn_zone.setOnClickListener(this);

        txt_zone = (EditText)findViewById(R.id.txt_db_zone_search);
        txt_zone.setFocusable(false);
        txt_zone.setOnClickListener(this);
        txt_zone.setText(api_db.getZone());

        final String []state = new String[100];
        final String []created = new String[100];
        final String []name = new String[100];
        final String []zoneName = new String[100];
        final String []dev = new String[100];
        final String []size = new String[100];
        final String []DBstate = new String[100];

        new Thread(new Runnable() {
            ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList
            @Override
            public void run() {
                try {
                    api_db.setState("all");//default 값 설정 - 추후 UI변경 시 수정
                    list = api_db.listMysqlDB();//이름, 상태, DB상태(보류), 용량, 생성일, 종속장치, 위치
                    list_size[0] = list.size();
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
                            Toast.makeText(getApplicationContext(), "해당 존에 DB가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근

                        System.out.println(list.get(1)[0]);
                        for (int i = 0; i < list.size(); i++) {
                            state[i] = list.get(i)[1];
                            DBstate[i] = list.get(i)[2];
                            created[i] = list.get(i)[4];
                            name[i] = list.get(i)[0];
                            zoneName[i] = list.get(i)[6];
                            dev[i] = list.get(i)[5];
                            size[i] = list.get(i)[3];
                        }

                        getData_service_db(state, created, name, zoneName, dev, size, DBstate);
                    }
                });
            }
        }).start();



        Rgroup_category = (RadioGroup) findViewById(R.id.rgroup_db_category);
        Rbnt_all = (RadioButton) findViewById(R.id.rbnt_db_all);
        Rbnt_hagroup = (RadioButton) findViewById(R.id.rbnt_db_hagroup);

        Rgroup_category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            public void onCheckedChanged(RadioGroup rgroup, int rbnt) {
                // TODO Auto-generated method stub
                switch (rbnt) {
                    case R.id.rbnt_db_all:
                        DBAdapter = new DBAdapter();
                        recyclerView.setAdapter(DBAdapter);

                        new Thread(new Runnable() {
                            ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList
                            @Override
                            public void run() {
                                try {
//                    API.setZone(zone);//default 값 설정 - UI변경되고 수정해야 함 - 수정 완료
                                    api_db.setState("all");//default 값 설정 - 추후 UI변경 시 수정
                                    list = api_db.listMysqlDB();//이름, 상태, DB상태(보류), 용량, 생성일, 종속장치, 위치
                                    list_size[0] = list.size();
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
                                            Toast.makeText(getApplicationContext(), "해당 존에 DB가 없습니다", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {//UI접근

                                        for (int i = 0; i < list.size(); i++) {
                                            state[i] = list.get(i)[1];
                                            DBstate[i] = list.get(i)[2];
                                            created[i] = list.get(i)[4];
                                            name[i] = list.get(i)[0];
                                            zoneName[i] = list.get(i)[6];
                                            dev[i] = list.get(i)[5];
                                            size[i] = list.get(i)[3];
                                        }

                                        getData_service_db(state, created, name, zoneName, dev, size, DBstate);
                                    }
                                });
                            }
                        }).start();

                        break;

                    case R.id.rbnt_db_hagroup:
                        DBHAgroupAdapter = new DBHAgroupAdapter();
                        recyclerView.setAdapter(DBHAgroupAdapter);

                        final String []hagroupname = new String[100];
                        final String []slavecount = new String[100];
                        final String []zone = new String[100];
                        final String []masterName = new String[100];
                        final String []masterStatus = new String[100];
                        final String []masterFebricStatus = new String[100];
                        final String []slaveName = new String[100];
                        final String []slaveStatus = new String[100];
                        final String []slaveFebricStatus = new String[100];
                        final String []slaveid = new String[100];
                        final String []masterid = new String[100];
                        final String []groupid = new String[100];


                        new Thread(new Runnable() {
                            ArrayList<String[]> list_ha = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList

                            @Override
                            public void run() {
                                try {
                                    api_db.setState("all");//default 값 설정 - 추후 UI변경 시 수정
                                    list_ha = api_db.listHaGroups();
                                    list_size[1] = list_ha.size();
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
                                            Toast.makeText(getApplicationContext(), "해당 존에 DB HA가 없습니다", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {//UI접근
                                        for (int i = 0; i < list_ha.size(); i++) {//이름, 아이디, slave 갯수, 위치, master이름, master 상태, slave 이름, slave 상태
                                            hagroupname[i] = list_ha.get(i)[0];
                                            groupid[i] = list_ha.get(i)[1];
                                            slavecount[i] = list_ha.get(i)[2];
                                            zone[i] = list_ha.get(i)[3];
                                            masterName[i] = list_ha.get(i)[4];
                                            masterStatus[i] = list_ha.get(i)[5];
                                            masterFebricStatus[i] = list_ha.get(i)[8];
                                            slaveName[i] = list_ha.get(i)[6] ;
                                            slaveStatus[i] = list_ha.get(i)[7];
                                            slaveFebricStatus[i] = list_ha.get(i)[9];
                                            masterid[i] = list_ha.get(i)[10];
                                            slaveid[i] = list_ha.get(i)[11];
                                        }

                                        getData_service_db_hagroup(hagroupname, slavecount, zone, masterName, masterStatus, masterFebricStatus, slaveName, slaveStatus, slaveFebricStatus,groupid,masterid,slaveid);
                                    }
                                });
                            }
                        }).start();
                        break;
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v == btn_zone || v == txt_zone) {
            final CharSequence[] zoneItem = {"전체", "Central-A", "Central-B", "Seoul-M", "Seoul-M2", "HA", "US-West"};
            AlertDialog.Builder oDialog = new AlertDialog.Builder(this);

            oDialog.setTitle("존(Zone) 위치 선택")
                    .setItems(zoneItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText tmp = (EditText)findViewById(R.id.txt_db_zone_search);
                            tmp.setText(zoneItem[which]);
                            api_db.setZone((String) zoneItem[which]);
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setCancelable(true)
                    .show();
        }



    }


    private void init() {
        // recyclerView = db list
        recyclerView = findViewById(R.id.recyclerView_service_db);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DBAdapter = new DBAdapter();
        recyclerView.setAdapter(DBAdapter);

    }


    private void getData_service_db(String[] state, String[] created, String[] name, String[] zoneName, String[] dev, String[] size, String[] DBstate) {
        // 임의의 데이터입니다.
        List<String> listState = Arrays.asList(state);
        List<String> listCreated = Arrays.asList(created);
        List<String> listName = Arrays.asList(name);
        List<String> listZone = Arrays.asList(zoneName);
        List<String> listDev = Arrays.asList(dev);
        List<String> listSize = Arrays.asList(size);
        List<String> listDBstate = Arrays.asList(DBstate);

        Integer [] tmp = new Integer[list_size[0]];
        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = R.drawable.db;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size[0]; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            DBData dbData = new DBData();
            dbData.setName(listName.get(i));
            dbData.setState(listState.get(i));
            dbData.setResId(listResId.get(i));
            dbData.setZoneName(listZone.get(i));
            dbData.setDev(listDev.get(i));
            dbData.setSize(listSize.get(i));
            dbData.setCreated(listCreated.get(i));
            dbData.setDBstate(listDBstate.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            DBAdapter.addItem(dbData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        DBAdapter.notifyDataSetChanged();
    }


    private void getData_service_db_hagroup(String[] hagroupname, String[] slavecount, String[] zone, String[] masterName,
                                            String[] masterStatus, String[] masterFebricStatus, String[] slaveName,
                                            String[] slaveStatus, String[] slaveFebricStatus, String[] groupid,
                                            String[] masterid, String[] slaveid) {
        // 임의의 데이터입니다.

        List<String> listHAgroupName = Arrays.asList(hagroupname);
        List<String> listSlaveCount = Arrays.asList(slavecount);
        List<String> listZone = Arrays.asList(zone);
        List<String> listMasterName = Arrays.asList(masterName);
        List<String> listMasterStatus = Arrays.asList(masterStatus);
        List<String> listMasterFebricStatus = Arrays.asList(masterFebricStatus);
        List<String> listSlaveName = Arrays.asList(slaveName);
        List<String> listSlaveStatus = Arrays.asList(slaveStatus);
        List<String> listSlaveFebricStatus = Arrays.asList(slaveFebricStatus);
        List<String> listgroupid = Arrays.asList(groupid);
        List<String> listmasterid = Arrays.asList(masterid);
        List<String> listslaveid = Arrays.asList(slaveid);

        Integer [] tmp = new Integer[list_size[1]];
        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = R.drawable.db;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size[1]; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            DBHAgroupData dbHAgroupData = new DBHAgroupData();
            dbHAgroupData.setResId(listResId.get(i));
            dbHAgroupData.setHagroupname(listHAgroupName.get(i));
            dbHAgroupData.setSlavecount(listSlaveCount.get(i));
            dbHAgroupData.setZone(listZone.get(i));
            dbHAgroupData.setMasterName(listMasterName.get(i));
            dbHAgroupData.setMasterStatus(listMasterStatus.get(i));
            dbHAgroupData.setMasterFebricStatus(listMasterFebricStatus.get(i));
            dbHAgroupData.setSlaveName(listSlaveName.get(i));
            dbHAgroupData.setSlaveStatus(listSlaveStatus.get(i));
            dbHAgroupData.setSlaveFebricStatus(listSlaveFebricStatus.get(i));
            dbHAgroupData.setHagroupId(listgroupid.get(i));
            dbHAgroupData.setMasterId(listmasterid.get(i));
            dbHAgroupData.setSlaveId(listslaveid.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            DBHAgroupAdapter.addItem(dbHAgroupData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        DBHAgroupAdapter.notifyDataSetChanged();
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

    /**
     * 라디오버튼 클릭 처리 함수
     */
    public void RbntClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), this.getClass());
        startActivity(intent);
    }


}