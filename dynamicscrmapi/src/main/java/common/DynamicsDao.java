package common;

import javafx.util.Pair;
import okhttp3.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DynamicsDao extends MicrosoftDataAccessObject {

    private static DynamicsDao microsoftDynamicsDaoInstance = null;

    private DynamicsDao(String environment, String tenant) {
        this.tenantId = tenant + ".onmicrosoft.com";
        String unitedStatesCrmDomain = ".crm.dynamics.com";
        this.resource = "https://" + environment + unitedStatesCrmDomain;
        this.restApiUrl
                = "https://" + environment + ".api" + unitedStatesCrmDomain + "/api/data/" + this.apiVersion + '/';
        authenticate();
    }

    public static DynamicsDao getInstance(String environment, String tenant) {
        if (microsoftDynamicsDaoInstance == null) {
            microsoftDynamicsDaoInstance = new DynamicsDao(environment, tenant);
        }
        return microsoftDynamicsDaoInstance;
    }

    public String get(String path) throws MalformedURLException, InterruptedException, ExecutionException {
        try {
            okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(restApiUrl + path)
                    .get()
                    .addHeader("OData-MaxVersion", "4.0")
                    .addHeader("OData-Version", "4.0")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Accept", "*/*")
                    .addHeader("cache-control", "no-cache")
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "UNDEFINED";
        } catch (IOException e) {
            e.printStackTrace();
            return "UNDEFINED";
        }
    }

    public void post(String path, RequestBody body, Pair<String, String>... additionalHeaders)
            throws MalformedURLException, InterruptedException, ExecutionException {
        try {
            okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(restApiUrl + path);
            builder.post(body);
            Map<String, String> headersMap = new HashMap<String, String>();
            headersMap.put("OData-MaxVersion", "4.0");
            headersMap.put("OData-Version", "4.0");
            headersMap.put("Authorization", "Bearer " + accessToken);
            headersMap.put("Accept", "application/json");
            headersMap.put("Content-Type", "application/json; charset=utf-8");
            headersMap.put("cache-control", "no-cache");
            if (additionalHeaders.length > 0) {
                for (Pair<String, String> header : additionalHeaders) {
                    headersMap.put(header.getKey(), header.getValue());
                }
            }
            Headers headers = Headers.of(headersMap);
            builder.headers(headers);
            Request request = builder.build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
