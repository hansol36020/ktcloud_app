package com.example.test1;

import android.app.Application;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Base64.Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class APIcall_main extends Application {

    private static String baseurl, apikey, secretKey, zone = "", state = "all";
    private static final String TAG = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * @brief baseurl 정보를 설정하는 함수
     * @param z 설정하길 원하는 zone 문자열
     **/
    protected static void setBaseurl(String z){
        baseurl = z;
    }

    /**
     * @brief 사용자의 APIKEY를 설정하는 함수
     * @param z 설정하길 원하는 zone 문자열
     **/
    protected static void setApikey(String z){
        apikey = z;
    }

    /**
     * @brief 사용자의 SECRETKEY를 설정하는 함수
     * @param z 설정하길 원하는 zone 문자열
     **/
    protected static void setSecretKey(String z){
        secretKey = z;
    }

    /**
     * @brief 사용자의 APIKEY를 리턴하는 함수
     **/
    protected static String getApikey(){
        return  apikey;
    }

    /**
     * @brief 사용자의 APIKEY를 리턴하는 함수
     **/
    protected static String getZone(){
        return  zone;
    }

    /**
     * @brief 조회할 Zone의 정보를 설정하는 함수
     * @param z 설정하길 원하는 zone 문자열
     **/
    protected static void setZone(String z){
        zone = z;
    }

    /**
     * @brief 조회할 State의 정보를 설정하는 함수
     * @param s 설정하길 원하는 state 문자열
     **/
    protected static void setState(String s){
        state = s;
    }

    /**
     * @param request command 생성에 필요한 값을 저장하는, 알파벳 순으로 정렬된 request
     * @return 생성된 signature
     * @brief 암호화 및 인코딩을 통한 signature 생성을 위한 함수
     **/
    private static String generateSig(TreeMap<String, String> request) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        List<String> list = new ArrayList<String>();

        Set<String> keys = request.keySet();

        for (String key : keys) {
            list.add(key.toLowerCase() + "=" + URLEncoder.encode(request.get(key), "UTF-8").replace("+", "%20").toLowerCase());
        }

        String sig_str = String.join("&", list);

//        Log.d(TAG,"SIG : " + sig_str);
//        Log.d(TAG,"api : " + apikey);
//        Log.d(TAG,"secret : " + secretKey);

        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);

        byte[] encryptedBytes = mac.doFinal(sig_str.getBytes());
//        Log.d(TAG,"encrypt : " + encryptedBytes);

        Encoder encoder = Base64.getEncoder();
        String signature = encoder.encodeToString(encryptedBytes);

        signature = URLEncoder.encode(signature, "UTF-8");
