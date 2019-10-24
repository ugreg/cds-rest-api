package common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MicrosoftDynamicsDao {

    private static MicrosoftDynamicsDao microsoftDynamicsDaoInstance = null;

    String AUTHORITY = "https://login.microsoftonline.com/";
    String RESOURCE = "https://msott.crm.dynamics.com";
    String VERSION = "v9.0";
    String REST_API_URL = RESOURCE + "/api/data/" + VERSION + "/";
    String clientId = "64f4cba8-0656-4ccd-8c2a-fd269fe7636f";
    String clientSecret = "";
    String tenantID = "grdegr.onmicrosoft.com";
    String accessToken = "";

    private MicrosoftDynamicsDao() {
        authenticate();
    }

    public static MicrosoftDynamicsDao getInstance() {
        if(microsoftDynamicsDaoInstance == null) {
            microsoftDynamicsDaoInstance = new MicrosoftDynamicsDao();
        }
        return microsoftDynamicsDaoInstance;
    }

    public void get(String endpoint) {
        int maxRetries = 3;
        int retries = 0;
        boolean runnning = true;
        while(runnning) {
            try {
                OkHttpClient client = new OkHttpClient();

                String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";
                endpoint = "accounts%28" + accountId + "%29/Account_CustomerAddress";

                Request request = new Request.Builder()
                        .url(REST_API_URL + endpoint)
                        .get()
                        .addHeader("OData-MaxVersion", "4.0")
                        .addHeader("OData-Version", "4.0")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                Response response = client.newCall(request).execute();
                if(response.code() == 401 && response.message().equals("Unauthorized")) {
                    authenticate();
                    if(++retries == maxRetries) {
                        runnning = false;
                    }
                }
                else {
                    String dataReturnedFromGetAddresses = response.body().string();
                    System.out.println("end");
                }
            }
            catch (IOException e) { }
        }
    }
    
    private void authenticate() {
        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            AuthenticationResult result;
            AuthenticationContext context = new AuthenticationContext(AUTHORITY + tenantID, true, service);
            Future<AuthenticationResult> future = context.acquireToken(RESOURCE, new ClientCredential(clientId, clientSecret), null);

            result = future.get();
            accessToken = result.getAccessToken();
        }
        catch (MalformedURLException e) { }
        catch (InterruptedException e) { }
        catch (ExecutionException e) { }
    }
}
