package com.greguniverse.dynamicsapi;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

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

        // zs3wkoJAPOloYJMi+1MM8eAj33cO6Xo6RmxGbJl2bac=

        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            String authorityUrl = "/";
            ExecutorService executor = Executors.newSingleThreadExecutor();
            AuthenticationContext context = new AuthenticationContext(authorityUrl, false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                    "https://graph.windows.net", tenantId, username, password,
                    null);
            AuthenticationResult result = future.get();
            System.out.println("Access Token - " + result.getAccessToken());
            System.out.println("Refresh Token - " + result.getRefreshToken());
            System.out.println("ID Token - " + result.getIdToken());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
