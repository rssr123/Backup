// src/environments/environment.prod.ts
export const environment = {
  production: false,
  localtomcat: true,
  ssmdev: false,
  ssmstg: false,
  cdc: false,
  angularPortal:'https://4.193.214.62:8443/rms',
  apiUrl: 'https://4.193.214.62:8443/rmsrest',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
  url: 'https://4.193.214.62:8443/rms',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2445',
  bo_url: 'https://4.193.214.62:8443/rmsbo',
  mockss_url: 'https://4.193.214.62:8443/mockss',
  forceLogoutIDP: true,
  idpLogoutEndpoint: 'https://login.microsoftonline.com/common/oauth2/logout',
  env: 'https://4.193.214.62:8443',
  // vjsUrl: 'https://0.0.0.0:8447/jasperserver-pro/client/visualize.js',//'https://13.67.106.225:8443/jasperserver-pro/client/visualize.js',
  // vjsUsername: 'jasperadmin',
  // vjsPassword: 'jasperadmin'
  // ... any other configuration values,
  authKey: 'Basic cm95OnBhc3M='
};