// src/environments/environment.prod.ts
export const environment = {
    production: false,
    appVersion: '1.1.0',
    211: false,
    sit: true,
    angularPortal:'https://rmsdev.ssm4u.com.my/rms',
    //apiUrl: '/rmsrest',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
    apiUrl: 'https://rmsdev.ssm4u.com.my/rmsrest',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
    apiAuthUrl: 'https://rmsdev.ssm4u.com.my/rmsrest',
    url: 'https://rmsdev.ssm4u.com.my/rms',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2445',
    bo_url: 'https://rmsdev.ssm4u.com.my/rmsbo',//'https://ssmrms.eastasia.cloudapp.azure.com:4200',
    eghl: 'https://pay.e-ghl.com/IPGSG/Payment.aspx',
    fmsCheckCustIdApi: 'https://rmsdev.ssm4u.com.my/fms/api/fms/v1/arc',
    forceLogoutIDP: false,
    idpLogoutEndpoint: 'https://idprodev.ssm.com.my:8443/nidp/app/logout',
    ssm4uEndpoint: 'https://dev.ssm4u.com.my/',
    // vjsUrl: 'https://0.0.0.0:8447/jasperserver-pro/client/visualize.js',//'https://13.67.106.225:8443/jasperserver-pro/client/visualize.js',
    // vjsUsername: 'jasperadmin',
    // vjsPassword: 'jasperadmin'
    
    // ... any other configuration values

    PaginationMaxSize: 3,
    DefaultPage:1,
    ItemPerPage:20,
    DropDownSize:1000,
    dropdownOptions: [20, 40, 60, 100],
    fadeInOutDuration: 15000,

    authKey: 'Basic cm95OnBhc3M='
  };
