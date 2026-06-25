// const { protractor, browser, by, element, ExpectedConditions: ExpectedConditions } = require('protractor');
// describe('Tax Code Update Tests', () => {
//   beforeAll(() => {
//     browser.waitForAngularEnabled(false);
//     await browser.manage().window().maximize();
//     browser.get('https://localhost:4200/tax-code-listing');
//     const advancedButton = element(by.id('details-button'));
//     await browser.wait(ExpectedConditions.elementToBeClickable(advancedButton), 100000);
//     advancedButton.click();
//     const proceedLink = element(by.id('proceed-link'));
//     proceedLink.click();
 
//     const advancedButton2 = element(by.id('details-button'));
//     await browser.wait(ExpectedConditions.elementToBeClickable(advancedButton), 100000);
//     advancedButton2.click();
//     const proceedLink2 = element(by.id('proceed-link'));
//     proceedLink2.click();
 
//     const username = element(by.id('i0116'));
//     await browser.wait(ExpectedConditions.elementToBeClickable(username), 100000);
//     username.sendKeys('wewong@persys-tech.com');
//     const nextButton = element(by.id('idSIButton9'));
//     nextButton.click();

//     const password = element(by.id('i0118'));
//     await browser.wait(ExpectedConditions.elementToBeClickable(password), 100000);
//     password.sendKeys('password');
//     const signinButton = element(by.id('idSIButton9'));
//     signinButton.click();
 
//     const yesButton = element(by.id('idSIButton9'));
//     await browser.wait(ExpectedConditions.elementToBeClickable(yesButton), 100000);
//     yesButton.click();
//   });

it('should click the edit button for the first row and edit the data on the first row', async () => {
    browser.refresh();
    const input = 'tcd' + Math.floor(Math.random() * 10000000);
    const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
    const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
    const taxPercentageInput = element(by.id('taxPercentage'));

    // Assuming you have a table with rows and each row has an "Edit" button
    const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
    first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

    await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
    'Edit button taking too long to be clickable');

    // Click the Edit button for the first row
    await firstRowEditButton.click();

    await taxCodeNameEnInput.clear();
    await taxCodeNameBmInput.clear();
    await taxPercentageInput.clear();

    await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%' + input);
    await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%' + input);
    await taxPercentageInput.sendKeys('3.33');

    const updateButton = element(by.buttonText('Update'));
    await updateButton.click();
    
      // Wait for the success alert to be present
    const updateSuccess = element(by.className('alert alert-info PA-alert-box'));
    await browser.wait(ExpectedConditions.presenceOf(updateSuccess), 10000, 'Success alert taking too long to appear');
    
      // Assert that the success alert contains the expected text
    await browser.wait(async () => {
      const successText = await updateSuccess.getText();
        return successText.includes('updated successfuly');
    }, 200000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await updateSuccess.getText()).toContain('updated successfuly');
});

it('should clear input for Tax Code Name (EN) and throw an error when clicking on update button', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys(' ');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('3.33');

  taxCodeNameBmInput.click();

  const updateButton = element(by.buttonText('Update'));
  await updateButton.click();

  const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
  await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

  // Assert that error messages are displayed for each mandatory field
  expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

  const taxCodeErrorMessage = errorMessageElements.get(0);
  expect(await taxCodeErrorMessage.getText()).toContain('Tax Code Name (EN) is required');
});

it('should clear input for Tax Code Name (BM) and throw an error when clicking on update button', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  // await taxCodeNameEnInput.sendKeys('');
  await taxCodeNameBmInput.sendKeys(' ');
  await taxPercentageInput.sendKeys('3.33');

  browser.sleep(1000);

  const updateButton = element(by.buttonText('Update'));
  await updateButton.click();

  const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
  await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

  // Assert that error messages are displayed for each mandatory field
  expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

  const taxCodeErrorMessage = errorMessageElements.get(0);
  expect(await taxCodeErrorMessage.getText()).toContain('Tax Code Name (BM) is required');
});

it('should not update the Tax Percentage with an negative value', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('-3.33');

  browser.sleep(1000);

  const updateButton = element(by.buttonText('Update'));
  await updateButton.click();

    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage must be greater than or equal to 1');
});

it('should not update the Tax Percentage with a value more than 100', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('100.03');

  browser.sleep(1000);

  const updateButton = element(by.buttonText('Update'));
  await updateButton.click();

    const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
    await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

    // Assert that error messages are displayed for each mandatory field
    expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

    const taxCodeErrorMessage = errorMessageElements.get(0);
    expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage must be less than or equal to 100');
});

it('should increment the Tax Percentage by 0.1 when pressing upper arrow button', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('10');

  // Locate the number input field
  const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
  await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');
  // Simulate pressing the up arrow key
  await numberInput.sendKeys(protractor.Key.ARROW_UP);

  // Verify that the value is incremented by the default step (0.1)
  const incrementedValue = await numberInput.getAttribute('value');
  expect(incrementedValue).toBe('10.1');

});

it('should decrement the Tax Percentage by 0.1 when pressing lower arrow button', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('10');

  // Locate the number input field
  const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
  await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');
  // Simulate pressing the up arrow key
  await numberInput.sendKeys(protractor.Key.ARROW_DOWN);

  // Verify that the value is incremented by the default step (0.1)
  const incrementedValue = await numberInput.getAttribute('value');
  expect(incrementedValue).toBe('9.9');
});


