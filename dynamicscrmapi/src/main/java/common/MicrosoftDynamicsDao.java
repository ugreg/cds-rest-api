package common;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MicrosoftDynamicsDao {

    private static MicrosoftDynamicsDao microsoftDynamicsDaoInstance = null;

    private static final String AUTHORITY = "https://login.microsoftonline.com/";
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

    private void authenticate() {
        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            AuthenticationResult result;
            AuthenticationContext context = new AuthenticationContext(AUTHORITY + tenantID, true, service);
            Future<AuthenticationResult> future = context.acquireToken(RESOURCE, new ClientCredential(clientId, clientSecret), null);

            result = future.get();
            accessToken = result.getAccessToken();
        }
        catch (MalformedURLException e) {
            System.out.println("MalformedURLException ");
        }
        catch (InterruptedException e) {
            System.out.println("InterruptedException ");
        }
        catch (ExecutionException e) {
            System.out.println("ExecutionException, have you provided the correct client id and secret pair? ");
        }
    }

    /**
     * View all Global Option Sets
     * Should work on v9.1 needs testing
     * https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions(06d1a507-4d57-e911-a82a-000d3a1d5203)/Microsoft.Dynamics.CRM.OptionSetMetadata?$select=Options
     */
    public void postGlobalOptionSetValuesDynamically()
            throws MalformedURLException, InterruptedException, ExecutionException {
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
            File file = new File(
                    getClass().getClassLoader().getResource("global-optionset.json").getFile()
            );
            JSONTokener jt = new JSONTokener(new FileReader(file.getPath()));
            JSONObject jo = new JSONObject(jt);
            jo.put("OptionSetName", optionSetName);
            jo.put("Value", value);
            jo.getJSONObject("Label").getJSONArray("LocalizedLabels").getJSONObject(0).put("Label", label);
            jo.getJSONObject("Label").getJSONArray("LocalizedLabels").getJSONObject(0).put("MetadataId", metadataId);
            jo.getJSONObject("Label").getJSONObject("UserLocalizedLabel").put("Label", label);
            jo.getJSONObject("Label").getJSONObject("UserLocalizedLabel").put("MetadataId", metadataId);
            jo.getJSONObject("Description").getJSONArray("LocalizedLabels").getJSONObject(0).put("MetadataId", metadataId);
            jo.getJSONObject("Description").getJSONObject("UserLocalizedLabel").put("Label", label);
            jo.getJSONObject("Description").getJSONObject("UserLocalizedLabel").put("MetadataId", metadataId);

            String content = jo.toString();

            RequestBody body = RequestBody.create(mediaType, content);
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

    /**
     * View this optionset
     * https://msott.crm.dynamics.com/api/data/v9.1/EntityDefinitions(LogicalName='cr965_testcdsentity')/Attributes/Microsoft.Dynamics.CRM.PicklistAttributeMetadata?$select=LogicalName&$filter=LogicalName%20eq%20%27new_localoptionsettoform%27&$expand=OptionSet
     * POST value to an option set field of an entity in a Solution
     */
    public void postLocalOptionSetValuesDynamically()
            throws MalformedURLException, InterruptedException, ExecutionException {
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
            JSONArray optionsArray =  odataResponse
                    .getJSONArray("value")
                    .getJSONObject(0)
                    .getJSONObject("OptionSet")
                    .getJSONArray("Options");
            previousValue = optionsArray
                    .getJSONObject(optionsArray.length() - 1)
                    .getInt("Value");
        }
        catch (IOException e) { }

        String optionValue = Integer.toString(++previousValue);
        String optionLabel = "SuperNewOption";

        try {
            OkHttpClient client = new OkHttpClient();

            File file = new File(
                    getClass().getClassLoader().getResource("local-optionset.json").getFile()
            );
            JSONTokener jt = new JSONTokener(new FileReader(file.getPath()));
            JSONObject jo = new JSONObject(jt);
            jo.put("AttributeLogicalName", optionSetLogicalName);
            jo.put("EntityLogicalName", entityLogicalname);
            jo.put("Value", optionValue);
            jo.getJSONObject("Label").getJSONArray("LocalizedLabels").getJSONObject(0).put("Label", optionLabel);
            jo.getJSONObject("Label").getJSONObject("UserLocalizedLabel").put("Label", optionLabel);

            String content = jo.toString();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, content);

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

    //    // TODO: 2
//    public static void postEmailWithPartyList(String accessToken) {
//        try {
//            OkHttpClient client = new OkHttpClient();
//
//            String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";
//
//            Request request = new Request.Builder()
//                    .url(REST_API_URL + "accounts" +
//                            "%28" + accountId + "%29/contact_customer_accounts")
//                    .get()
//                    .addHeader("Authorization", "Bearer " + accessToken)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            String dataReturnedFromGetOptions = response.body().string();
//
//            JSONObject json = new JSONObject(dataReturnedFromGetOptions);
//            JSONArray jsonArray = (JSONArray) json.get("value");
//            Queue<String> contactIds = new LinkedList<String>();
//            for(int i=0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                contactIds.add(jsonObject.getString("contactid"));
//            }
//
//            final int SENDER_PARTICIPATION_TYPE_MASK = 1;
//            final int TO_PARTICIPATION_TYPE_MASK = 2;
//            final int CC_PARTICIPATION_TYPE_MASK = 3;
//            final int BCC_PARTICIPATION_TYPE_MASK = 4;
//            String senderId = "96b856f4-134c-e911-a823-000d3a1d5de8";
//            MediaType mediaType = MediaType.parse("application/json");
//            String contacts = "";
//            for(String id : contactIds){
//                contacts += ",{\"partyid_contact@odata.bind\": \"/contacts(" + id + ")\"," +
//                        "\"participationtypemask\": " + TO_PARTICIPATION_TYPE_MASK + "}";
//            }
//            String requestBodyContent = "{" +
//                        "\"email_activity_parties\": " +
//                        "[" +
//                            "{" +
//                                "\"partyid_systemuser@odata.bind\": \"/systemusers(" + senderId +")\"," +
//                                "\"participationtypemask\": " + SENDER_PARTICIPATION_TYPE_MASK +
//                            "}" + contacts +
//                        "]" +
//                    "}";
//            RequestBody body = RequestBody.create(mediaType, requestBodyContent);
//            request = new Request.Builder()
//                    .url(REST_API_URL + "emails")
//                    .post(body)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Authorization", "Bearer " + accessToken)
//                    .build();
//
//            response = client.newCall(request).execute();
//
//            System.out.println();
//        }
//        catch(IOException e) {}
//    }

//    // TODO: 3
//    public static void getEntityAuditHistory(String accessToken) {
//        class DynamicsEntityAudit {
//            private String mChangeDate; // createdon
//            private String mChangedBy; // _userid_value
//            private String mEvent; // action
//            private String mChangedField; // attributemask
//            private String mOldValue;
//            private String mNewValue;
//            DynamicsEntityAudit(String changeDate, String changedBy, String event,
//                String changedField, String oldValue, String newValue) {
//                    mChangeDate = changeDate;
//                    mChangedBy = changedBy;
//                    mEvent = event;
//                    mChangedField = changedField;
//                    mOldValue = oldValue;
//                    mNewValue = newValue;
//            }
//            public String getmChangeDate() {
//                return mChangeDate;
//            }
//            public String getmChangedBy() {
//                return mChangedBy;
//            }
//            public String getmEvent() {
//                return mEvent;
//            }
//            public String getmChangedField() {
//                return mChangedField;
//            }
//            public String getmOldValue() {
//                return mOldValue;
//            }
//            public String getmNewValue() {
//                return mNewValue;
//            }
//        }
//
//        String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";
//
//        List<DynamicsEntityAudit> dynamicsEntityAudits = new ArrayList<DynamicsEntityAudit>();
//
////        try {
////            System.out.println("end");
////        }
////        catch (IOException e) {}
//    }

    /**
     * https://msott.api.crm.dynamics.com/api/data/v9.0/accounts(da084227-2f4b-e911-a830-000d3a1d5a4d)/Account_CustomerAddress
     */
    public void getAssociatedAccountAddresses() throws MalformedURLException, InterruptedException, ExecutionException {
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

//    // TODO: 5
//    public static void createWithDataReturned(String accessToken) {
//        try {
//            OkHttpClient client = new OkHttpClient();
//
//            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//            RequestBody body = RequestBody.create(mediaType, "{" +
//                    "\"name\": \"Sample Postman Account\"," +
//                    "\"creditonhold\": false," +
//                    "\"address1_latitude\": 47.639583," +
//                    "\"description\": \"This is the description of the sample account\"," +
//                    "\"revenue\": 5000000," +
//                    "\"accountcategorycode\": 1" +
//                    "}");
//            Request request = new Request.Builder()
//                    .url("https://msott.api.crm.dynamics.com/api/data/v9.0/accounts" +
//                            "?$select=name,creditonhold,address1_latitude,description,revenue,accountcategorycode,createdon")
//                    .post(body)
//                    .addHeader("OData-MaxVersion", "4.0")
//                    .addHeader("OData-Version", "4.0")
//                    .addHeader("Accept", "application/json")
//                    .addHeader("Content-Type", "application/json; charset=utf-8")
//                    .addHeader("Prefer", "return=representation")
//                    .addHeader("Authorization", "Bearer " + accessToken)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//
//            String dataReturnedFromCreate = response.body().string();
//
//            System.out.println(dataReturnedFromCreate);
//        }
//        catch (IOException e) { }
//    }

    public void postAccountBatch() throws MalformedURLException, InterruptedException, ExecutionException {
        try {
            OkHttpClient client = new OkHttpClient();

            File f = new File(
                    getClass().getClassLoader().getResource("batch.txt").getFile()
            );

            FileReader fr = new FileReader(f);
            char[] letters = new char[(int)f.length()];
            fr.read(letters);
            fr.close();
            String content = new String(letters);
            String changeSetType = "multipart/mixed;boundary=changeset_BBB456";
            MediaType mediaType = MediaType.parse(changeSetType);
            RequestBody body = RequestBody.create(mediaType, content);
            Request request = new Request.Builder()
                    .url(REST_API_URL + "$batch")
                    .post(body)
                    .addHeader("Content-Type", changeSetType)
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
