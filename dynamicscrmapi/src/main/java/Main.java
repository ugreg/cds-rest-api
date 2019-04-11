import java.io.IOException;
import java.lang.String;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    static final String AUTHORITY = "https://login.microsoftonline.com/";
    static final String RESOURCE = "https://msott.crm.dynamics.com";
    static final String VERSION = "v9.0";
    static final String REST_API_URL = RESOURCE + "/api/data/" + VERSION + "/";

    public static void main(String[] args) {

        String clientId = "64f4cba8-0656-4ccd-8c2a-fd269fe7636f";
        String clientSecret = "";
        String tenantID = "grdegr.onmicrosoft.com";

        ExecutorService service = Executors.newFixedThreadPool(1);
        AuthenticationResult result;

        try {
            AuthenticationContext context = new AuthenticationContext(AUTHORITY + tenantID, true, service);
            Future<AuthenticationResult> future = context.acquireToken(RESOURCE, new ClientCredential(clientId, clientSecret), null);

            result = future.get();
            String accessToken = result.getAccessToken();

            // TODO: 1
            // addOptionSetValuesDynamically(accessToken);
            // TODO: 2
            createEmailWithPartyList(accessToken);
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
    // Create custom party list using OOTB Party List (Field to select Multiple lookup values):
    // Functional Scenario:
    // We have Account entity and Contact entity.
    // We need to select multiple Contacts for single Account.
    // Like Email Entity To, BCC, CC fields.
    // https://docs.microsoft.com/en-us/dynamics365/customer-engagement/web-api/activityparty?view=dynamics-ce-odata-9
    // participationtypemask defines the role on the email
    // https://community.dynamics.com/crm/f/117/t/174608
    public static void createEmailWithPartyList(String accessToken) {
        try {
            OkHttpClient client = new OkHttpClient();

            String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";

            Request request = new Request.Builder()
                    .url(REST_API_URL + "accounts" +
                            "%28" + accountId + "%29/contact_customer_accounts")
                    .get()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();
            String dataReturnedFromGetOptions = response.body().string();

            JSONObject json = new JSONObject(dataReturnedFromGetOptions);
            JSONArray jsonArray = (JSONArray) json.get("value");
            Queue<String> contactIds = new LinkedList<String>();
            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                contactIds.add(jsonObject.getString("contactid"));
            }

            final int SENDER_PARTICIPATION_TYPE_MASK = 1;
            final int TO_PARTICIPATION_TYPE_MASK = 2;
            final int CC_PARTICIPATION_TYPE_MASK = 3;
            final int BCC_PARTICIPATION_TYPE_MASK = 4;
            String senderId = "96b856f4-134c-e911-a823-000d3a1d5de8";
            MediaType mediaType = MediaType.parse("application/json");
            String contacts = "";
            for(String id : contactIds){
                contacts += ",{\"partyid_contact@odata.bind\": \"/contacts(" + id + ")\"," +
                        "\"participationtypemask\": " + TO_PARTICIPATION_TYPE_MASK + "}";
            }
            String requestBodyContent = "{" +
                        "\"email_activity_parties\": " +
                        "[" +
                            "{" +
                                "\"partyid_systemuser@odata.bind\": \"/systemusers(" + senderId +")\"," +
                                "\"participationtypemask\": " + SENDER_PARTICIPATION_TYPE_MASK +
                            "}" + contacts +
                        "]" +
                    "}";
            RequestBody body = RequestBody.create(mediaType, requestBodyContent);
            request = new Request.Builder()
                    .url(REST_API_URL + "emails")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            response = client.newCall(request).execute();

            System.out.println();
        }
        catch(IOException e) {}
    }

    // TODO: 3
    // Retrieve Audit history data using OData Web API.
    // This URL /api/data/v9.1/audits This provides the overall summary. But we need the individual
    public static void audit(String accessToken) {
//        try {
//            System.out.println("end");
//        }
//        catch (IOException e) {}
    }

    // TODO: 4
    // Associate:
    // How to avoid multiple OData Web API service call?
    // Functional Scenario:
    // - We have Account entity and Address entity.
    // - We need to associate multiple address (more than 2 address) to a particular Account record.
    // - We have created N:1 relationship between Address entity and Account entity.
    public static void associateAccounts(String accessToken) {
//        try {
//            System.out.println("end");
//        }
//        catch (IOException e) {}
    }
    // - We have created one sub-grid for address entity using related record and placed that in Account entity.
    public static void associateAccountSubGrid(String accessToken) {
//        try {
//            System.out.println("end");
//        }
//        catch (IOException e) {}
    }
    // - Consider we associated 5 addresses to one account record.
    // - We want to retrieve these 5 records with respect to one single account.
    // In that case we are executing the service call 5 times, which leads to performance issue.
    public static void associatedAccountAddresses(String accessToken) {
//        try {
//            System.out.println("end");
//        }
//        catch (IOException e) {}
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

    //  6. Bulk Operation
    // - When we create bulk operation it returns code as 200 , response message as ok and input stream as batchresponse_8f8077da-1e2a-433e-84fa-d856365fcsdfsd
    // - We can't get exact response and status of the batch process
    // - It’s not reflected in the CRM
    //- Sample code (SampleBulkInsert.txt)
    public static void bulkInsert(String accessToken) {
//        try {
//            System.out.println("end");
//        }
//        catch (IOException e) {}
    }
}
