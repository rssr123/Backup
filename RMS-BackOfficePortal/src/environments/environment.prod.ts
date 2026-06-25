// src/environments/environment.prod.ts
export const environment = {
    production: true,
    appVersion: '1.1.0',
    uat: false,
    211: false,
    sit: false,
    cdc: false,
    // apiUrl: 'https://ssm-dev.southeastasia.cloudapp.azure.com:2443/java',
    angularPortal: 'https://rms.ssm4u.com.my/rmsbo',// 'https://ssm-dev.southeastasia.cloudapp.azure.com:2444',
    //url:'https://appsdev.ssm4u.com.my',// 'https://ssm-dev.southeastasia.cloudapp.azure.com',
    apiUrl: 'https://rms.ssm4u.com.my/rmsrest',
    // apiUrl: '/rmsrest',//'https://rmsstg.ssm4u.com.my/rmsrest';'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
    apiAuthUrl: 'https://rms.ssm4u.com.my/rmsrest',
    //vjsUrl: 'https://0.0.0.0:8447/jasperserver-pro/client/visualize.js',//'https://172.188.49.207:8443/jasperserver-pro/client/visualize.js',
    //vjsUsername: 'jasperadmin',
    //vjsPassword: 'jasperadmin',
    fmsCheckCustIdApi: 'https://integrasiapi.ssm4u.com.my:9443/ssm/ssm4u/fms/ar/get-customer',
    xibmclientid: '3ff9e64eeaa38038ca04d624e3c228c6',
    notificationURL: 'wss://rms.ssm4u.com.my/rmsrest/notifications',
    forceLogoutIDP: false,
    idpLogoutEndpoint: 'https://idpro.ssm.com.my/nidp/app/logout',
    ssm4uEndpoint: 'https://ssm4u.com.my/',

    
        // ... any other configuration values
  
        PaginationMaxSize: 3,
        DefaultPage:1,
        ItemPerPage:20,
        DropDownSize:1000,
        dropdownOptions: [20, 40, 60, 100],
        fadeInOutDuration: 15000,
  
        authKey: 'Basic cm1zOlBAJCR3MHJkJCRN',
      baseHref: '/rmsbo/' //20250312,Roy-RouteIssue
  };
  
  
