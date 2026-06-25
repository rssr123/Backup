// src/environments/environment.prod.ts
export const environment = {
    production: false,
    appVersion: '1.1.0',
    ssm: false,
    211: true,
    sit: false,
    cdc: false,
    // apiUrl: 'https://ssm-dev.southeastasia.cloudapp.azure.com:2443/java',
    angularPortal: 'https://4.193.214.62:8443/rmsbo',
    //url:'https://appsdev.ssm4u.com.my',// 'https://ssm-dev.southeastasia.cloudapp.azure.com',
    apiUrl: 'https://4.193.214.62:8443/rmsrest',
    // apiUrl: '/rmsrest',//'https://4.193.214.62:8443/rmsrest';
    apiAuthUrl: 'https://4.193.214.62:8443/rmsrest',
    fmsCheckCustIdApi: 'https://4.193.214.62:8443/fms/api/fms/v1/arc',
    notificationURL: 'wss://4.193.214.62:8443/rmsrest/notifications',
    forceLogoutIDP: false,
    idpLogoutEndpoint: 'https://login.microsoftonline.com/common/oauth2/logout',
    xibmclientid: 'c988dc347b25e7e3c8d704840de0ef4d',
    ssm4uEndpoint: 'https://ssm4u.com.my/',


        // ... any other configuration values

        PaginationMaxSize: 3,
        DefaultPage:1,
        ItemPerPage:20,
        DropDownSize:1000,
        dropdownOptions: [20, 40, 60, 100],
        fadeInOutDuration: 15000,

        authKey: 'Basic cm95OnBhc3M='
  };
  
