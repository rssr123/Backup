// src/environments/environment.prod.ts
export const environment = {
    production: false,
    localtomcat: false,
    ssmdev: false,
    ssmstg: true,
    cdc: true,
   angularPortal:'https://10.221.24.146:8080/rms',
    apiUrl: 'https://10.221.24.146:8080/rmsrest',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2443',
    url: 'https://10.221.24.146:8080/rms',//'https://ssm-dev.southeastasia.cloudapp.azure.com:2445',
    bo_url: 'https://10.221.24.146:8080/rmsbo',
    eghl: 'https://pay.e-ghl.com/IPGSG/Payment.aspx',
    forceLogoutIDP: true,
    idpLogoutEndpoint: 'https://authstg.ssm.com.my:8443/nidp/app/logout',
    env: 'https://10.221.24.146:8080/mockcrs',
    // vjsUrl: 'https://0.0.0.0:8447/jasperserver-pro/client/visualize.js',//'https://13.67.106.225:8443/jasperserver-pro/client/visualize.js',
    // vjsUsername: 'jasperadmin',
    // vjsPassword: 'jasperadmin'
    // ... any other configuration values,
    authKey: 'Basic cm95OnBhc3M='
  };


