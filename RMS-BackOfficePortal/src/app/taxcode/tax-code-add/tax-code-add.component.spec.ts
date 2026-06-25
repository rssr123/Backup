const { protractor} = require('protractor');

jasmine.DEFAULT_TIMEOUT_INTERVAL = 60000;

describe('Tax Code Add Tests', () => {
  beforeAll(async () => {
    browser.waitForAngularEnabled(false);
    await browser.manage().window().maximize();
    browser.get('https://localhost:4200/tax-code-listing');
    const advancedButton = element(by.id('details-button'));
    await browser.wait(ExpectedConditions.elementToBeClickable(advancedButton), 100000);
    advancedButton.click();
    const proceedLink = element(by.id('proceed-link'));
    proceedLink.click();
 
    const advancedButton2 = element(by.id('details-button'));
    await browser.wait(ExpectedConditions.elementToBeClickable(advancedButton), 100000);
    advancedButton2.click();
    const proceedLink2 = element(by.id('proceed-link'));
    proceedLink2.click();
 
    const username = element(by.id('i0116'));
    await browser.wait(ExpectedConditions.elementToBeClickable(username), 100000);
    username.sendKeys('wewong@persys-tech.com');
    const nextButton = element(by.id('idSIButton9'));
    nextButton.click();

    const password = element(by.id('i0118'));
    await browser.wait(ExpectedConditions.elementToBeClickable(password), 100000);
    password.sendKeys('password');
    const signinButton = element(by.id('idSIButton9'));
    signinButton.click();
 
    const yesButton = element(by.id('idSIButton9'));
    await browser.wait(ExpectedConditions.elementToBeClickable(yesButton), 100000);
    yesButton.click();
  });

  it('should add a new item with tax code information', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));

    const input = 'tcd' + Math.floor(Math.random() * 10000000);

    await taxCodeInput.sendKeys(input);
    await taxCodeNameEnInput.sendKeys('taxcd_test' + input);
    await taxCodeNameBmInput.sendKeys('taxcd_cuba' + input);
    await taxPercentageInput.sendKeys('5.32');
  
    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();
  
    // Wait for the success alert to be present
    const addSuccess = element(by.className('alert alert-success PA-alert-box'));
    await browser.wait(ExpectedConditions.presenceOf(addSuccess), 10000, 'Success alert taking too long to appear');
  
    // Assert that the success alert contains the expected text
    await browser.wait(async () => {
      const successText = await addSuccess.getText();
      return successText.includes('added successfuly');
    }, 200000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await addSuccess.getText()).toContain('added successfuly');

    browser.sleep(1000);
  });

  it('should show all error messages for mandatory fields if not filled', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
    
    const submitButton = element(by.buttonText('Submit'));
    await browser.wait(ExpectedConditions.elementToBeClickable(submitButton), 1000000, 'Submit button taking too long to be clickable');
    await submitButton.click();

    browser.sleep(1500);

    // Wait for error messages to be displayed
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(4); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code is required');

    const taxCodeNameENErrorMessage = errorMessageElements.get(1);
    expect(await taxCodeNameENErrorMessage.getText()).toContain('Tax Code Name (EN) is required');

    const taxCodeNameBMErrorMessage = errorMessageElements.get(2);
    expect(await taxCodeNameBMErrorMessage.getText()).toContain('Tax Code Name (BM) is required');

    const taxPercentageErrorMessage = errorMessageElements.get(3);
    expect(await taxPercentageErrorMessage.getText()).toContain('Tax Percentage is required');

  });

  it('should show Tax Percentage must be greater than 0 when inputting negative value for it', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await browser.waitForAngular();
  
    await taxCodeInput.sendKeys('taxcd_08');
    await taxCodeNameEnInput.sendKeys('taxcd_test_08');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba_08');
    await taxPercentageInput.sendKeys('-0.32');
  
    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();
  
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage must be greater than or equal to 1');

    browser.sleep(1000);
  });

  it('should show Tax Percentage must be less than or equal to 100 when inputting value more than 100 for it', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await browser.waitForAngular();
  
    await taxCodeInput.sendKeys('taxcd_08');
    await taxCodeNameEnInput.sendKeys('taxcd_test_08');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba_08');
    await taxPercentageInput.sendKeys('100.01');
  
    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();
  
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage must be less than or equal to 100');

    browser.sleep(1000);
  });

  it('should error of Tax Percentage is required when trying to input a string to the field', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await browser.waitForAngular();
  
    await taxCodeInput.sendKeys('taxcd_07');
    await taxCodeNameEnInput.sendKeys('taxcd_test_07');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba_07');
    await taxPercentageInput.sendKeys('abc');
  
    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();

    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage is required');

    browser.sleep(1000);
  });

  it('should show Tax Code is required if not filled when pressing Submit button', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await browser.waitForAngular();

    await taxCodeNameEnInput.sendKeys('taxcd_test_07');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba_07');
    await taxPercentageInput.sendKeys('5.32');
    
    const submitButton = element(by.buttonText('Submit'));
    await browser.wait(ExpectedConditions.elementToBeClickable(submitButton), 1000000, 'Submit button taking too long to be clickable');
    await submitButton.click();

    // Wait for error messages to be displayed
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code is required');
    
    browser.sleep(1000);
  });

  it('should show Tax Code Name (EN) is required if not filled when pressing Submit button', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await browser.waitForAngular();

    await taxCodeInput.sendKeys('taxcd_07');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba_07');
    await taxPercentageInput.sendKeys('5.32');
    
    const submitButton = element(by.buttonText('Submit'));
    await browser.wait(ExpectedConditions.elementToBeClickable(submitButton), 1000000, 'Submit button taking too long to be clickable');
    await submitButton.click();

    // Wait for error messages to be displayed
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code Name (EN) is required');
    
    browser.sleep(1000);
  });

  it('should show Tax Code Name (BM) is required if not filled when pressing Submit button', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await browser.waitForAngular();

    await taxCodeInput.sendKeys('taxcd_07');
    await taxCodeNameEnInput.sendKeys('taxcd_test_07');
    await taxPercentageInput.sendKeys('5.32');
    
    const submitButton = element(by.buttonText('Submit'));
    await browser.wait(ExpectedConditions.elementToBeClickable(submitButton), 1000000, 'Submit button taking too long to be clickable');
    await submitButton.click();

    // Wait for error messages to be displayed
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code Name (BM) is required');
    
    browser.sleep(1000);
  });

  it('should show Tax Percentage is required if not filled when pressing Submit button', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  
    await browser.waitForAngular();

    await taxCodeInput.sendKeys('taxcd_07');
    await taxCodeNameEnInput.sendKeys('taxcd_test_07');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba_07');
    
    const submitButton = element(by.buttonText('Submit'));
    await browser.wait(ExpectedConditions.elementToBeClickable(submitButton), 1000000, 'Submit button taking too long to be clickable');
    await submitButton.click();

    // Wait for error messages to be displayed
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage is required');
    
    browser.sleep(1000);
  });

  it('should close the dialog when "Close" button is clicked', async () => {
    browser.refresh();
    // Click the "Add Item" button
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    browser.sleep(1000);

    // Wait for the "Close" button to be present
    const closeButton = element(by.buttonText('Close')); // Adjust the class or locator based on your actual button
    await browser.wait(ExpectedConditions.presenceOf(closeButton), 10000, 'Close button taking too long to appear');

    // Click the "Close" button
    await closeButton.click();

    const dialogElement = element(by.className('app-tax-code-add.ng-star-inserted')); // Adjust the ID based on your actual element
    await browser.wait(ExpectedConditions.stalenessOf(dialogElement), 10000, 'Dialog taking too long to close');
  });

  it('should click once on each field and click the "Close" button', async () => {
    browser.refresh();
    
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    // Click on Tax Code field
    const taxCodeInput = element(by.name('taxCode')); // Adjust the name or locator based on your actual input
    await browser.wait(ExpectedConditions.elementToBeClickable(taxCodeInput), 10000, 'Tax Code input taking too long to be clickable');
    await taxCodeInput.click();

    // Click on Tax Code Name (EN) field
    const taxCodeNameENInput = element(by.name('taxCodeNameEN')); // Adjust the ID or locator based on your actual input
    await browser.wait(ExpectedConditions.elementToBeClickable(taxCodeNameENInput), 10000, 'Tax Code Name (EN) input taking too long to be clickable');
    await taxCodeNameENInput.click();

    // Click on Tax Code Name (BM) field
    const taxCodeNameBMInput = element(by.name('taxCodeNameBM')); // Adjust the name or locator based on your actual input
    await browser.wait(ExpectedConditions.elementToBeClickable(taxCodeNameBMInput), 10000, 'Tax Code Name (BM) input taking too long to be clickable');
    await taxCodeNameBMInput.click();

    // Click on Tax Percentage field
    const taxPercentageInput = element(by.id('taxPercentage')); // Adjust the ID or locator based on your actual input
    await browser.wait(ExpectedConditions.elementToBeClickable(taxPercentageInput), 10000, 'Tax Percentage input taking too long to be clickable');
    await taxPercentageInput.click();

    const submitButton = element(by.buttonText('Submit'));
    await browser.wait(ExpectedConditions.elementToBeClickable(submitButton), 1000000, 'Submit button taking too long to be clickable');
    await submitButton.click();

    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    browser.sleep(1000);

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(4); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code is required');

    const taxCodeNameENErrorMessage = errorMessageElements.get(1);
    expect(await taxCodeNameENErrorMessage.getText()).toContain('Tax Code Name (EN) is required');

    const taxCodeNameBMErrorMessage = errorMessageElements.get(2);
    expect(await taxCodeNameBMErrorMessage.getText()).toContain('Tax Code Name (BM) is required');

    const taxPercentageErrorMessage = errorMessageElements.get(3);
    expect(await taxPercentageErrorMessage.getText()).toContain('Tax Percentage is required');

    // Click on Close button
    const closeButton = element(by.css('.btn.btnClose')); // Adjust the class or locator based on your actual button
    await browser.wait(ExpectedConditions.elementToBeClickable(closeButton), 10000, 'Close button taking too long to be clickable');
    await closeButton.click();

    const dialogElement = element(by.className('app-tax-code-add.ng-star-inserted')); // Adjust the ID based on your actual element
    await browser.wait(ExpectedConditions.stalenessOf(dialogElement), 10000, 'Dialog taking too long to close');
  });

    it('should check for duplicate data for tax code', async () => {
        browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const duplicateRecords = 'tcd' + Math.floor(Math.random() * 10000000);
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await browser.waitForAngular();
  
    await taxCodeInput.sendKeys(duplicateRecords);
    await taxCodeNameEnInput.sendKeys('taxcd_test' + duplicateRecords);
    await taxCodeNameBmInput.sendKeys('taxcd_cuba' + duplicateRecords);
    await taxPercentageInput.sendKeys('5.20');
  
    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();
  
    // Wait for the success alert to be present
    const addSuccess = element(by.className('alert alert-success PA-alert-box'));
    await browser.wait(ExpectedConditions.presenceOf(addSuccess), 10000, 'Success alert taking too long to appear');
  
    // Assert that the success alert contains the expected text
    await browser.wait(async () => {
      const successText = await addSuccess.getText();
      return successText.includes('added successfuly');
    }, 500000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await addSuccess.getText()).toContain('added successfuly');

    await addItemButton.click();
    await taxCodeInput.sendKeys(duplicateRecords);
    await taxCodeNameEnInput.sendKeys('taxcd_test' + duplicateRecords);
    await taxCodeNameBmInput.sendKeys('taxcd_cuba' + duplicateRecords);
    await taxPercentageInput.sendKeys('5.20');

    await submitButton.click();

    const validationError = element(by.className('error-box ng-star-inserted')); // Adjust the ID based on your actual element
    await browser.wait(ExpectedConditions.presenceOf(validationError), 10000, 'Validation Error takes too long to be displayed.');

    browser.sleep(1000);
  });

  it('should test increment value for taxPercentage when pressing upper arrow key', async () => {
    browser.refresh();
    
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    // Locate the number input field
    const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
    await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');

    // Set an initial valid number value
    await numberInput.clear();
    await numberInput.sendKeys(10);

    // Simulate pressing the up arrow key
    await numberInput.sendKeys(protractor.Key.ARROW_UP);

    // Verify that the value is incremented by the default step (0.1)
    const incrementedValue = await numberInput.getAttribute('value');
    expect(incrementedValue).toBe('10.1');
  });

  it('should test decrement value for taxPercentage when pressing lower arrow key', async () => {
    browser.refresh();
    
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    // Locate the number input field
    const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
    await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');

    // Set an initial valid number value
    await numberInput.clear();
    await numberInput.sendKeys(10);

    // Simulate pressing the up arrow key
    await numberInput.sendKeys(protractor.Key.ARROW_DOWN);

    // Verify that the value is incremented by the default step (0.1)
    const incrementedValue = await numberInput.getAttribute('value');
    expect(incrementedValue).toBe('9.9');
  });

  it('should test increment value for taxPercentage when pressing upper arrow key without any initial value', async () => {
    browser.refresh();
    
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    // Locate the number input field
    const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
    await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');

    // Simulate pressing the up arrow key
    await numberInput.sendKeys(protractor.Key.ARROW_UP);

    // Verify that the value is incremented by the default step (0.1)
    const incrementedValue = await numberInput.getAttribute('value');
    expect(incrementedValue).toBe('0.1');
  });

  it('should test decrement value for taxPercentage when pressing lower arrow key without any initial value and throw error for Tax Percentage must be greater than or equal to 1', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    // Locate the number input field
    const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
    await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');

    // Simulate pressing the up arrow key
    await numberInput.sendKeys(protractor.Key.ARROW_DOWN);

    // Verify that the value is decremented by the default step (0.1)
    const decrementValue = await numberInput.getAttribute('value');
    expect(decrementValue).toBe('-0.1');

    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    browser.sleep(1000);

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(2); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code is required');

    const taxCodeErrorMessage1 = errorMessageElements.get(1);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage must be greater than or equal to 1');
  });

  it('should increment the Tax Percentage by 0.1 when pressing upper arrow button for the value more than 2 decimal places', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    // Locate the number input field
    const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
    await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');

    numberInput.sendKeys('5.32');

    // Simulate pressing the up arrow key
    await numberInput.sendKeys(protractor.Key.ARROW_UP);

    // Verify that the value is incremented by the default step (0.1)
    const incrementValue = await numberInput.getAttribute('value');
    expect(incrementValue).toBe('5.42');
  });

  it('should decrement the Tax Percentage by 0.1 when pressing upper arrow button for the value more than 2 decimal places', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    // Locate the number input field
    const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
    await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');

    numberInput.sendKeys('5.32');

    // Simulate pressing the up arrow key
    await numberInput.sendKeys(protractor.Key.ARROW_DOWN);

    // Verify that the value is incremented by the default step (0.1)
    const decrementValue = await numberInput.getAttribute('value');
    expect(decrementValue).toBe('5.22');
  });

  it('should not allow any empty string for the Tax Code as it is a mandatory field', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));
  
    await taxCodeInput.sendKeys(' ');
    await taxCodeNameEnInput.sendKeys('taxcd_test_15');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba_15');
    await taxPercentageInput.sendKeys('5.32');

    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();
  
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code is required');

    browser.sleep(1000);
});

