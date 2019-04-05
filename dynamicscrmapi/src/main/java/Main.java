import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import okhttp3.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.String;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {

        String authority = "https://login.microsoftonline.com/";
        String resource = "https://msott.crm.dynamics.com";
        String clientId = "64f4cba8-0656-4ccd-8c2a-fd269fe7636f";
        String clientSecret = "";
        String tenantID = "grdegr.onmicrosoft.com";
        ExecutorService service = Executors.newFixedThreadPool(1);
        AuthenticationResult result;

        try {
            AuthenticationContext context = new AuthenticationContext(authority + tenantID, true, service);
            Future<AuthenticationResult> future = context.acquireToken(resource, new ClientCredential(clientId, clientSecret), null);

            result = future.get();
            String accessToken = result.getAccessToken();

            createWithDataReturned(accessToken);
        }
        catch (MalformedURLException e) { }
        catch (InterruptedException e) { }
        catch (ExecutionException e) { }
    }

    // TODO: 5
    // Retrieving customized responses on POST method:
    public static void createWithDataReturned(String accessToken) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, "{" +
                    "\"name\": \"Sample Postman Account\"," +
                    "\"creditonhold\": false," +
                    "\"address1_latitude\": 47.639583," +
                    "\"description\": \"This is the description of the sample account\"," +
                    "\"revenue\": 5000000," +
                    "\"accountcategorycode\": 1" +
                    "}");
            Request request = new Request.Builder()
                    .url("https://msott.api.crm.dynamics.com/api/data/v9.0/accounts?$select=name,creditonhold,address1_latitude,description,revenue,accountcategorycode,createdon")
                    .post(body)
                    .addHeader("OData-MaxVersion", "4.0")
                    .addHeader("OData-Version", "4.0")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Prefer", "return=representation")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "472f1651-c4e1-47c1-8a5c-6f70636181b0")
                    .build();

            Response response = client.newCall(request).execute();

            String dataReturnedFromCreate = response.body().string();

            System.out.println();
        }
        catch (IOException e) { }
    }
}
