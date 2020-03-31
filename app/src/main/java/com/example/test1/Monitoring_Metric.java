package com.example.test1;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class Monitoring_Metric extends AppCompatActivity {
    private MetricAdapter adapter;
    APIcall_watch apIcall_watch = new APIcall_watch();
    final int[] server_num = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moni_watch_metric);
        final Handler handler = new Handler();

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

        RadioGroup rg = (RadioGroup) findViewById(R.id.rg_metric_opt);
        final RadioButton all = (RadioButton) findViewById(R.id.rbtn_metric_allServer);
        final RadioButton per = (RadioButton) findViewById(R.id.rbtn_metric_perServer);

        init();

        // 메트릭 그래프 옵션 선택

        final String[] serverName =  new String[100];
        final String[] opt = {"CPUUtilization", "MemoryTarget", "MemoryInternalFree", "DiskReadBytes", "DiksWriteBytes", "NetworkIn", "NetworkOut"};
        final String[] metricOpt = new String[serverName.length * 7];

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList
                try {
                    list = apIcall_watch.listServerAt("Seoul-M");//default - m존, running상태
                    server_num[0] = list.size();
                    for (int i = 0; i < server_num[0]; i++) serverName[i] = list.get(i)[0];

                    int tmp = 0;
                    for (int i = 0; i < server_num[0]; i++) {
                        for (int j  = 0; j < opt.length; j++) {
                            metricOpt[tmp++] = serverName[i] + " - " + opt[j];
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "조회할 서버가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (all.isChecked()) {
                    adapter.rmItem();
                    getData(opt, 7);
                } else if (per.isChecked()) {
                    adapter.rmItem();
                    getData(metricOpt, server_num[0]*7);
                } else {
                    adapter.rmItem();
                }
            }
        });
        menu_btn();
    }

    public void menu_btn() {
        // 메트릭 그래프 추가 옵션(주기, 통계 등) 선택
        final Button btn_status = (Button) findViewById(R.id.btn_moni_metric_status);
        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미

                getMenuInflater().inflate(R.menu.metric_status_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sum:
                                btn_status.setText("합");
                                break;
                            case R.id.max:
                                btn_status.setText("최대");
                                break;
                            case R.id.min:
                                btn_status.setText("최소");
                                break;
                            case R.id.sample:
                                btn_status.setText("샘플 수");
                                break;
                            case R.id.avg:
                                btn_status.setText("평균");
                                break;
                            default:
                                btn_status.setText("합");
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        final Button btn_period = (Button) findViewById(R.id.btn_moni_metric_period);
        btn_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미

                getMenuInflater().inflate(R.menu.metric_period_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.m5:
                                btn_period.setText("5분");
                                break;
                            case R.id.m15:
                                btn_period.setText("15분");
                                break;
                            case R.id.m30:
                                btn_period.setText("30분");
                                break;
                            case R.id.h1:
                                btn_period.setText("1시간");
                                break;
                            case R.id.h6:
                                btn_period.setText("6시간");
                                break;
                            case R.id.h12:
                                btn_period.setText("12시간");
                                break;
                            case R.id.d1:
                                btn_period.setText("1일");
                                break;
                            default:
                                btn_period.setText("5분");
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        final Button btn_cycle = (Button) findViewById(R.id.btn__moni_metric_cycle);
        btn_cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미

                getMenuInflater().inflate(R.menu.metric_cycle_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.h1:
                                btn_cycle.setText("1시간");
                                break;
                            case R.id.h3:
                                btn_cycle.setText("3시간");
                                break;
                            case R.id.h6:
                                btn_cycle.setText("6시간");
                                break;
                            case R.id.h12:
                                btn_cycle.setText("12시간");
                                break;
                            case R.id.d1:
                                btn_cycle.setText("1일");
                                break;
                            case R.id.w1:
                                btn_cycle.setText("1주일");
                                break;
                            default:
                                btn_cycle.setText("1시간");
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

//    //액션버튼 메뉴 액션바에 집어 넣기
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    private void init() {
        // recyclerView = server list
        RecyclerView recyclerView = findViewById(R.id.recyclerView_topic_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MetricAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData(String [] opt, int num) {
        // 임의의 데이터입니다.
        List<String> listOpt = Arrays.asList(opt);

        CheckBox [] tmp = new CheckBox[num];
        Boolean [] tmp_b = new Boolean[num];

        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = findViewById(R.id.cbtn_metric_opt);
            tmp_b[i] = false;
        }

        List<Boolean> listBool = Arrays.asList(tmp_b);
        List<CheckBox> listCheckBox = Arrays.asList(tmp);

        for (int i = 0; i < num; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            MetricData maData = new MetricData();
            maData.setOpt(listOpt.get(i));
            maData.setCbtn(listCheckBox.get(i));
            maData.setChecked(listBool.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(maData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
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

