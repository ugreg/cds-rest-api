Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

if (!(Get-Module -ListAvailable -Name AzureAD)) {
    Install-Module -Name AzureAD    
} 
if (!(Get-Module -ListAvailable -Name Microsoft.PowerApps.Administration.PowerShell)) {    
    Install-Module -Name Microsoft.PowerApps.Administration.PowerShell
} 
if (!(Get-Module -ListAvailable -Name  Microsoft.PowerApps.PowerShell)) {    
    Install-Module -Name Microsoft.PowerApps.PowerShell -AllowClobber
} 
if (!(Get-Module -ListAvailable -Name Microsoft.Xrm.OnlineManagementAPI)) {
    Install-Module -Name Microsoft.Xrm.OnlineManagementAPI    
} 
if (!(Get-Module -ListAvailable -Name Microsoft.Xrm.Data.PowerShell)) {
    Install-Module -Name Microsoft.Xrm.Data.PowerShell    
} 
if (!(Get-Module -ListAvailable -Name MSOnline)) {
    Install-Module -Name MSOnline    
} 

$User = Read-Host -Prompt "Please enter username" # "admin@M365x529226.onmicrosoft.com"
$Password = Read-Host -Prompt "Please enter your password" -AsSecureString
$cred = New-Object System.Management.Automation.PSCredential ($User, $Password)
Get-Credential $cred
Connect-AzureAD -Credential $cred
Connect-MsolService -Credential $cred -AzureEnvironment AzureCloud

Write-Host "### You can use these functions ###" 
Write-Host ""
Write-Host "  Add-AdminInADay-Licenses" -ForegroundColor Green     
Write-Host "  Create-CrmInstance"  -ForegroundColor Green
Write-Host ""

function Add-AdminInADay-Licenses {
    $PowerAppsPerUserPlan = "POWERAPPS_PER_USER"
    $Office365E3 = "ENTERPRISEPACK"
    $Plans = $PowerAppsPerUserPlan, $Office365E3
    $License = New-Object -TypeName Microsoft.Open.AzureAD.Model.AssignedLicense
    for ($i=0; $i -lt $Plans.Count; $i++) {
        $planName = $Plans[$i]
        $License.SkuId = (Get-AzureADSubscribedSku | Where-Object -Property SkuPartNumber -Value $planName -EQ).SkuID    
        $LicensesToAssign = New-Object -TypeName Microsoft.Open.AzureAD.Model.AssignedLicenses
        $LicensesToAssign.AddLicenses = $License
        Set-AzureADUserLicense -ObjectId $User -AssignedLicenses $LicensesToAssign
    }
}

function Create-CrmInstance {
    $connectionhost = "https://admin.services.crm.dynamics.com"

    Get-CrmInstances -ApiUrl $connectionhost -Credential $cred

    $versions = Get-CrmServiceVersions -ApiUrl $connectionhost -Credential $cred  
    $v9dot0Guid = $versions | where Version -like "9.0"  
    $timestamp = Get-Date -Format "MMddyyyyHHmm"
    $domainname = "ps" + $timestamp.ToString()
    $timestamp = Get-Date -Format "MM//dd/yyyy HH:mm"
    $friendlyname = "PS - " + $timestamp.ToString()
    $instanceInfo = New-CrmInstanceInfo -BaseLanguage 1033 `
        -ServiceVersionId $v9dot0Guid.Id `
        -InstanceType Sandbox `
        -DomainName $domainname `
        -InitialUserEmail $User `
        -FriendlyName $friendlyname `
        -CurrencyCode 840  `
        -CurrencyName USD  `
        -CurrencyPrecision 2 `
        -CurrencySymbol $  `
        -Purpose "PowerShell created instance"

    $newCrmInstanceResponse = New-CrmInstance -ApiUrl $connectionhost -Credential $cred -NewInstanceInfo $instanceInfo
    $guid = $newCrmInstanceResponse.ResourceLocation
    $guid = $guid.Substring($guid.Length - 36, 36)
    $crmInstance = Get-CrmInstance -Id $guid -ApiUrl $connectionhost -Credential $cred
}

function Import-CrmSolution {
    $orgname = "org6283a48a"
    $conn = Get-CrmConnection -Credential $cred -DeploymentRegion NorthAmerica –OnlineType Office365 –OrganizationName $orgname
    $path = Get-Location
    $path = $path.ToString() + ".\Solutions\NorthwindTraders_1_0_0_6.zip"
    Import-CrmSolution -conn $conn -SolutionFilePath $path
    # TODO: Apply all customizations
}

function Change-InstanceType {
    $connectionhost = "https://admin.services.crm.dynamics.com"

    $instances = Get-CrmInstances -ApiUrl $connectionhost -Credential $cred
    $i = $instances | Where-Object {$_.FriendlyName -eq 'Thrive Hr - Test'}
    $i.Id
    # need logic to set to Sandbox instance
    $i = $instances | Where-Object {$_.FriendlyName -eq 'Thrive Hr - UAT'}
    $i.Id
    # need logic to set to Sandbox instance

    $versions = Get-CrmServiceVersions -ApiUrl $connectionhost -Credential $cred  
    $v9dot0Guid = $versions | where Version -like "9.0"
}
