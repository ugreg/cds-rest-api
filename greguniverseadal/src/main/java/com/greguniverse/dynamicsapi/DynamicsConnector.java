package com.greguniverse.dynamicsapi;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class DynamicsConnector {

    public MicrosoftAuth microsoftAuth;
    private String _accessToken;

    public DynamicsConnector() {

    }

    public void createWebhook() {
        String uri = "https://orgname.crm.dynamics.com/api/data/v9.0/serviceendpoints";
        /*
        POST
            {
             "messageformat": 2, //2 - json
             "namespaceformat": 1, // 1- Namespace Address
             "path": " ", // can leave as empty space string
             "userclaim": 1, //1 - None
             "authtype": 4, // 4 - is value for Webhook Key
             "contract": 8, // 8 - is value for Webhook
             "url": "http://msgainsight.proxy.beeceptor.com", // mock proxy api, replace it with your own
             "name": "MS Gainsight Test Event Listener",
             "authvalue": "none"
             }
        */

        createSdkMessageProcessingStep();
    }

    /**
     * @MicrosoftDocs
     * https://docs.microsoft.com/en-us/dynamics365/customerengagement/developer/entities/sdkmessageprocessingstep
     */
    private void createSdkMessageProcessingStep() {
        String url = "https://orgname.crm.dynamics.com/api/data/v9.0/serviceendpoints";

        String webhookId = "";
        String serviceendpointId = webhookId;

        String sdkmessagefilterId = getSdkMessageFilterId();
        String sdkmessageId = getSdkMessageId();

        /*
         POST
         {
             "configuration":null,
             "asyncautodelete": true,
             "canusereadonlyconnection": false,
             "description": "OCP Wkshp Bee: Create of Account",
             "eventhandler_serviceendpoint@odata.bind": "/serviceendpoints(09cdfe1c-d074-e811-a957-000d3a1d709f)",
             "mode": 1,
             "rank": 1,
             "filteringattributes": null,
             "name": "MS Gainsight Test Event Listener: Create of Account",

             "sdkmessagefilterid@odata.bind": "/sdkmessagefilters(c2c5bb1b-ea3e-db11-86a7-000a3a5473e8)",

             "sdkmessageid@odata.bind": "/sdkmessages(9ebdbb1b-ea3e-db11-86a7-000a3a5473e8)",

             "stage": 40, //40 - post-operation
             "statecode": 0, //0 - enabled
             "statuscode": 1, //1 - enabled
             "supporteddeployment": 0 // 0 - Server only
         }
         */

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, url);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + this._accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println("Something went wrong with the ok http call :(");
            e.printStackTrace();
        }

        System.out.println("Got accounts " + response.toString());
    }
    private String getSdkMessageId() {
        // create message id
        String url = "https://orgname.crm.dynamics.com/api/data/v9.0/sdkmessages?$filter=name eq" +
                "'create'&$select=sdkmessageid";

        return "";
    }
    private String getSdkMessageFilterId() {
        // create message id of account
        String sdkMessageId = getSdkMessageId();
        String url = "https://orgname.crm.dynamics.com/api/data/v9.0/sdkmessagefilters?$select=primaryobjecttypecode" +
                ",sdkmessagefilterid,_sdkmessageid_value&$filter=primaryobjecttypecode eq 'account' and" +
                "_sdkmessageid_value eq '" + sdkMessageId + "'";

        return "";
    }

    public void createEntityLink() {
        String url = "http://mssmartsheet.crm.dynamics.com/main.aspx?etn=account&pagetype=entityrecord&id=%7B7711aed2-a175-e811-a85e-000d3a1bea77%7D";
    }

    public void doSomeCoolPagination() {
        /*
        {
            "@odata.context": "https://mssmartsheet.crm.dynamics.com/api/data/v9.0/$metadata#accounts",
            "value": [
            {data here...},
            {data here...}
            ],
            "@odata.nextLink": "https://mssmartsheet.crm.dynamics.com/api/data/v9.0/accounts?$skiptoken=%3Ccookie%20pagenumber=%222%22%20pagingcookie=%22%253ccookie%2520page%253d%25221%2522%253e%253caccountid%2520last%253d%2522%257b6D11AED2-A175-E811-A85E-000D3A1BEA77%257d%2522%2520first%253d%2522%257b0D6F25B4-9B78-E811-A83E-000D3A13A9C6%257d%2522%2520%252f%253e%253c%252fcookie%253e%22%20istracking=%22False%22%20/%3E"
        }
         */
    }
}
