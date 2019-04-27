import java.io.IOException;
import java.lang.String;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.JsonObject;
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
            // addGlobalOptionSetValuesDynamically(accessToken);
            addLocalOptionSetValuesDynamically(accessToken);
            // TODO: 2
            // createEmailWithPartyList(accessToken);
            // TODO: 3
            // readEntityAuditHistory(accessToken);
            // TODO: 4
            // getAssociatedAccountAddresses(accessToken);
            // TODO: 5
            // createWithDataReturned(accessToken);
            // TODO: 6
            // bacthAccountPost(accessToken);
        }
        catch (MalformedURLException e) { }
        catch (InterruptedException e) { }
        catch (ExecutionException e) { }
        catch (IOException e) { }
    }

    // TODO: 1
    // Check if local optoinset meta data GUID does not exists
    public static void addGlobalOptionSetValuesDynamically(String accessToken) {
        int previousValue = 0;
        String optionSetGuidString = "06d1a507-4d57-e911-a82a-000d3a1d5203";

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(REST_API_URL + "GlobalOptionSetDefinitions%28" +
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
        String label = "newOptionLabel";
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
                    .url(REST_API_URL + "InsertOptionValue")
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
    public static void addLocalOptionSetValuesDynamically(String accessToken) {
        int previousValue = 0;
        String entityLogicalname = "cr965_testcdsentity";
        String optionSetLogicalName = "new_localoptionsettoform";

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(REST_API_URL + "EntityDefinitions%28LogicalName=%27" + entityLogicalname +
                            "%27%29/Attributes/Microsoft.Dynamics.CRM.PicklistAttributeMetadata" +
                            "?$select=LogicalName&$filter=LogicalName%20eq%20%27" + optionSetLogicalName +
                            "%27&$expand=OptionSet")
                    .get()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();
            String dataReturnedFromGetOptions = response.body().string();

            JSONObject odataResponse = new JSONObject(dataReturnedFromGetOptions);
            JSONArray optionsetMetadataArray = (JSONArray) odataResponse.get("value");
            JSONObject optionsetMetadataObject = (JSONObject) optionsetMetadataArray.get(0);
            JSONObject optionSet = optionsetMetadataObject.getJSONObject("OptionSet");
            JSONArray optionSetOptions = (JSONArray) optionSet.get("Options");
            JSONObject option = (JSONObject) optionSetOptions.get(optionSetOptions.length() - 1);
            previousValue = option.getInt("Value");

            System.out.println("End");
        }
        catch (IOException e) { }

        String optionValue = Integer.toString(++previousValue);
        String optionLabel = "newOptionLabel";
        String optionSetMetadataId = "06d1a507-4d57-e911-a82a-000d3a1d5203";

        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n" +
                "\"AttributeLogicalName\": \"" + optionSetLogicalName + "\",\r\n" +
                "\"EntityLogicalName\": \"" + entityLogicalname + "\",\r\n" +
                "\"Value\": \"" + optionValue + "\",\r\n" +
                "\"Label\": {\r\n" +
                    "\"LocalizedLabels\": [\r\n" +
                        "{\r\n" +
                            "\"Label\": \"" + optionLabel + "\",\r\n" +
                            "\"LanguageCode\": 1033,\r\n" +
                            "\"IsManaged\": false,\r\n" +
                            "\"MetadataId\": \"881daca2-5c68-e911-a825-000d3a1d501d\",\r\n" +
                            "\"HasChanged\": null\r\n" +
                        "}\r\n" +
                    "],\r\n" +
                    "\"UserLocalizedLabel\": {\r\n" +
                        "\"Label\": \"" + optionLabel + "\",\r\n" +
                        "\"LanguageCode\": 1033,\r\n" +
                        "\"IsManaged\": false,\r\n" +
                        "\"MetadataId\": \"881daca2-5c68-e911-a825-000d3a1d501d\",\r\n" +
                        "\"HasChanged\": null\r\n" +
                    "}\r\n" +
                "},\r\n" +
                "\"Description\": {\r\n" +
                    "\"LocalizedLabels\": [\r\n" +
                        "{\r\n" +
                            "\"Label\": \"\",\r\n" +
                            "\"LanguageCode\": 1033,\r\n" +
                            "\"IsManaged\": false,\r\n" +
                            "\"MetadataId\": \"881daca2-5c68-e911-a825-000d3a1d501d\",\r\n" +
                            "\"HasChanged\": null\r\n" +
                        "}\r\n" +
                    "],\r\n" +
                    "\"UserLocalizedLabel\": {\r\n" +
                        "\"Label\": \"\",\r\n" +
                        "\"LanguageCode\": 1033,\r\n" +
                        "\"IsManaged\": false,\r\n" +
                        "\"MetadataId\": \"881daca2-5c68-e911-a825-000d3a1d501d\",\r\n" +
                        "\"HasChanged\": null\r\n" +
                    "}\r\n" +
                "}\r\n" +
            "}");

            Request request = new Request.Builder()
                    .url(REST_API_URL + "InsertOptionValue")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer "+ accessToken)
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println("End");
        }
        catch (IOException e) { }
    }

    // TODO: 2
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
    public static void readEntityAuditHistory(String accessToken) {
        class DynamicsEntityAudit {
            private String mChangeDate; // createdon
            private String mChangedBy; // _userid_value
            private String mEvent; // action
            private String mChangedField; // attributemask
            private String mOldValue;
            private String mNewValue;
            DynamicsEntityAudit(String changeDate, String changedBy, String event,
                String changedField, String oldValue, String newValue) {
                    mChangeDate = changeDate;
                    mChangedBy = changedBy;
                    mEvent = event;
                    mChangedField = changedField;
                    mOldValue = oldValue;
                    mNewValue = newValue;
            }
            public String getmChangeDate() {
                return mChangeDate;
            }
            public String getmChangedBy() {
                return mChangedBy;
            }
            public String getmEvent() {
                return mEvent;
            }
            public String getmChangedField() {
                return mChangedField;
            }
            public String getmOldValue() {
                return mOldValue;
            }
            public String getmNewValue() {
                return mNewValue;
            }
        }

        String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";

        List<DynamicsEntityAudit> dynamicsEntityAudits = new ArrayList<DynamicsEntityAudit>();

//        try {
//            System.out.println("end");
//        }
//        catch (IOException e) {}
    }

    // TODO: 4
    public static void getAssociatedAccountAddresses(String accessToken) {
        try {
            OkHttpClient client = new OkHttpClient();

            String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";

            Request request = new Request.Builder()
                    .url(REST_API_URL + "accounts%28" + accountId + "%29/Account_CustomerAddress")
                    .get()
                    .addHeader("OData-MaxVersion", "4.0")
                    .addHeader("OData-Version", "4.0")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();
            String dataReturnedFromGetAddresses = response.body().string();
            System.out.println("end");
        }
        catch (IOException e) {}
    }

    // TODO: 5
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

    // TODO: 6
    public static void bacthAccountPost(String accessToken) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("multipart/mixed;boundary=changeset_BBB456");
            RequestBody body = RequestBody.create(mediaType, "--batch_AAA123\r\nContent-Type: multipart/mixed;boundary=changeset_BBB456\r\n\r\n--changeset_BBB456\r\nContent-Type: application/http\r\nContent-Transfer-Encoding:binary\r\nContent-ID: 1\r\n\r\nPOST https://msott.api.crm.dynamics.com/api/data/v9.0/accounts HTTP/1.1\r\nContent-Type: application/json;type=entry\r\n\r\n{\"name\":\"batchgggg\"}\r\n\r\n--changeset_BBB456\r\nContent-Type: application/http\r\nContent-Transfer-Encoding:binary\r\nContent-ID: 2\r\n\r\nPOST https://msott.api.crm.dynamics.com/api/data/v9.0/accounts HTTP/1.1\r\nContent-Type: application/json;type=entry\r\n\r\n{\"name\":\"batchggggg49\"}\r\n\r\n--changeset_BBB456\r\nContent-Type: application/http\r\nContent-Transfer-Encoding:binary\r\nContent-ID: 3\r\n\r\nPOST https://msott.api.crm.dynamics.com/api/data/v9.0/accounts HTTP/1.1\r\nContent-Type: application/json;type=entry\r\n\r\n{\"name\":\"batch9gggg469\"}\r\n\r\n--changeset_BBB456--\r\n--batch_AAA123--");
            Request request = new Request.Builder()
                    .url("https://msott.api.crm.dynamics.com/api/data/v9.0/$batch")
                    .post(body)
                    .addHeader("Content-Type", "multipart/mixed;boundary=changeset_BBB456")
                    .addHeader("Accept", "application/json")
                    .addHeader("OData-MaxVersion", "4.0")
                    .addHeader("OData-Version", "4.0")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println("end");
        }
        catch (IOException e) {}
    }
}
