// src/environments/environment.prod.ts
export const environment = {
  production: false,
  localtomcat: false,
  ssmdev: false,
  ssmstg: false,
  ssmprd: true,
  cdc: false,
 angularPortal:'https://rms.ssm4u.com.my/rms',
  apiUrl: 'https://rms.ssm4u.com.my/rmsrest',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
  url: 'https://rms.ssm4u.com.my/rms',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2445',
  bo_url: 'https://rms.ssm4u.com.my/rmsbo',
  eghl: 'https://pay.e-ghl.com/IPGSG/Payment.aspx',
  forceLogoutIDP: true,
  idpLogoutEndpoint: 'https://idprodev.ssm.com.my:8443/nidp/app/logout',
  env: 'https://rms.ssm4u.com.my/mockcrs',
  // vjsUrl: 'https://0.0.0.0:8447/jasperserver-pro/client/visualize.js',//'https://13.67.106.225:8443/jasperserver-pro/client/visualize.js',
  // vjsUsername: 'jasperadmin',
  // vjsPassword: 'jasperadmin'
  // ... any other configuration values,
  authKey: 'Basic cm95OnBhc3M='
};
