package com.example.test1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class APIcall_DB extends APIcall_main {
    /**
     * @brief APIcall_DB 클래스의 생성자
     **/
    public APIcall_DB(){
        this.baseurl = "https://api.ucloudbiz.olleh.com/db/v1/client/api?";
    }
    /**
     * @brief DB 인스턴스의 zone과 인스턴스 id로 Displayname을 받아오는 함수
     * @param zonename displayname을 알기를 원하는 DB 인스턴스의 zone 이름
     * @return zonename과 id이 일치하는 DB 인스턴스의 displayname, 찾지못했을 경우 "" 반환
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     **/
    private String getDisplaynameById(String zonename, String instanceids) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        try{
            TimeUnit.MILLISECONDS.sleep(500);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String displayname = "";

        int button = 20;

        TreeMap<String, String> request = new TreeMap<String, String>();

        if(zonename.equals("kr-1")) super.zone = "Seoul-M2";

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("instanceids", instanceids);

        String req_message = generateReq(request);

        String[] instanceidArr = instanceids.split(",");

		/*
		System.out.println("Request Message is...");
		System.out.println(req_message);
		 */

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listinstancesresponse = (JSONObject) obj.get("listinstancesresponse");

        // 조회된 DB 인스턴스가 존재할 경우에만 검색
        if(parse_listinstancesresponse.size() > 0 ) {

            JSONObject parse_listinstancelist= (JSONObject) parse_listinstancesresponse.get("instancelist");

            JSONArray parse_instance = (JSONArray) parse_listinstancelist.get("instance");

            JSONObject instance;

            for(int i=0; i < instanceidArr.length; i++) {

                for(int j=0; j < parse_instance.size(); j++) {
                    instance = (JSONObject) parse_instance.get(j);

                    if(instanceidArr[i].equals(instance.get("instanceid"))) {

                        if(i != 0) displayname =  displayname + "," + String.valueOf(instance.get("instancename"));
                        else displayname += String.valueOf(instance.get("instancename"));
                    }
                }
            }

        }


        return displayname;

    }

    /**
     * @throws ParseException
     * @return Database의 이름, 상태, DB상태(보류), 용량, 생성일, 종속장치, 위치를 가지는 arraylist
     * @brief Database 기능에 해당하는, 생성된 mysql DB 정보 출력을 위한 함수
     **/
    public ArrayList<String[]> listMysqlDB() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 20;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);