it('should not allow any empty string for the Tax Code Name (EN) as it is a mandatory field', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));

    const input = 'tcd' + Math.floor(Math.random() * 10000000);

    await taxCodeInput.sendKeys(input);
    await taxCodeNameEnInput.sendKeys(' ');
    await taxCodeNameBmInput.sendKeys('taxcd_cuba' + input);
    await taxPercentageInput.sendKeys('5.32');

    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();
  
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code Name (EN) is required');

    browser.sleep(1000);
});

it('should not allow any empty string for the Tax Code Name (BM) as it is a mandatory field', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();
  
    const taxCodeInput = element(by.name('taxCode'));
    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));

  const input = 'tcd' + Math.floor(Math.random() * 10000000);

    await taxCodeInput.sendKeys(input);
    await taxCodeNameEnInput.sendKeys('taxcd_test' + input);
    await taxCodeNameBmInput.sendKeys(' ');
    await taxPercentageInput.sendKeys('5.32');

    const submitButton = element(by.buttonText('Submit'));
    await submitButton.click();
  
    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Code Name (BM) is required');

    browser.sleep(1000);
});

  it('should show not allow Tax Code input is more than 10 characters', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const taxCodeInput = element(by.name('taxCode'));
  
    await browser.waitForAngular();
    const input = 'thistaxcodeismorethan10characters';

    await taxCodeInput.sendKeys(input);

    expect(await taxCodeInput.getAttribute('value')).toBe(input.substring(0, 10));
    browser.sleep(1000);
  });

  it('should show not allow Tax Code Name (EN) input is more than 50 characters', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const taxCodeNameEnInput = element(by.name('taxCodeNameEN'));
  
    await browser.waitForAngular();
    const input = 'thistaxcodenameinenglishisdefinitelymorethanfiftycharacters';

    await taxCodeNameEnInput.sendKeys(input);

    expect(await taxCodeNameEnInput.getAttribute('value')).toBe(input.substring(0, 50));
    browser.sleep(1000);
  });

  it('should show not allow Tax Code Name (BM) input is more than 50 characters', async () => {
    browser.refresh();
    const addItemButton = element(by.buttonText('Add Item'));
    await browser.wait(ExpectedConditions.elementToBeClickable(addItemButton), 100000, 'Add Item button taking too long to be clickable');
    await addItemButton.click();

    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  
    await browser.waitForAngular();
    const input = 'thistaxcodenameinmalayisdefinitelymorethanfiftycharacters';

    await taxCodeNameBmInput.sendKeys(input);

    expect(await taxCodeNameBmInput.getAttribute('value')).toBe(input.substring(0, 50));
    browser.sleep(1000);
  });

});



// // import { ComponentFixture, TestBed } from '@angular/core/testing';

// // import { TaxCodeAddComponent } from './tax-code-add.component';

// // describe('TaxCodeAddComponent', () => {
// //   let component: TaxCodeAddComponent;
// //   let fixture: ComponentFixture<TaxCodeAddComponent>;

// //   beforeEach(() => {
// //     TestBed.configureTestingModule({
// //       declarations: [TaxCodeAddComponent]
// //     });
// //     fixture = TestBed.createComponent(TaxCodeAddComponent);
// //     component = fixture.componentInstance;
// //     fixture.detectChanges();
// //   });

// //   it('should create', () => {
// //     expect(component).toBeTruthy();
// //   });
// // });

// // const {browser, by, element, ExpectedConditions: ExpectedConditions} = require('protractor');

