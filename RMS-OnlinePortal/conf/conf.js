var HtmlReporter = require('protractor-beautiful-reporter');

// An example configuration file.
exports.config = {
  //directConnect: true,
  chromeDriver: 'C:/Users/Admin/Downloads/chromedriver-win64/chromedriver-win64/chromedriver',
  specs: ['todo-spec.js'],

  onPrepare: function() {
    // Add a screenshot reporter and store screenshots to `/Reports/screenshots`:
      jasmine.getEnv().addReporter(new HtmlReporter({
           baseDirectory: 'Reports/screenshots'
        }).getJasmine2Reporter());
      },
    
  // Capabilities to be passed to the webdriver instance.
  capabilities: {
    'browserName': 'chrome'
  },

  // Framework to use. Jasmine is recommended.
  framework: 'jasmine',

  // Spec patterns are relative to the current working directory when
  // protractor is called.
  specs: ['C:/Project/SSM-RMS/RMS-BackOfficePortal/src/app/taxcode/tax-code-add/tax-code-add.component.spec.ts',
  'C:/Project/SSM-RMS/RMS-BackOfficePortal/src/app/taxcode/tax-code-listing/tax-code-listing.component.spec.ts',
  'C:/Project/SSM-RMS/RMS-BackOfficePortal/src/app//taxcode/tax-code-update/tax-code-update.component.spec.ts'],

  // Options to be passed to Jasmine.
  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000
  }
};
