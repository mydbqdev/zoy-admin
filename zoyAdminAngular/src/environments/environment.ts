// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  basePath:'http://localhost:8082/api-admin/',
  websocketBasePath:'ws://localhost:8082/api-admin/',
  basePathExternal:'/api/',
  version:'1.0.0-04-05-2020-11:41:00',
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
    "sales":true,
    "salesSubMenu":{
      "salesPerson":true,
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


  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
