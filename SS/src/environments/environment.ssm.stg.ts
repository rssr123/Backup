// src/environments/environment.prod.ts
export const environment = {
  production: false,
  localtomcat: false,
  ssmdev: false,
  ssmstg: true,
  cdc: false,
 angularPortal:'https://rmsstg.ssm4u.com.my/rms',
  apiUrl: 'https://rmsstg.ssm4u.com.my/rmsrest',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
  url: 'https://rmsstg.ssm4u.com.my/rms',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2445',
  bo_url: 'https://rmsstg.ssm4u.com.my/rmsbo',
  mockss_url: 'https://rmsstg.ssm4u.com.my/mockss',
  forceLogoutIDP: true,
  idpLogoutEndpoint: 'https://authstg.ssm.com.my:8443/nidp/app/logout',
  env: 'https://rmsstg.ssm4u.com.my',

  authKey: 'Basic cm95OnBhc3M='
};
