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
}
