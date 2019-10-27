package common;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import okhttp3.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class MicrosoftDataAccessObject {

    protected static final String AUTHORITY = "https://login.microsoftonline.com/";

    protected String apiVersion = "v9.0";
    protected String tenantId;
    protected String resource;
    protected String restApiUrl;
    protected String clientId = "64f4cba8-0656-4ccd-8c2a-fd269fe7636f";
    protected String clientSecret = "";
    protected String accessToken = "";
    protected OkHttpClient okHttpClient;

    void authenticate() {
        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            AuthenticationResult result;
            AuthenticationContext context
                    = new AuthenticationContext(AUTHORITY + tenantId, true, service);
            Future<AuthenticationResult> future
                    = context.acquireToken(this.resource, new ClientCredential(this.clientId, this.clientSecret), null);

            result = future.get();
            accessToken = result.getAccessToken();
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("InterruptedException "  + e.getMessage());
        } catch (ExecutionException e) {
            System.out.println("ExecutionException, have you provided the correct client id and secret pair? " +
                    e.getMessage());
        }
    }
}
