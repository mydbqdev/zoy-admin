export const environment = {
  production: true,
  basePath:'http://45.129.87.152:8082/api-admin/',
  //basePath:'https://api-admin.zoypg.com/api-admin/',
  //websocketBasePath:'https://api-admin.zoypg.com/api-admin/',
  websocketBasePath:'http://45.129.87.152:8082/api-admin/',
  basePathExternal:'https://maps.googleapis.com/',
  //basePathExternal:'/api/',
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
      "configurationMasterApproval":true,
      "userAudit":true,
      "cancellationAndRefundRules":true,
      "percentageAndChargeConfigurations":true,
      "promotionAndOffersManagement":true
    },
    "reports":true,
    "reportsSubMenu":{
      "tenantReports": true,
      "ownerReports": true,
      "supportReports": true,
      "financeReports": true
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
