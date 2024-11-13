// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  basePath:'http://45.129.87.152:8082/api-admin/',
  version:'1.0.0-04-05-2020-11:41:00',
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

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
