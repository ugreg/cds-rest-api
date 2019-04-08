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

    // TODO: 1
    // Add Option set values dynamically using OData Web API.
    // https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions(06d1a507-4d57-e911-a82a-000d3a1d5203)/Microsoft.Dynamics.CRM.OptionSetMetadata/Options
    public static void addOptionSetValuesDynamically(String accessToken) {
        String metadataId = "06d1a507-4d57-e911-a82a-000d3a1d5203";

        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "{" +
                    "OptionSetName: \"new_msdatzooptionset\"," +
                    "Value: \"100000004\"," +
                    "Label: {" +
                        "LocalizedLabels: [" +
                            "{" +
                                "Label: \"e\",\n                LanguageCode: 1033,\n                IsManaged: false,\n                MetadataId: \"09d1a507-4d57-e911-a82a-000d3a1d5203\",\n                HasChanged: null\n            }\n        ],\n        UserLocalizedLabel: {\n            Label: \"e\",\n            LanguageCode: 1033,\n            IsManaged: false,\n            MetadataId: \"09d1a507-4d57-e911-a82a-000d3a1d5203\",\n            HasChanged: null\n        }\n    },\n    Description: {\n        LocalizedLabels: [\n            {\n                Label: \"\",\n                LanguageCode: 1033,\n                IsManaged: false,\n                MetadataId: \"09d1a507-4d57-e911-a82a-000d3a1d5203\",\n                HasChanged: null\n            }\n        ],\n        UserLocalizedLabel: {\n            Label: \"\",\n            LanguageCode: 1033,\n            IsManaged: false,\n            MetadataId: \"09d1a507-4d57-e911-a82a-000d3a1d5203\",\n            HasChanged: null\n        }\n    }\n}");
            Request request = new Request.Builder()
                    .url("https://msott.api.crm.dynamics.com/api/data/v9.0/InsertOptionValue")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "d42c5a62-c763-45a8-9149-c0aa82bd58fe")
                    .build();

            Response response = client.newCall(request).execute();
        }
        catch (IOException e) { }
    }

    // TODO: 2
    // Create custom party list using OOTB.

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
