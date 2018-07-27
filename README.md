# Dynamics REST APIs in Java
Dynamics 365 REST API in Java.

# Deployment
You will need the [mvn CLI](https://maven.apache.org/install.html).
Complete the 5 setup steps using the [Java Web App using ADAL](https://github.com/Azure-Samples/active-directory-java-webapp-openidconnect) tutorial.
These last two steps are the most important.

### Step 4:  Configure the sample to use your Azure AD tenant

Open `web.xml` in the webapp/WEB-INF/ folder. Fill in with your tenant and app registration information noted in registration step. Replace 'YOUR_TENANT_NAME' with the tenant domain name, 'YOUR_CLIENT_ID' with the Application Id and 'YOUR_CLIENT_SECRET' with the key value noted.

### Step 5: Package and then deploy the adal4jsample.war file.

- `$ mvn compile -DgroupId='com.microsoft.azure' -DartifactId=adal4jsample -DinteractiveMode=false`


- `$ mvn package`

This will generate a `adal4jsample.war` file in your /targets directory. Deploy this war file using Tomcat or any other J2EE container solution. To deploy on Tomcat container, copy the .war file to the webapps folder under your Tomcat installation and then start the Tomcat server.

This WAR will automatically be hosted at `http://<yourserverhost>:<yourserverport>/adal4jsample/`

Example: `http://localhost:8080/adal4jsample/`

### You're done!

Click on "Show users in the tenant" to start the process of logging in.

# References
- [Deploy War using Docker](https://www.youtube.com/watch?v=yOudtpXDPzw)
- [Icons](https://graphicburger.com/200-windows-10-icons/)
- [Java Web App using ADAL](https://github.com/Azure-Samples/active-directory-java-webapp-openidconnect)