//        Log.d(TAG,"signature: " + signature);

        return signature;
    }

    /**
     * @param request command 생성에 필요한 값을 저장하는, 알파벳 순 정렬 이전의 map
     * @return 생성된  최종 request url
     * @brief 생성된 signature를 사용하여 최종 request url 작성을 위한 함수
     **/
    static String generateReq(TreeMap<String, String> request) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        //1. Quote_plus = replace ' ' with '+'
        //2. Making signature
        //3. final request query

        Set<String> keys = request.keySet();
        List<String> list = new ArrayList<String>();

        for (String key : keys) {
            list.add(key + "=" + request.get(key));
        }

        String request_str = String.join("&", list);

        String signature = generateSig(request);
        request_str = request_str + "&signature=" + signature;
        String req = baseurl + request_str;

        return req;
    }

    /**
     * @param button  호출할 api를 지정하는 변수
     * @param request command 생성에 필요한 값을 저장하는, 알파벳 순 정렬 이전의 map
     * @return 지정된 api 호출에 필요한 값을 저장하는 최종 request
     * @brief button값에 따라 호출할 api에 해당하는 baseurl 및 command를 정의하기 위한 함수
     **/
    static TreeMap<String, String> generateRequire(int button, TreeMap<String, String> request) {

        switch (button) {
            case 1:  // Watch 관련 기능
                if (zone.equals("Seoul-M2"))
                    baseurl = "https://api.ucloudbiz.olleh.com/watch/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/watch/v1/client/api?";
                request.put("command", "listMetrics");
                break;
            case 2: // Server 조회
                baseurl = "https://api.ucloudbiz.olleh.com/server/v1/client/api?";
                request.put("command", "listVirtualMachines");

                if (zone.equals("Central-A"))
                    request.put("zoneid", "eceb5d65-6571-4696-875f-5a17949f3317");
                else if (zone.equals("Central-B"))
                    request.put("zoneid", "9845bd17-d438-4bde-816d-1b12f37d5080");
                else if (zone.equals("Seoul-M"))
                    request.put("zoneid", "95e2f517-d64a-4866-8585-5177c256f7c7");
                else if (zone.equals("Seoul-M2")) {
                    request.put("zoneid", "d7d0177e-6cda-404a-a46f-a5b356d2874e");
                    baseurl = "https://api.ucloudbiz.olleh.com/server/v2/client/api?";
                } else if (zone.equals("HA"))
                    request.put("zoneid", "dfd6f03d-dae5-458e-a2ea-cb6a55d0d994");
                else if (zone.equals("US-West"))
                    request.put("zoneid", "b7eb18c8-876d-4dc6-9215-3bd455bb05be");

                if (!state.equals("all")) request.put("state", state);

                break;
            case 3: // Server 정지
                if (zone.equals("Seoul-M2"))
                    baseurl = "https://api.ucloudbiz.olleh.com/server/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/server/v1/client/api?";
                request.put("command", "stopVirtualMachine");
                break;
            case 4: // Server 시작
                if (zone.equals("Seoul-M2"))
                    baseurl = "https://api.ucloudbiz.olleh.com/server/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/server/v1/client/api?";
                request.put("command", "startVirtualMachine");
                break;
            case 5: // Server 재시작
                if (zone.equals("Seoul-M2"))
                    baseurl = "https://api.ucloudbiz.olleh.com/server/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/server/v1/client/api?";
                request.put("command", "rebootVirtualMachine");
                break;
            case 6: // Metric 통계
                if(zone.equals("Seoul-M2")) baseurl = "https://api.ucloudbiz.olleh.com/watch/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/watch/v1/client/api?";
                request.put("command", "getMetricStatistics" );
                break;
            case 7: // Metric에 대한 알람리스트 조회
                if(zone.equals("Seoul-M2")) baseurl = "https://api.ucloudbiz.olleh.com/watch/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/watch/v1/client/api?";
                request.put("command", "listAlarms" );
                break;

            case 8: // Metric에 대한 알람이력 조회
                if(zone.equals("Seoul-M2")) baseurl = "https://api.ucloudbiz.olleh.com/watch/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/watch/v1/client/api?";
                request.put("command", "listAlarmHistory" );
                request.put("historyitemtype", "StateUpdate" );
                break;

            case 9: // 특정 Metric에 대한 알람리스트 조회
                if(zone.equals("Seoul-M2")) baseurl = "https://api.ucloudbiz.olleh.com/watch/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/watch/v1/client/api?";
                request.put("command", "listAlarmsForMetric" );
                break;

            case 10: // 토픽 리스트 조회
                if(zone.equals("Seoul-M2")) baseurl = "https://api.ucloudbiz.olleh.com/messaging/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/messaging/v1/client/api?";
                request.put("command", "listTopics" );
                break;

            case 11: // 구독 리스트 조회
                if(zone.equals("Seoul-M2")) baseurl = "https://api.ucloudbiz.olleh.com/messaging/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/messaging/v1/client/api?";
                request.put("command", "listSubscriptions" );
                break;

            case 12: // 선택한 토픽의 모든 구독자들에게 메시지 발송
                if(zone.equals("Seoul-M2")) baseurl = "https://api.ucloudbiz.olleh.com/messaging/v2/client/api?";
                else baseurl = "https://api.ucloudbiz.olleh.com/messaging/v1/client/api?";
                request.put("command", "publish" );
                break;

            case 15: // Disk 조회
                baseurl = "https://api.ucloudbiz.olleh.com/server/v1/client/api?";
                request.put("command", "listVolumes" );

                if(zone.equals("Central-A")) request.put("zoneid", "eceb5d65-6571-4696-875f-5a17949f3317");
                else if (zone.equals("Central-B")) request.put("zoneid", "9845bd17-d438-4bde-816d-1b12f37d5080");
                else if (zone.equals("Seoul-M"))  request.put("zoneid", "95e2f517-d64a-4866-8585-5177c256f7c7");
                else if (zone.equals("Seoul-M2")) {
                    request.put("zoneid", "d7d0177e-6cda-404a-a46f-a5b356d2874e");
                    baseurl = "https://api.ucloudbiz.olleh.com/server/v2/client/api?";
                }
                else if (zone.equals("HA"))  request.put("zoneid", "dfd6f03d-dae5-458e-a2ea-cb6a55d0d994");
                else if (zone.equals("US-West"))  request.put("zoneid", "b7eb18c8-876d-4dc6-9215-3bd455bb05be");
                break;

            case 16: // LB 조회
                baseurl = "https://api.ucloudbiz.olleh.com/loadbalancer/v1/client/api?";
                request.put("command", "listLoadBalancers" );

                if(zone.equals("Central-A")) request.put("zoneid", "eceb5d65-6571-4696-875f-5a17949f3317");
                else if (zone.equals("Central-B")) request.put("zoneid", "9845bd17-d438-4bde-816d-1b12f37d5080");
                else if (zone.equals("Seoul-M"))  request.put("zoneid", "95e2f517-d64a-4866-8585-5177c256f7c7");
                else if (zone.equals("Seoul-M2")) {
                    request.put("zoneid", "d7d0177e-6cda-404a-a46f-a5b356d2874e");
                    baseurl = "https://api.ucloudbiz.olleh.com/loadbalancer/v2/client/api?";
                }
                else if (zone.equals("HA"))  request.put("zoneid", "dfd6f03d-dae5-458e-a2ea-cb6a55d0d994");
                else if (zone.equals("US-West"))  request.put("zoneid", "b7eb18c8-876d-4dc6-9215-3bd455bb05be");
                break;

            case 17: // LB에 등록된 웹서버 조회
                baseurl = "https://api.ucloudbiz.olleh.com/loadbalancer/v1/client/api?";
                request.put("command", "listLoadBalancerWebServers" );

                if(zone.equals("Central-A")) request.put("zoneid", "eceb5d65-6571-4696-875f-5a17949f3317");
                else if (zone.equals("Central-B")) request.put("zoneid", "9845bd17-d438-4bde-816d-1b12f37d5080");
                else if (zone.equals("Seoul-M"))  request.put("zoneid", "95e2f517-d64a-4866-8585-5177c256f7c7");
                else if (zone.equals("Seoul-M2")) {
                    request.put("zoneid", "d7d0177e-6cda-404a-a46f-a5b356d2874e");
                    baseurl = "https://api.ucloudbiz.olleh.com/loadbalancer/v2/client/api?";
                }
                else if (zone.equals("HA"))  request.put("zoneid", "dfd6f03d-dae5-458e-a2ea-cb6a55d0d994");
                else if (zone.equals("US-West"))  request.put("zoneid", "b7eb18c8-876d-4dc6-9215-3bd455bb05be");
                break;

            default:
        }
        return request;
    }


    /**
     * @param req_message 데이터를 받아올 URL
     * @return 지정된 api 호출에 필요한 값을 저장하는 최종 request
     * @throws ParseException
     * @throws IOException
     * @brief URL로부터 JSON 데이터를 받아오는 함수
     **/
    public static JSONObject readJsonFromUrl(String req_message) throws ParseException, IOException {
        URL url = new URL(req_message);

        BufferedReader bf;
        String line = "";
        String result = "";

        bf = new BufferedReader(new InputStreamReader(url.openStream()));

        while ((line = bf.readLine()) != null) {
            result = result.concat(line);
        }

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);

        return obj;
    }

}
