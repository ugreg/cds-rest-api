# Dynamics REST APIs in Java
Dynamics 365 REST API in Java.

# Azure AD Setup Required
## Multi Tenant Auth

# Deployment
You will need the [mvn CLI](https://maven.apache.org/install.html).
Complete the 5 setup steps using the [Java Web App using ADAL](https://github.com/Azure-Samples/active-directory-java-webapp-openidconnect) tutorial.


> IMPORTANT
>
> Make sure you select "Grant Permission" for your app at https://portal.azure.com.

These last two steps are also important.

### Step 4:  Configure the sample to use your Azure AD tenant

Open `web.xml` in the webapp/WEB-INF/ folder. Fill in with your tenant and app registration information noted in registration step. Replace 'YOUR_TENANT_NAME' with the tenant domain name, 'YOUR_CLIENT_ID' with the Application Id and 'YOUR_CLIENT_SECRET' with the key value noted.

### Step 5: Package and then deploy the adal4jsample.war file.

- `$ mvn compile -DgroupId='com.microsoft.azure' -DartifactId=adal4jsample -DinteractiveMode=false`


- `$ mvn package`

This will generate a `adal4jsample.war` file in your /targets directory. Deploy this [Deploy .war using Docker](https://www.youtube.com/watch?v=yOudtpXDPzw).

```bash
// docker build -f <DOCKER FILE NAME> -t <DOCKER IMAGE NAME> .
docker build -f Dockerfile -t adal4jsample.war .
docker images
docker run -p 8080:8080 -t adal4jsample
```

### You're done!

Click on "Show users in the tenant" to start the process of logging in.

# References
- [Deploy War using Docker](https://www.youtube.com/watch?v=yOudtpXDPzw)
- [Icons](https://graphicburger.com/200-windows-10-icons/)
- [Java Web App using ADAL](https://github.com/Azure-Samples/active-directory-java-webapp-openidconnect)
