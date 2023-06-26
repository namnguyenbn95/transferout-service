/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.commoninterface.ott;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.OkHttpClient.Builder;
import org.bouncycastle.util.encoders.Base64;

import javax.net.ssl.*;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author nam
 */
@Slf4j
public class NoticeClient {

    private static NoticeClient instance = new NoticeClient();
    private OkHttpClient okClient = null;
    private static int TIME_OUT = 180;
    private static final SSLContext trustAllSslContext;
    private static String URL = "";
    private static String aesKey = "";
    private static String macKey = "";
    private String bankCode = "870436";

    private NoticeClient() {
    }

    public static NoticeClient getInstance() {
        if (instance == null) {
            instance = new NoticeClient();
        }
        return instance;
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }};

    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static final SSLSocketFactory trustAllSslSocketFactory =
            trustAllSslContext.getSocketFactory();

    public static OkHttpClient trustAllSslClient(OkHttpClient client) {

        Builder builder = client.newBuilder();
        builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return builder.build();
    }

    private OkHttpClient getOkClientInstance() {
        if (okClient == null) {
            OkHttpClient clientOrig = new Builder().retryOnConnectionFailure(true).build();
            okClient = clientOrig
                    .newBuilder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS).build();
        }
        return okClient;
    }

    public String generateUnique() {
        String guid = UUID.randomUUID().toString();
        return guid;
    }

    public boolean sendMessage(Entity entity, String rawData) throws Exception {
        //System.out.println("------------ send a message -----------------");
        aesKey = entity.getAesKey();
        macKey = entity.getMacKey();
        bankCode = entity.getBankCode();
        URL = entity.getUrl();

        MessageEntity me = new MessageEntity();
        me.setCif(entity.getCiff());
        me.setContent(entity.getContent());
        me.setIsEncrypt(0);
        me.setMobile(entity.getPhone());
        me.setType(Integer.valueOf(entity.getType()));
        me.setPriority(1);
        me.setMessageId(generateUnique());
        me.setRawData(rawData);
        me.setUrlImg(entity.getUrlImg());

        Gson gson = new Gson();

        String data = gson.toJson(me);


        byte[] iv = AESService.generateIV();

        byte[] encryptByte = AESService.encrypt(aesKey, data, iv);

        byte[] finalEncrypt = new byte[iv.length + encryptByte.length];
        System.arraycopy(iv, 0, finalEncrypt, 0, iv.length);
        System.arraycopy(encryptByte, 0, finalEncrypt, iv.length, encryptByte.length);

        String encryptData = new String(Base64.encode(finalEncrypt));
        System.out.println(encryptData);

        String inputMac = bankCode + encryptData;

        String mac = new String(Base64.encode(HMAC256.createMac(inputMac.getBytes("UTF-8"), macKey.getBytes())));
        log.info(mac);
        RequestBody formBody = new FormBody.Builder().add("m", mac).add("b", bankCode)
                .add("e", encryptData).add("t", Long.toString(System.currentTimeMillis())).build();
        String url = URL;
        log.info(url);
        Request request = new Request.Builder().url(url).post(formBody).cacheControl(CacheControl.FORCE_NETWORK).build();
        Response response = getOkClientInstance().newCall(request).execute();

        String res = new String(response.body().bytes(), "UTF-8");
        log.info("res: " + res);


        JsonReader reader = new JsonReader(new StringReader(res));
        reader.setLenient(true);

        NoticeResponse noticeRes = gson.fromJson(reader, NoticeResponse.class);

        String e = noticeRes.getE();

        byte[] allEncryptByte = Base64.decode(e);
        String stringFinalEncrypt = new String(allEncryptByte, "UTF-8");

        byte[] clientMacB = HMAC256.createMac((stringFinalEncrypt + noticeRes.getT()).getBytes(), macKey.getBytes());
        String clientMac = new String(Base64.encode(clientMacB));
        log.info("clientMac: " + clientMac);
        if (noticeRes.getM() != null && !"".equals(noticeRes.getM())) {
            if (clientMac.equals(noticeRes.getM())) {
                iv = new byte[AESService.IV_LENGTH];
                System.arraycopy(allEncryptByte, 0, iv, 0, iv.length);
                byte[] dataByte = new byte[allEncryptByte.length - AESService.IV_LENGTH];
                System.arraycopy(allEncryptByte, iv.length, dataByte, 0, dataByte.length);
                String decrypteRes = AESService.decrypt(aesKey, dataByte, iv);
                log.info("Decrypt: " + dataByte.length + " value" + decrypteRes);
                BaseResponse baseRes = gson.fromJson(decrypteRes, BaseResponse.class);
                return baseRes != null && baseRes.getCode().equals("00");
            } else {
                log.info("Wrong mac");
            }
        } else {
            log.info("Invalid bank");
        }
        return false;
    }

    /*public boolean sendNotification(String mobile, String content) throws Exception {
        SendMessageRequest rq = new SendMessageRequest();
        rq.setContent(content);
        rq.setMessageId(java.util.UUID.randomUUID().toString());
        rq.setMobile(mobile);
        rq.setType(21);
        Gson gson =new Gson();
        String data =gson.toJson(rq);
        String aesKey = Constants.aesKey;
        String macKey = Constants.macKey;
        byte[] iv = AESService.generateIV();
        byte[] encryptByte = AESService.encrypt(aesKey, data, iv);
        byte[] finalEncrypt = new byte[iv.length + encryptByte.length];
        System.arraycopy(iv, 0, finalEncrypt, 0, iv.length);
        System.arraycopy(encryptByte, 0, finalEncrypt, iv.length, encryptByte.length);
        String encryptData = new String(Base64.encode(finalEncrypt));
        String bankCode = "970488";
        String inputMac = bankCode + encryptData;
        String mac = new String(Base64.encode(HMACMD5.createMac(inputMac.getBytes("UTF-8"),macKey.getBytes())));
        RequestBody formBody = new FormBody.Builder()
                .add("m", mac).add("b", bankCode)
                .add("e", encryptData).add("t", Long.toString(System.currentTimeMillis()))
                .build();
        Request request = new Request.Builder()
                .url(Constants.OTTUrl + "/sendNotification")
                .post(formBody)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        log.info("sendNotification request: " + request.toString());
        Response response = getOkClientInstance().newCall(request).execute();
        String res = new String(response.body().bytes(),"UTF-8");
        log.info("sendNotification res: " + res);
        if(res == null){
            return false;
        }
        JsonReader reader = new JsonReader(new StringReader(res));
        reader.setLenient(true);
        NoticeResponse noticeRes = gson.fromJson(reader, NoticeResponse.class);
        String e = noticeRes.getE();
        byte[] allEncryptByte = Base64.decode(e);
        byte[] clientMacB = HMACMD5.createMac(allEncryptByte, macKey.getBytes());
        String clientMac = new String(Base64.encode(clientMacB));
        if (clientMac.equals(noticeRes.getM())) {
            iv = new byte[AESService.IV_LENGTH];
            System.arraycopy(allEncryptByte, 0, iv, 0, iv.length);
            byte[] dataByte = new byte[allEncryptByte.length - AESService.IV_LENGTH];
            System.arraycopy(allEncryptByte, iv.length, dataByte, 0, dataByte.length);
            String decrypteRes= AESService.decrypt(aesKey, dataByte, iv);
            BaseResponse baseRes = gson.fromJson(decrypteRes, BaseResponse.class);
            return baseRes!= null && baseRes.getCode().equals("00");
        } else {
            log.info("sendNotification Wrong mac");
        }
        return false;
    }*/
}
