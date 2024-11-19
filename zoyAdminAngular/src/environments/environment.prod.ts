export const environment = {
  production: true,
  basePath:'http://45.129.87.152:8082/api-admin/',
  version:'@Version',
  oDataBlockSize:100,
  defMenuEnable:{
    "owners":true,
    "ownersSubMenu":{
       "zoycode":true,
       "managingOwners":true,
  },
  "users":true,
    "usersSubMenu":{
      "managingUsers": true,
      "permissionApproval": true,
    },
    "tenants":true,
    "tenantsSubMenu":{
      "tenantzoy": true,
    },
    "finances":true,
    "financesSubMenu":{
      "reports":true,
    },
    "supports":true,
    "supportsSubMenu":{
      "tickets":true,
    },
    "settings":true,
    "settingsSubMenu":{
      "roleAndPermission":true,
      "cancellationAndRefundRules":true,
      "percentageAndChargeConfigurations":true,
      "promotionAndOffersManagement":true
      
    },

  
    
    // "userManagement":true,
    // "financialManagement":true,
    // "configurationSettings":true,
    // "configurationSettingsSubMenu":{
    
    // },
    //  "reports":true,
    //   "reportsSubMenu":{
    //      "reports":true
    // },

    // "ownerManagement":true,
    // "ownerManagementSubMenu":{
    //   "ownerOnboardingAndRegistration":true,
    //   "ownerEKYCVerification":true,
     
    // },
    
  }
};
