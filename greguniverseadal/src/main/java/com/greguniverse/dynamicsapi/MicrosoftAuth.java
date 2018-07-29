package com.greguniverse.dynamicsapi;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MicrosoftAuth extends AuthBase {

    private String authority;
    private String username;
    private String password;
    private String tenantId;

    public MicrosoftAuth(String username, String password) {
    }

    @Override
    public void auth() {

        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            AuthenticationContext context = new AuthenticationContext(
                    "https://login.microsoftonline.com/",
                    false,
                    service);
            Future<AuthenticationResult> future = context.acquireToken(
                    "https://graph.microsoft.com",
                    "",
                    "",
                    "",
                    null);
            AuthenticationResult result = future.get();
            System.out.println("Access Token - " + result.getAccessToken());
            System.out.println("Refresh Token - " + result.getRefreshToken());
            System.out.println("ID Token - " + result.getIdToken());

            String url = "https://game.api.crm.dynamics.com/api/data/v9.0/accounts";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String x = response.body().string();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something went wrong with the ok http call :(");
            e.printStackTrace();
        }
    }

    public void okhttp() {
        String url = "https://game.api.crm.dynamics.com/api/data/v9.0/serviceendpoints";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String x = response.body().string();
    }
}
