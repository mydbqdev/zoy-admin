export const environment = {
  production: true,
  basePath:'http://admin.qa.zoypg.com/api-admin/',
  version:'@Version',
  oDataBlockSize:100,
  defMenuEnable:{
    "ownerManagement":true,
    "ownerManagementSubMenu":{
      "ownerOnboardingAndRegistration":true,
      "ownerEKYCVerification":true,
      "managingOwners":true
    },
    "userManagement":true,
    "financialManagement":true,
    "configurationSettings":true,
    "configurationSettingsSubMenu":{
      "cancellationAndRefundRules":true,
      "percentageAndChargeConfigurations":true,
      "promotionAndOffersManagement":true
    }
  }
};
