export const environment = {
  production: true,
  basePath:'http://localhost:8087/zoy-admin/v1/',
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
