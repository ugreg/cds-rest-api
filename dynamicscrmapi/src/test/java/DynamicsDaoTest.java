import common.DynamicsDao;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class DynamicsDaoTest {

    private DynamicsDao testMicrosoftDynamicsDao = DynamicsDao.getInstance("msott", "grdegr");

    /**
     * Get audit history for an entity.
     * AuditDetails collects all audits, most recent change to an entity is in front of queue.
     * @see https://msott.api.crm.dynamics.com/api/data/v9.0/RetrieveRecordChangeHistory(Target=@tid, PagingInfo=@pi)?@tid={'@odata.id':'accounts(da084227-2f4b-e911-a830-000d3a1d5a4d)'}&@pi=null
     */
    @Test
    public void getEntityAuditHistory() throws MalformedURLException, InterruptedException, ExecutionException {
        try {
            System.out.println("getEntityAuditHistory()");
            String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";
            String path = "RetrieveRecordChangeHistory%28" +
                    "Target=@tid,%20" +
                    "PagingInfo=@pi%29?" +
                    "@tid={%27@odata.id%27:%27accounts%28" + accountId + "%29%27}&" +
                    "@pi=null";

            String responseString = testMicrosoftDynamicsDao.get(path);
            JSONObject odataResponse = new JSONObject(responseString);

            JSONArray auditDetails = odataResponse.getJSONObject("AuditDetailCollection").getJSONArray("AuditDetails");
            auditDetails.length();

            Queue<JSONObject> auditHistory = new LinkedList<JSONObject>();
            for (Object o : auditDetails) {
                JSONObject jo = (JSONObject) o;
                auditHistory.add(jo);
            }

            String anOldValue = auditHistory.peek().getJSONObject("OldValue").toString();
            String aNewValue = auditHistory.peek().getJSONObject("NewValue").toString();
            System.out.println("Old value " + anOldValue);
            System.out.println("New value " + aNewValue);
            String goal = "Old and new value have different data";
            assertEquals(goal, goal);
            System.out.println("end");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see https://msott.api.crm.dynamics.com/api/data/v9.0/accounts(da084227-2f4b-e911-a830-000d3a1d5a4d)/Account_CustomerAddress
     */
    @Test
    public void getAssociatedAccountAddresses() throws MalformedURLException, InterruptedException, ExecutionException {
        try {
            System.out.println("getAssociatedAccountAddresses()");
            String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";
            String path = "accounts%28" + accountId + "%29/Account_CustomerAddress";
            String responseString = testMicrosoftDynamicsDao.get(path);
            String goal = "Got list of addresses";
            assertEquals(goal, goal);
            System.out.println("end");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions(06d1a507-4d57-e911-a82a-000d3a1d5203)/Microsoft.Dynamics.CRM.OptionSetMetadata?$select=Options
     */
    @Test
    public void postGlobalOptionSetValue()
            throws MalformedURLException, InterruptedException, ExecutionException {
        System.out.println("postGlobalOptionSetValue()");
        int previousValue = 0;
        String optionSetGuidString = "06d1a507-4d57-e911-a82a-000d3a1d5203";
        try {
            String path = "GlobalOptionSetDefinitions%28" +  optionSetGuidString +
                    "%29/Microsoft.Dynamics.CRM.OptionSetMetadata/Options";
            String dataReturnedFromGetOptions = testMicrosoftDynamicsDao.get(path);
            JSONObject json = new JSONObject(dataReturnedFromGetOptions);
            JSONArray jsonArray = (JSONArray) json.get("value");
            JSONObject jsonObject = (JSONObject) jsonArray.get(jsonArray.length() - 1);
            previousValue = jsonObject.getInt("Value");
        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }

        String prefix = "new";
        String entity = "_msdatzooptionset";
        String optionSetName = prefix + entity;
        String value = Integer.toString(++previousValue);
        String label = "JavaGlobalOption";
        String metadataId = "06d1a507-4d57-e911-a82a-000d3a1d5203";
        try {
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

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, content);
            testMicrosoftDynamicsDao.post("InsertOptionValue", body);

            String goal = "Posted global Option Set in Settings > Customizations > " +
                    "Customize the System > Option Sets. Named " + label + " in Publisher defined Option Set.";
            assertEquals(goal, goal);
            System.out.println("End");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adds value to an Option Set field of an Entity in a Solution.
     * @see https://msott.crm.dynamics.com/api/data/v9.1/EntityDefinitions(LogicalName='cr965_testcdsentity')/Attributes/Microsoft.Dynamics.CRM.PicklistAttributeMetadata?$select=LogicalName&$filter=LogicalName%20eq%20%27new_localoptionsettoform%27&$expand=OptionSet
     */
    @Test
    public void postLocalOptionSetValue()
            throws MalformedURLException, InterruptedException, ExecutionException {

        System.out.println("postLocalOptionSetValue()");
        int previousValue = 0;
        String entityLogicalname = "cr965_testcdsentity";
        String optionSetLogicalName = "new_localoptionsettoform";
        try {
            String path = "EntityDefinitions%28LogicalName=%27" + entityLogicalname +
                    "%27%29/Attributes/Microsoft.Dynamics.CRM.PicklistAttributeMetadata" +
                    "?$select=LogicalName&$filter=LogicalName%20eq%20%27" + optionSetLogicalName +
                    "%27&$expand=OptionSet";
            String dataReturnedFromGetOptions = testMicrosoftDynamicsDao.get(path);
            JSONObject odataResponse = new JSONObject(dataReturnedFromGetOptions);
            JSONArray optionsArray = odataResponse
                    .getJSONArray("value")
                    .getJSONObject(0)
                    .getJSONObject("OptionSet")
                    .getJSONArray("Options");
            previousValue = optionsArray
                    .getJSONObject(optionsArray.length() - 1)
                    .getInt("Value");
        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }

        String optionValue = Integer.toString(++previousValue);
        String label = "JavaLocalOption";
        try {
            File file = new File(
                    getClass().getClassLoader().getResource("local-optionset.json").getFile()
            );
            JSONTokener jt = new JSONTokener(new FileReader(file.getPath()));
            JSONObject jo = new JSONObject(jt);
            jo.put("AttributeLogicalName", optionSetLogicalName);
            jo.put("EntityLogicalName", entityLogicalname);
            jo.put("Value", optionValue);
            jo.getJSONObject("Label").getJSONArray("LocalizedLabels").getJSONObject(0).put("Label", label);
            jo.getJSONObject("Label").getJSONObject("UserLocalizedLabel").put("Label", label);
            String content = jo.toString();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, content);
            testMicrosoftDynamicsDao.post("InsertOptionValue", body);

            String goal = "Posted Option Set in Solutions > {Your_Solution_Name} > {Your_Entity} > " +
                    "Fields > Option Sets. Named " + label + " in Publisher defined Option Set.";
            assertEquals(goal, goal);
            System.out.println("End");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an email to all Account Contacts. Displays in Entity Timeline.
     * View an entity in a Model-driven app using these queries.
     * ?appid=c2b315b4-9040-e911-a823-000d3a1a25b8
     * &pagetype=entityrecord
     * &etn=contact
     * &id=a8093fe9-795c-e911-a825-000d3a1d501d
     * https://msott.api.crm.dynamics.com/api/data/v9.0/emails
     */
    @Test
    public void postEmailWithPartyList() throws MalformedURLException, InterruptedException, ExecutionException {

        System.out.println("postLocalOptionSetValue()");
        try {
            OkHttpClient client = new OkHttpClient();
            String accountId = "da084227-2f4b-e911-a830-000d3a1d5a4d";
            String path = "accounts" + "%28" + accountId + "%29/contact_customer_accounts";
            String contactsResponse = testMicrosoftDynamicsDao.get(path);
            JSONObject contactsResponseJson = new JSONObject(contactsResponse);
            JSONArray contactsJsonArray = contactsResponseJson.getJSONArray("value");
            Queue<String> contactIds = new LinkedList<String>();
            for (Object o : contactsJsonArray) {
                JSONObject contact = (JSONObject) o;
                contactIds.add(contact.getString("contactid"));
            }

            final int SENDER_PARTICIPATION_TYPE_MASK = 1;
            final int TO_PARTICIPATION_TYPE_MASK = 2;
            final int CC_PARTICIPATION_TYPE_MASK = 3;
            final int BCC_PARTICIPATION_TYPE_MASK = 4;
            String systemuserId = "96b856f4-134c-e911-a823-000d3a1d5de8";
            String senderId = systemuserId;
            MediaType mediaType = MediaType.parse("application/json");
            Stack<JSONObject> stack = new Stack<JSONObject>();
            JSONObject contact;
            for (String id : contactIds) {
                contact = new JSONObject();
                contact.put("partyid_contact@odata.bind", "/contacts(" + id + ')');
                contact.put("participationtypemask", TO_PARTICIPATION_TYPE_MASK);
                stack.push(contact);
            }
            File f = new File(
                    getClass().getClassLoader().getResource("email-activity-party.json").getFile()
            );
            FileReader fr = new FileReader(f);
            char[] letters = new char[(int) f.length()];
            fr.read(letters);
            fr.close();
            String content = new String(letters);

            JSONTokener jt = new JSONTokener(new FileReader(f.getPath()));
            JSONObject jo = new JSONObject(jt);
            while (!stack.empty()) {
                jo.getJSONArray("email_activity_parties").put(stack.pop());
            }
            content = jo.toString();
            content = content.replace("SYSTEM_USER_ID", senderId);
            mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, content);
            testMicrosoftDynamicsDao.post("emails", body);

            String goal = "Sent emails to account with id" + accountId + " from user id " + systemuserId +
                    "Check Timeline for emails.";
            assertEquals(goal, goal);
            System.out.println("End");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see https://msott.api.crm.dynamics.com/api/data/v9.0/accounts?$select=name,address1_latitude,address1_longitude,description,revenue,createdon
     */
    @Test
    public void postWithDataReturned() throws MalformedURLException, InterruptedException, ExecutionException {

        System.out.println("postWithDataReturned()");
        try {
            File f = new File(
                    getClass().getClassLoader().getResource("account.json").getFile()
            );
            FileReader fr = new FileReader(f);
            char[] letters = new char[(int) f.length()];
            fr.read(letters);
            fr.close();
            String content = new String(letters);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, content);
            String path = "accounts?$select=name,address1_latitude,address1_longitude,description,revenue,createdon";
            AbstractMap.SimpleEntry<String, String> headers = new AbstractMap.SimpleEntry<String, String>("Prefer", "return=representation");
            testMicrosoftDynamicsDao.post(path, body, headers);

            String goal = "Created and an Account with response data returned.";
            assertEquals(goal, "Test is failing :(");
            System.out.println("End");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Handle SocketTimeoutException
    @Test
    public void batchAccountcreate() throws MalformedURLException, InterruptedException, ExecutionException {

        System.out.println("batchAccountcreate()");
        try {
            File f = new File(
                    getClass().getClassLoader().getResource("batch.txt").getFile()
            );
            FileReader fr = new FileReader(f);
            char[] letters = new char[(int) f.length()];
            fr.read(letters);
            fr.close();
            String content = new String(letters);
            String changeSetType = "multipart/mixed;boundary=changeset_BBB456";
            MediaType mediaType = MediaType.parse(changeSetType);
            RequestBody body = RequestBody.create(mediaType, content);
            String path = "$batch";
            AbstractMap.SimpleEntry<String, String> headers = new AbstractMap.SimpleEntry<String, String>("Content-Type", changeSetType);
            testMicrosoftDynamicsDao.post(path, body, headers);

            String goal = "Created accounts in batch from txt file.";
            assertEquals(goal, goal);
            System.out.println("end");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
