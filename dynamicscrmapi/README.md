// TODO: 1
    // Add Option set values dynamically using OData Web API.
    // GET all GlobalOptionSetDefinitions and find the MetadataId of the one you
    // want to insert an option into
    // https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions
    // You can use the MetadataId to see all current options for your option set
    // https://msott.api.crm.dynamics.com/api/data/v9.0/GlobalOptionSetDefinitions(06d1a507-4d57-e911-a82a-000d3a1d5203)/Microsoft.Dynamics.CRM.OptionSetMetadata/Options
    // if some API calls work and others don't, most likely the user does not have the right permissions
    // SecLib::CheckPrivilege failed. User: b4cd9bac-8851-e911-a825-000d3a1d5de8,
    // PrivilegeName: prvWriteOptionSet, PrivilegeId: 2493b394-f9d7-4604-a6cb-13e1f240450d,
    // Required Depth: Basic, BusinessUnitId: 1abfdddc-8140-e911-a823-000d3a1a25b8,
    // MetadataCache Privileges Count: 2999, User Privileges Count: 682

        // TODO: 2
    // Create custom party list using OOTB Party List (Field to select Multiple lookup values):
    // Functional Scenario:
    // We have Account entity and Contact entity.
    // We need to select multiple Contacts for single Account.
    // Like Email Entity To, BCC, CC fields.
    // https://docs.microsoft.com/en-us/dynamics365/customer-engagement/web-api/activityparty?view=dynamics-ce-odata-9
    // participationtypemask defines the role on the email
    // https://community.dynamics.com/crm/f/117/t/174608

    // TODO: 3
    // Retrieve Audit history data using OData Web API.
    // This URL /api/data/v9.1/audits This provides the overall summary. But we need the individual
    // Settings > System > Auditing turn it on
    // Make sure user has the proper audit security roles Core Records > Miscellaneous Privileges
    // View Audit History - View Audit Summary - View Audit Partitions - Delete Audit Partitions

    // TODO: 4
    // Associate:
    // How to avoid multiple OData Web API service call?
    // Functional Scenario:
    // - We have Account entity and Address entity.
    // - We need to associate multiple address (more than 2 address) to a particular Account record -  guide here https://stackoverflow.com/questions/47967916/dynamics-crm-add-multiple-address-in-account-entity
    // - We have created N:1 relationship between Address entity and Account entity. -  do not need to do this through system customization, just need to follow this guide, create the subgrid, and get all the addresses using this endpoint https://msott.api.crm.dynamics.com/api/data/v9.0/accounts(da084227-2f4b-e911-a830-000d3a1d5a4d)/Account_CustomerAddress

    // TODO: 5
    // Retrieving customized responses on POST method:

    // TODO: 6
    // Given permissions Custom Entities Batch Job

