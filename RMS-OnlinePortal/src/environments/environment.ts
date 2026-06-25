// src/environments/environment.ts
export const environment = {
  production: false,
  appVersion: '1.1.0',
  211:false,
  angularPortal: 'https://localhost:4300',
  apiUrl: 'https://localhost:8080',//'https://ssmrms.eastasia.cloudapp.azure.com:8080',
  apiAuthUrl: 'https://localhost:8080',
  url: 'https://localhost:4300',//'https://ssmrms.eastasia.cloudapp.azure.com:4200',
  bo_url: 'https://localhost:4200',
  eghl: 'https://pay.e-ghl.com/IPGSG/Payment.aspx',
  forceLogoutIDP: false,
  idpLogoutEndpoint: 'https://login.microsoftonline.com/common/oauth2/logout',
  ssm4uEndpoint: 'https://ssm4u.com.my/',
  // ... any other configuration values

  //added by zx
  PaginationMaxSize: 3,
  DefaultPage:1,
  ItemPerPage:20,
  DropDownSize:1000,
  dropdownOptions: [20, 40, 60, 100],
  fadeInOutDuration: 15000,


  authKey: 'Basic cm95OnBhc3M=',

  fmsCheckCustIdApi: 'https://localhost:1443/api/fms/v1/arc',

    // ... any other configuration values

};
