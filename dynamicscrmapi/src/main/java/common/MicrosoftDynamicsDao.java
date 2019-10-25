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
     * https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions(06d1a507-4d57-e911-a82a-000d3a1d5203)/Microsoft.Dynamics.CRM.OptionSetMetadata?$select=Options
     */
    public void addGlobalOptionSetValuesDynamically()
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
                    getClass().getClassLoader().getResource("optionset.json").getFile()
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

    public void addLocalOptionSetValuesDynamically()
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
}
