package java.dynamicsapi;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

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

    public void auth() {
        ExecutorService service = Executors.newFixedThreadPool(1);
        AuthenticationContext context = new AuthenticationContext(authority, false, service);
        Future<AuthenticationResult> future = context.acquireToken(
                "https://graph.windows.net", tenantId, username, password,
                null);
        AuthenticationResult result = future.get();
        System.out.println("Access Token - " + result.getAccessToken());
        System.out.println("Refresh Token - " + result.getRefreshToken());
        System.out.println("ID Token - " + result.getIdToken());
    }
}