//        System.out.println("Request Message is...");
//        System.out.println(req_message);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listinstancesresponse = (JSONObject) obj.get("listinstancesresponse");

        JSONObject parse_listinstancelist= (JSONObject) parse_listinstancesresponse.get("instancelist");

        JSONArray parse_instance = (JSONArray) parse_listinstancelist.get("instance");

        JSONObject instance;
        ArrayList<String[]> list = new ArrayList<String[]>();

        for(int i = 0 ; i < parse_instance.size(); i++) {
            instance = (JSONObject) parse_instance.get(i);

            String dbstatus =null;
            if(((String)instance.get("instancestatus")).equals("Running"))dbstatus="정상";
            else dbstatus = "알수없음";

            //이름, 상태, DB상태(보류), 용량, 생성일, 종속장치, 위치
            list.add(new String[]{(String) instance.get("instancename"),(String)instance.get("instancestatus"),
                    dbstatus, (String)instance.get("storagesize") + " GB", (String)instance.get("instancecreationtime")
                    , (String) instance.get("instancetype"), (String)instance.get("zone")});
        }

        return list;
    }
    /**
     * @throws ParseException
     * @brief Database 기능에 해당하는, DB의 HA 그룹 정보 출력을 위한 함수
     **/
    public ArrayList<String[]> listHaGroups() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 21;


        TreeMap<String, String> request = new TreeMap<String, String>();

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());


        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listhagroupsresponse = (JSONObject) obj.get("listhagroupsresponse");

        JSONObject parse_hagrouplist= (JSONObject) parse_listhagroupsresponse.get("hagrouplist");

        JSONArray parse_hagroup = (JSONArray) parse_hagrouplist.get("hagroup");
        ArrayList<String[]> list = new ArrayList<String[]>();

        JSONObject hagroup;
        JSONObject master;

        for(int i = 0 ; i < parse_hagroup.size(); i++) {
            hagroup = (JSONObject) parse_hagroup.get(i);

            // HA 그룹 내의 마스터, 슬레이브 정보 저장
            String[] instanceStatusArr = new String[ Integer.parseInt(String.valueOf(hagroup.get("slavecount")))+1 ];
            String[] instanceFebricStatusArr = new String[ Integer.parseInt(String.valueOf(hagroup.get("slavecount")))+1 ];
            List<String> instanceId = new ArrayList<String>();

            System.out.println("HA 그룹명: "+ hagroup.get("hagroupname"));
            System.out.println("HA 그룹 id: "+ hagroup.get("hagroupid"));
            System.out.println("slave 갯수: "+ hagroup.get("slavecount"));

            //위치, master이름/상태, slave이름/상태
            master = (JSONObject) hagroup.get("master");

            String HAzone = String.valueOf(master.get("zone"));
            System.out.println("위치: "+ HAzone);

            instanceStatusArr[0] = String.valueOf(master.get("instancestatus"));
            instanceFebricStatusArr[0] = String.valueOf(master.get("instancefabricstatus"));
            instanceId.add(String.valueOf(master.get("instanceid")));

            JSONObject parse_slavelist = (JSONObject) hagroup.get("slavelist");
            JSONArray parse_slave = (JSONArray) parse_slavelist.get("slave");
            JSONObject slave;

            for(int j = 0 ; j < parse_slavelist.size(); j++) {
                slave = (JSONObject) parse_slave.get(j);

                instanceStatusArr[j+1] = String.valueOf(slave.get("instancestatus"));
                instanceFebricStatusArr[j+1] = String.valueOf(slave.get("instancefabricstatus"));
                instanceId.add(String.valueOf(slave.get("instanceid")));

            }
            // 마스터, 슬레이브 위치 및 id를 통해 인스턴스 이름을 받아와서 배열에 저장
            String[] instanceArr = getDisplaynameById(HAzone, String.join(",", instanceId)).split(",");

            System.out.println("master 이름: "+ instanceArr[0]);
            System.out.println("master id: "+ instanceId.get(0));
            System.out.println("master 상태: "+ instanceStatusArr[0]);
            System.out.println("master Febric Status 상태: "+ instanceFebricStatusArr[0]);

            for(int j=1; j<instanceArr.length; j++) {
                System.out.println(j + "번 째 slave 이름: "+ instanceArr[j]);
                System.out.println(j + "번 째 slave id: "+ instanceId.get(j));
                System.out.println(j + "번  slave 상태: "+ instanceStatusArr[j]);
                System.out.println(j + "번  slave Febric Status 상태: "+ instanceFebricStatusArr[j]);
            }

            System.out.println();
            //이름, 아이디, slave 갯수, 위치, master이름, master 상태, slave 이름, slave 상태, master-모드, slave-모드 ,masterid, slaveid
            list.add(new String[]{(String) hagroup.get("hagroupname"),(String)hagroup.get("hagroupid"),
                    (String)hagroup.get("slavecount"), HAzone, instanceArr[0], instanceStatusArr[0],
                    instanceArr[1], instanceStatusArr[1], instanceFebricStatusArr[0],
                    instanceFebricStatusArr[1],instanceId.get(0),instanceId.get(1)});
        }
        return list;
    }

    /**
     * @throws ParseException
     * @brief Database 기능에 해당하는, 복제 그룹의 슬레이브의 수를 변경하기 위한 함수
     **/

    public String updateHaGroupSlaveCount(String hagroupid, String instanceid) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 22;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("hagroupid", hagroupid);
        request.put("instanceid", instanceid);

        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_updatehagrouppromoteresponse = (JSONObject) obj.get("updatehagrouppromoteresponse");

        String hapromotestatus = String.valueOf(parse_updatehagrouppromoteresponse.get("hapromotestatus"));

        System.out.print("HA promote status: "+ hapromotestatus);
        return hapromotestatus;
    }
}