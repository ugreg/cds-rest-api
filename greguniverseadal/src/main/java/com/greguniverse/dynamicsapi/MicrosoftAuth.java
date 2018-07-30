package com.greguniverse.dynamicsapi;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

import com.microsoft.aad.adal4j.ClientCredential;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            AuthenticationContext context = new AuthenticationContext(
                    "https://login.microsoftonline.com/",
                    false,
                    service);

            Future<AuthenticationResult> future = context.acquireToken(
                    "https://graph.microsoft.com/",
                    "",
                    "",
                    "",
                    null);

            AuthenticationResult result = future.get();
            System.out.println("Access Token - " + result.getAccessToken());
            String accessToken = result.getAccessToken();
            System.out.println("Refresh Token - " + result.getRefreshToken());
            String refreshToken = result.getRefreshToken();
            System.out.println("ID Token - " + result.getIdToken());

            ClientCredential clientCredential =  new ClientCredential("", "");
            Future<AuthenticationResult> futureWithSecret = context.acquireToken(
                    "https://graph.microsoft.com/",
                    clientCredential,
                    null);

            AuthenticationResult resultForFutureWithSecret = futureWithSecret.get();
            System.out.println("Access Token - " + resultForFutureWithSecret.getAccessToken());
            accessToken = resultForFutureWithSecret.getAccessToken();
            System.out.println("Refresh Token - " + resultForFutureWithSecret.getRefreshToken());
            refreshToken = resultForFutureWithSecret.getRefreshToken();

            String url = "api/data/v9.0/accounts";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("api/data/v9.0/accounts")
                    .get()
                    .addHeader("Authorization", "Bearer ")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Postman-Token", "2ad35f87-c424-4b9b-b44c-b1c8ad3611f0")
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println("Accounts " + response.body().string());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something went wrong with the ok http call :(");
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }
}
