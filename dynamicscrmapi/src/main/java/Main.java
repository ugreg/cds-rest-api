import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

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

            // TODO: 1
            // addOptionSetValuesDynamically(accessToken);
            // TODO: 2
            // TODO: 3
            // TODO: 4
            // TODO: 5
            // createWithDataReturned(accessToken);
            // TODO: 6
        }
        catch (MalformedURLException e) { }
        catch (InterruptedException e) { }
        catch (ExecutionException e) { }
        catch (IOException e) { }
    }

    // TODO: 1
    // Add Option set values dynamically using OData Web API.
    // GET all GlobalOptionSetDefinitions and find the MetadataId of the one you
    // want to insert an option into
    // https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions
    // You can use the MetadataId to see all current options for your option set
    // https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions(06d1a507-4d57-e911-a82a-000d3a1d5203)/Microsoft.Dynamics.CRM.OptionSetMetadata/Options
    // if some API calls work and others don't, most likely the user does not have the right permissions
    // SecLib::CheckPrivilege failed. User: b4cd9bac-8851-e911-a825-000d3a1d5de8,
    // PrivilegeName: prvWriteOptionSet, PrivilegeId: 2493b394-f9d7-4604-a6cb-13e1f240450d,
    // Required Depth: Basic, BusinessUnitId: 1abfdddc-8140-e911-a823-000d3a1a25b8,
    // MetadataCache Privileges Count: 2999, User Privileges Count: 682
    public static void addOptionSetValuesDynamically(String accessToken) {
        int previousValue = 0;
        String optionSetGuidString = "06d1a507-4d57-e911-a82a-000d3a1d5203";

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions%28" +
                            optionSetGuidString +
                            "%29/Microsoft.Dynamics.CRM.OptionSetMetadata/Options")
                    .get()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();
            String dataReturnedFromGetOptions = response.body().string();

            JSONObject json = new JSONObject(dataReturnedFromGetOptions);
            JSONArray jsonArray = (JSONArray) json.get("value");
            JSONObject jsonObject = (JSONObject) jsonArray.get(jsonArray.length() - 1);
            previousValue = jsonObject.getInt("Value");
        }
        catch (IOException e) { }

        String optionSetName = "new_msdatzooptionset";
        String value = Integer.toString(++previousValue);
        String label = "changeMe";
        String metadataId = "06d1a507-4d57-e911-a82a-000d3a1d5203";

        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, "{" +
                    "\"OptionSetName\": \"" + optionSetName + "\"," +
                    "\"Value\": \"" + value + "\"," +
                    "\"Label\": {" +
                        "\"LocalizedLabels\": [" +
                            "{" +
                                "\"Label\": \"" + label + "\"," +
                                "\"LanguageCode\": 1033," +
                                "\"IsManaged\": false," +
                                "\"MetadataId\": \"" + metadataId + "\"," +
                                "\"HasChanged\": null" +
                            "}" +
                        "]," +
                        "\"UserLocalizedLabel\": {" +
                            "\"Label\": \"" + label + "\"," +
                            "\"LanguageCode\": 1033," +
                            "\"IsManaged\": false," +
                            "\"MetadataId\": \"" + metadataId + "\"," +
                            "\"HasChanged\": null" +
                        "}" +
                    "}," +
                    "\"Description\": {" +
                        "\"LocalizedLabels\": [" +
                            "{" +
                                "\"Label\": \"\"," +
                                "\"LanguageCode\": 1033," +
                                "\"IsManaged\": false," +
                                "\"MetadataId\": \"" + metadataId + "\"," +
                                "\"HasChanged\": null" +
                            "}" +
                        "]," +
                        "\"UserLocalizedLabel\": {" +
                            "\"Label\": \"\"," +
                            "\"LanguageCode\": 1033," +
                            "\"IsManaged\": false," +
                            "\"MetadataId\": \"" + metadataId + "\"," +
                            "\"HasChanged\": null" +
                        "}" +
                    "}" +
                "}");
            Request request = new Request.Builder()
                    .url("https://msott.api.crm.dynamics.com/api/data/v9.0/InsertOptionValue")
                    .post(body)
                    .addHeader("OData-MaxVersion", "4.0")
                    .addHeader("OData-Version", "4.0")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println("End");
        }
        catch (IOException e) { }
    }

    // TODO: 2
    // Create custom party list using OOTB

    // TODO: 3
    // Retrieve Audit history data using OData Web API.
    // This URL /api/data/v9.1/audits This provides the overall summary. But we need the individual
    public static void audit(String accessToken) {}

    // TODO: 4
    // Associate:
    // How to avoid multiple OData Web API service call?
    // Functional Scenario:
    // - We have Account entity and Address entity.
    // - We need to associate multiple address (more than 2 address) to a particular Account record.
    // - We have created N:1 relationship between Address entity and Account entity.
    public static void associateAccounts(String accessToken) {

    }
    // - We have created one sub-grid for address entity using related record and placed that in Account entity.
    public static void associateAccountSubGrid(String accessToken) {

    }
    // - Consider we associated 5 addresses to one account record.
    // - We want to retrieve these 5 records with respect to one single account.
    // In that case we are executing the service call 5 times, which leads to performance issue.
    public static void associatedAccountAddresses(String accessToken) {

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
                    .url("https://msott.api.crm.dynamics.com/api/data/v9.0/accounts" +
                            "?$select=name,creditonhold,address1_latitude,description,revenue,accountcategorycode,createdon")
                    .post(body)
                    .addHeader("OData-MaxVersion", "4.0")
                    .addHeader("OData-Version", "4.0")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Prefer", "return=representation")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();

            String dataReturnedFromCreate = response.body().string();

            System.out.println(dataReturnedFromCreate);
        }
        catch (IOException e) { }
    }
}
