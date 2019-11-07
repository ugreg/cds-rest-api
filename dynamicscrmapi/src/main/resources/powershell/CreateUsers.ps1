<#
.NOTES
Ensure the MSOnline cmdlet is installed
https://docs.microsoft.com/en-us/powershell/azure/active-directory/install-msonlinev1?view=azureadps-1.0

If you run into a digitally signed error run this command first
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
#>

Import-Module Microsoft.PowerShell.Utility
Install-Module MSOnline

Connect-MsolService

# $UserCredential = Get-Credential
# Get-MsolAccountSku
# Connect-MsolService -Credential $UserCredential

function CreateUsers
{
    $Organization = "ciadsf"
    $DomainName = $Organization + ".onmicrosoft.com"
    $License = $Organization + ":POWERFLOW_P2"

    $EnvironmentLimit = 5
    $firstname
    Write-Host "Licence Plan " $License
    for ($i = 0; $i -lt $EnvironmentLimit; $i++) {
        $displayname =  + " " + $lastname
        $email = ("User" + $1.ToString() + "@" + $DomainName).ToLower()
        Write-Host "creating user " $displayname "with email " $email
        New-MsolUser -DisplayName $displayname -FirstName $firstname -LastName $lastname -UserPrincipalName $email -UsageLocation US -LicenseAssignment $License -Password "pass@word1" -PasswordNeverExpires $true -ForceChangePassword $false
    }
}
CreateUsers