// src/environments/environment.prod.ts
export const environment = {
    production: false,
    appVersion: '1.1.0',
    uat: false,
    211: false,
    sit: false,
    cdc: true,
    // apiUrl: 'https://ssm-dev.southeastasia.cloudapp.azure.com:2443/java',
    angularPortal: 'https://rmsstg.ssm4u.com.my/rmsbo',// 'https://ssm-dev.southeastasia.cloudapp.azure.com:2444',
    //url:'https://appsdev.ssm4u.com.my',// 'https://ssm-dev.southeastasia.cloudapp.azure.com',
    apiUrl: 'https://rmsstg.ssm4u.com.my/rmsrest',
    // apiUrl: '/rmsrest',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
    apiAuthUrl:'https://rmsstg.ssm4u.com.my/rmsrest',
    //vjsUrl: 'https://0.0.0.0:8447/jasperserver-pro/client/visualize.js',//'https://172.188.49.207:8443/jasperserver-pro/client/visualize.js',
    //vjsUsername: 'jasperadmin',
    //vjsPassword: 'jasperadmin',
    forceLogoutIDP: false,
    fmsCheckCustIdApi: 'https://rmsstg.ssm4u.com.my/fms/api/fms/v1/arc',
    mockAPIUrl: 'https://rmsstg.ssm4u.com.my/fms',
    xibmclientid: 'c988dc347b25e7e3c8d704840de0ef4d',
    notificationURL: 'wss://rmsstg.ssm4u.com.my/rmsrest/notifications',
    idpLogoutEndpoint: 'https://authstg.ssm4u.com.my/nidp/app/logout',
    ssm4uEndpoint: 'https://ssm4u.com.my/',

        // ... any other configuration values

        PaginationMaxSize: 3,
        DefaultPage:1,
        ItemPerPage:20,
        DropDownSize:1000,
        dropdownOptions: [20, 40, 60, 100],
        fadeInOutDuration: 15000,

        authKey: 'Basic cm95OnBhc3M=',
        baseHref: '/rmsbo/' //20250312,Roy-RouteIssue
  };
  
