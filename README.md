<p align="center"><img src="img/red.png"></p>

| <img src="img/poke.svg" height="16"> [![License: MIT](https://img.shields.io/badge/License-MIT-grey.svg)](https://opensource.org/licenses/MIT) | <img src="img/poke.svg" height="16"> [![Build Status](https://travis-ci.org/gregdegruy/dynamics-cds-api.svg?branch=master)](https://travis-ci.org/gregdegruy/dynamics-cds-api) |
| :- | :- |

# Common Data Service (CDS) REST APIs in Java

CDS / Dynamics 365 REST API in Java. Data Access Object controls interactions with the server. Run the JUnit test to verify.

# Setup

Create an [Application user in Dynamics 365](https://docs.microsoft.com/en-us/power-platform/admin/create-users-assign-online-security-roles#create-an-application-user).
Create an Azure AD Application accounts in any organizational directory (Any Azure AD directory - meaning Multitenant). Give permissions Microsoft Graph `User.Read` & Dynamics CRM `user_impersonation` to the app. Generate a client secret.

# [Submodules](https://stackoverflow.com/a/7813286/5266970)
AAD permissions features depends on the [Java Web application that signs in users with the Microsoft identity platform and calls Microsoft Graph](https://github.com/Azure-Samples/ms-identity-java-webapp)

To use you'll need to do:
```
git submodule init
git submodule update
```

When you want to update to a newer version cd into the submodule and pull:
```
cd lib/my_module
git pull
```

Facing issues? Ensure `Project Settings > Modules` includes main class in the source folders and other content roots for the module.