it('should increment the Tax Percentage by 0.1 when pressing upper arrow button for the value more than 2 decimal places', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('10.21');

  // Locate the number input field
  const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
  await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');
  // Simulate pressing the up arrow key
  await numberInput.sendKeys(protractor.Key.ARROW_UP);

  // Verify that the value is incremented by the default step (0.1)
  const incrementedValue = await numberInput.getAttribute('value');
  expect(incrementedValue).toBe('10.31');

});


it('should decrement the Tax Percentage by 0.1 when pressing lower arrow button for the value more than 2 decimal places', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('10.33');

  // Locate the number input field
  const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
  await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');
  // Simulate pressing the up arrow key
  await numberInput.sendKeys(protractor.Key.ARROW_DOWN);

  // Verify that the value is incremented by the default step (0.1)
  const incrementedValue = await numberInput.getAttribute('value');
  expect(incrementedValue).toBe('10.23');
});

it('should not increment the Tax Percentage by 0.1 when pressing upper arrow button for value 100 and throw an error message', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('100');

  // Locate the number input field
  const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
  await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');
  // Simulate pressing the up arrow key
  await numberInput.sendKeys(protractor.Key.ARROW_UP);

  // Verify that the value is incremented by the default step (0.1)
  const incrementedValue = await numberInput.getAttribute('value');
  expect(incrementedValue).toBe('100');

  const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
  await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

  // Assert that error messages are displayed for each mandatory field
  expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

  const taxCodeErrorMessage = errorMessageElements.get(0);
  expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage must be less than or equal to 100');
});

it('should not decrement the Tax Percentage by 0.1 when pressing lower arrow button for value 0.1 and throw an error message', async () => {
  browser.refresh();
  const taxCodeNameEnInput = element(by.id('taxCodeNameEN'));
  const taxCodeNameBmInput = element(by.name('taxCodeNameBM'));
  const taxPercentageInput = element(by.id('taxPercentage'));

  // Assuming you have a table with rows and each row has an "Edit" button
  const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
  first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

  await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
  'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
  await firstRowEditButton.click();

  await taxCodeNameEnInput.clear();
  await taxCodeNameBmInput.clear();
  await taxPercentageInput.clear();

  await taxCodeNameEnInput.sendKeys('A_UPDATED_SST -3.1%');
  await taxCodeNameBmInput.sendKeys('A_KEMASKINI_SST -3.1%');
  await taxPercentageInput.sendKeys('0.1');

  // Locate the number input field
  const numberInput = element(by.id('taxPercentage')); // Replace with the actual ID or locator of your input
  await browser.wait(ExpectedConditions.elementToBeClickable(numberInput), 10000, 'Number input taking too long to be clickable');
  // Simulate pressing the up arrow key
  await numberInput.sendKeys(protractor.Key.ARROW_DOWN);

  // Verify that the value is incremented by the default step (0.1)
  const incrementedValue = await numberInput.getAttribute('value');
  expect(incrementedValue).toBe('0.1');

  const errorMessageElements = element.all(by.className('error-text ng-star-inserted')); // Assuming you have a class for error messages
  await browser.wait(ExpectedConditions.presenceOf(errorMessageElements.first()), 10000, 'Error messages taking too long to appear');

  // Assert that error messages are displayed for each mandatory field
  expect(await errorMessageElements.count()).toBe(1); // Adjust the count based on the number of mandatory fields

  const taxCodeErrorMessage = errorMessageElements.get(0);
  expect(await taxCodeErrorMessage.getText()).toContain('Tax Percentage must be greater than or equal to 1');
});

  it('should close the dialog when "Close" button is clicked', async () => {
    browser.refresh();
  // Assuming you have a table with rows and each row has an "Edit" button
    const firstRowEditButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
    first().element(by.className('btn btnEdit ng-tns-c1107586794-0'));

    await browser.wait(ExpectedConditions.elementToBeClickable(firstRowEditButton), 100000, 
    'Edit button taking too long to be clickable');

  // Click the Edit button for the first row
    await firstRowEditButton.click();

    browser.sleep(1000);

    // Wait for the "Close" button to be present
    const closeButton = element(by.buttonText('Close')); // Adjust the class or locator based on your actual button
    await browser.wait(ExpectedConditions.presenceOf(closeButton), 10000, 'Close button taking too long to appear');

    // Click the "Close" button
    await closeButton.click();

    const dialogElement = element(by.className('app-tax-code-update.ng-star-inserted')); // Adjust the ID based on your actual element
    await browser.wait(ExpectedConditions.stalenessOf(dialogElement), 10000, 'Dialog taking too long to close');
  });



// });




// import { ComponentFixture, TestBed } from '@angular/core/testing';

// import { TaxCodeUpdateComponent } from './tax-code-update.component';

// describe('TaxCodeUpdateComponent', () => {
//   let component: TaxCodeUpdateComponent;
//   let fixture: ComponentFixture<TaxCodeUpdateComponent>;

//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       declarations: [TaxCodeUpdateComponent]
//     });
//     fixture = TestBed.createComponent(TaxCodeUpdateComponent);
//     component = fixture.componentInstance;
//     fixture.detectChanges();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });
// });
