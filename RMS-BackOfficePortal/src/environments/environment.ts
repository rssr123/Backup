// src/environments/environment.ts
export const environment = {
    production: false,
    appVersion: '1.1.0',
    ssm: false,
    211: false,
    sit: false,
    cdc: false,
    //apiUrl: 'http://ssm-dev-internal.internal.cloudapp.net:8080',
    // apiUrl: 'https://localhost:8080/rms-service',
    apiUrl: 'https://localhost:8080',
    apiAuthUrl: 'https://localhost:4200/rmsrest',
    angularPortal: 'https://localhost:4200',
    fmsCheckCustIdApi: 'https://integrasiapistg.ssm4u.com.my:9444/ssm/ssm4u/fms/AR/getCustomer',
    mockAPIUrl: 'https://localhost:1443',
    xibmclientid: 'c988dc347b25e7e3c8d704840de0ef4d',
    // notificationURL: 'wss://localhost:8080/notifications', // Disabled - not using WebSocket notifications
    forceLogoutIDP: false,
    idpLogoutEndpoint: 'https://login.microsoftonline.com/common/oauth2/logout',
    ssm4uEndpoint: 'https://ssm4u.com.my/',

  // ... any other configuration values

  PaginationMaxSize: 3,
  DefaultPage:1,
  ItemPerPage:20,
  DropDownSize:1000,
  // dropdownOptions: [2,4,6,10],
  dropdownOptions: [20, 40, 60, 100],
  fadeInOutDuration: 15000,

  authKey: 'Basic cm95OnBhc3M='

};
