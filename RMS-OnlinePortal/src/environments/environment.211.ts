// src/environments/environment.prod.ts
export const environment = {
  211: true,
  production: false,
  appVersion: '1.1.0',
  /*
  angularPortal:'https://52.163.62.7:1443/rms',
  apiUrl: 'https://52.163.62.7:1443/rmsrest',
  url: 'https://52.163.62.7:1443/rms',
  bo_url: 'https://52.163.62.7:1443/rmsbo',
  */
  angularPortal:'https://4.193.214.62:8443/rms',
  // apiUrl: '/rmsrest',
  apiUrl: 'https://4.193.214.62:8443/rmsrest',
  apiAuthUrl: 'https://4.193.214.62:8443/rmsrest',
  url: 'https://4.193.214.62:8443/rms',
  bo_url: 'https://4.193.214.62:8443/rmsbo',
  eghl: 'https://pay.e-ghl.com/IPGSG/Payment.aspx',
  // vjsUrl: 'https://0.0.0.0:8447/jasperserver-pro/client/visualize.js',//'https://13.67.106.225:8443/jasperserver-pro/client/visualize.js',
  // vjsUsername: 'jasperadmin',
  // vjsPassword: 'jasperadmin'
  fmsCheckCustIdApi: 'https://4.193.214.62:8443/fms/api/fms/v1/arc',
  forceLogoutIDP: false,
  idpLogoutEndpoint: 'https://login.microsoftonline.com/common/oauth2/logout',
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
