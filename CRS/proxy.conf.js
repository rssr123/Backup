module.exports = {
    "/payment-page": {
      "target": "https://ssmrms.eastasia.cloudapp.azure.com:4200",
      "secure": false,
      "changeOrigin": true,
      "logLevel": "debug",
      "onProxyRes": function (proxyRes, req, res) {
        proxyRes.headers['Access-Control-Allow-Origin'] = '*';
        proxyRes.headers['Access-Control-Allow-Methods'] = 'GET, POST, PUT, DELETE, OPTIONS';
        proxyRes.headers['Access-Control-Allow-Headers'] = 'Content-Type, Authorization';
      }
    }
  };
  