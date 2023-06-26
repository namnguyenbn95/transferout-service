package vn.vnpay.commoninterface.common;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RestClient {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private Gson gson;

    public String post(String url, String requestStr) throws Exception {
        log.info("[RestClient] POST url: {}", url);
        log.info("[RestClient] POST request: {}", requestStr);

        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), requestStr);
        Request request = new Request.Builder().url(url).post(reqBody).build();
        Response response = okHttpClient.newCall(request).execute();
        String responseStr = response.body().string();
        log.info("[RestClient] POST response: {}", responseStr);
        return responseStr;
    }

    public <T> T postBank(String url, String jsonReq, Class<T> resType) throws Exception {
        log.info("[RestClient] POST url: {}", url);
        log.info("[RestClient] POST request: {}", jsonReq);

        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonReq);
        Request request = new Request.Builder().url(url).post(reqBody).build();
        Response response = okHttpClient.newCall(request).execute();
        String responseStr = response.body().string();
        log.info("[RestClient] POST response: {}", responseStr);
        return gson.fromJson(responseStr, resType);
    }

    public String get(String url) throws Exception {
        log.info("[RestClient] GET url: {}", url);
        Request request = new Request.Builder().url(url).get().build();
        Response response = okHttpClient.newCall(request).execute();

        String responseStr = response.body().string();
        log.info("[RestClient] GET response: {}", responseStr);
        return responseStr;
    }

    public String post(String url, String requestStr, String username, String password) throws Exception {
        log.info("[RestClient] POST url: {}", url);
        log.info("[RestClient] POST request: {}", requestStr);

        OkHttpClient okHttpClient = createAuthenticatedClient(username, password);
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), requestStr);
        String credential = Credentials.basic(username, password);
        Request request = new Request.Builder().url(url).post(reqBody)
                .header("Authorization", credential)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String responseStr = response.body().string();
        log.info("[RestClient] POST response: {}", responseStr);
        return responseStr;
    }

    public String postCardRetrieve(String url, String requestStr, String header) throws Exception {
        log.info("[RestClient] POST url: {}", url);
        log.info("[RestClient] POST request: {}", requestStr);

        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), requestStr);
        Request request = new Request.Builder()
                .header("authorization", header)
                .header("x-app", "digibank")
                .url(url).post(reqBody).build();
        Response response = okHttpClient.newCall(request).execute();
        String responseStr = response.body().string();
        log.info("[RestClient] POST response: {}", responseStr);
        return responseStr;
    }

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        return httpClient;
    }
}
