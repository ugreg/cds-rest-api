<div align="center"><img src="img/pupadoo.svg"></div>

[![Build Status](https://travis-ci.org/gregdegruy/dynamics-rest-api.svg?branch=master)](https://travis-ci.org/gregdegruy/dynamics-rest-api)

# Dynamics REST APIs in Java

Dynamics 365 REST API in Java. Data Access Object controls interactions with the server,. Run the JUnit test to verify.

# Setup

Create an [Application user in Dynamics 365](https://docs.microsoft.com/en-us/power-platform/admin/create-users-assign-online-security-roles#create-an-application-user).
Create an Azure AD Application accounts in any organizational directory (Any Azure AD directory - meaning Multitenant). Give permissions Microsoft Graph `User.Read` & Dynamics CRM `user_impersonation` to the app. Generate a client secret.

# Submodules
AAD permissions features depends on the [Java Web application that signs in users with the Microsoft identity platform and calls Microsoft Graph
](https://github.com/Azure-Samples/ms-identity-java-webapp)
