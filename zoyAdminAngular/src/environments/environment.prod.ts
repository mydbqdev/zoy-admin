export const environment = {
  production: true,
  //basePath:'http://45.129.87.152:8082/api-admin/',
  basePath:'https://api-admin.zoypg.com',
  basePathExternal:'https://apis.zoypg.com/',
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
      "bulkUpload": true
    },
    "finances":true,
    "financesSubMenu":{
      "reports":true,
      "paymentApproval":true,
    },
    "supports":true,
    "supportsSubMenu":{
      "tickets":true,
    },
    "settings":true,
    "settingsSubMenu":{
      "roleAndPermission":true,
      "dbMasterConfiguration":true,
      "configurationMaster":true,
      "userAudit":true,
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
