<div align="center"><img src="img/pupadoo.svg"></div>

# Dynamics REST APIs in Java

Dynamics 365 REST API in Java. Data Access Object controls interactions with the server, run the JUnit test to verify.

# Setup

Create an [Application user in Dynamics 365](https://docs.microsoft.com/en-us/power-platform/admin/create-users-assign-online-security-roles#create-an-application-user) for Multitenant Account type.
Create an Azure AD Application, with AAD Graph `User.Read` & Dynamics CRM `user_impersonation`, and generate a client secret.
