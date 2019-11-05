package common;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class MicrosoftDataAccessObject {

    protected static final String AUTHORITY = "https://login.microsoftonline.com/";

    protected String apiVersion = "v9.0";
    protected String tenantId;
    protected String resource;
    protected String restApiUrl;
    protected String clientId = "49ad43ec-5d60-4471-b111-99d473a1b515";
    protected String clientSecret = "";
    protected String accessToken = "";
    protected OkHttpClient okHttpClient;

    public void authenticate() {
        String method = "MicrosoftDataAccessObject.authenticate()";
        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            AuthenticationResult result;
            AuthenticationContext context
                    = new AuthenticationContext(AUTHORITY + tenantId, true, service);
            Future<AuthenticationResult> future
                    = context.acquireToken(this.resource, new ClientCredential(this.clientId, this.clientSecret), null);

            result = future.get();
            accessToken = result.getAccessToken();
        } catch (MalformedURLException e) {
            System.out.println(method + "  MalformedURLException " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(method + "  InterruptedException "  + e.getMessage());
        } catch (ExecutionException e) {
            System.out.println(method + "  ExecutionException, have you provided the " +
                    "correct client id and secret pair? " +
                    "Has your secret expired? " +
                    e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(method + "  IllegalArgumentException, have you provided " +
                    "the correct client id and secret pair? " +
                    "Has your secret expired? " +
                    e.getMessage());
        }
    }

    public String readFile(String fileName) {
        try {
            File f = new File(
                    getClass().getClassLoader().getResource(fileName).getFile()
            );
            FileReader fr = new FileReader(f);
            char[] letters = new char[(int) f.length()];
            fr.read(letters);
            fr.close();
            String content = new String(letters);
            return content;
        } catch (IOException e) {
            return "";
        }
    }
}
