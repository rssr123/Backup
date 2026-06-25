// const { protractor, ElementFinder, browser, by, element, ExpectedConditions} = require('protractor');
// jasmine.DEFAULT_TIMEOUT_INTERVAL = 60000;
// describe('Tax Code Listing Tests', () => {
//   beforeAll(async () => {
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

  it('should collapse the search filter bar', async () => {
    browser.refresh();
    const searchFilterBar = element(by.className('btn btn-primary ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(searchFilterBar), 100000, 'Search Filter collapse button taking too long to be clickable');
    await searchFilterBar.click();

    const searchFilterBox = element(by.className('right-section73 collapse ng-tns-c1107586794-0 show')); // Adjust the ID based on your actual element
    await browser.wait(ExpectedConditions.stalenessOf(searchFilterBox), 100000, 'Box taking too long to close');
  });

  it('should collapse and open back the search filter bar', async () => {
    browser.refresh();
    const searchFilterBar = element(by.className('btn btn-primary ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(searchFilterBar), 100000, 'Search Filter collapse button taking too long to be clickable');
    await searchFilterBar.click();

    const searchFilterBox = element(by.className('right-section73 collapse ng-tns-c1107586794-0 show')); // Adjust the ID based on your actual element
    await browser.wait(ExpectedConditions.stalenessOf(searchFilterBox), 100000, 'Box taking too long to close');
    browser.sleep(2000);
    await searchFilterBar.click();
    await browser.wait(ExpectedConditions.presenceOf(searchFilterBox), 100000, 'Box taking too long to open');
    browser.sleep(2000);
  });

  it('should show 2 records when 2 records per page is selected', async () => {
    browser.refresh();
    // Assuming you have a control to set the number of records per page, replace 'perPageSelector' 
    // with your actual selector
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
    // Set the number of records per page to 4
    dropdownMenuButton.click();
    const iSize = element(by.buttonText('2'));
    await browser.wait(ExpectedConditions.elementToBeClickable(iSize), 100000, 'Page Size selector button taking too long to be clickable');
    iSize.click();

    // Wait for the table to update (you may need to wait for data to load)
    await browser.sleep(10000);  // Adjust the sleep time based on your application's behavior

    const list = element.all(by.className('MATable ng-tns-c1107586794-0'));
    const numberOfRows = list.all(by.tagName("tr")).count();
    expect(numberOfRows).toBe(3);

    browser.sleep(1000);
  });

  it('should show 4 records when 4 records per page is selected', async () => {
    browser.refresh();
    // Assuming you have a control to set the number of records per page, replace 'perPageSelector' 
    // with your actual selector
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
    // Set the number of records per page to 4
    dropdownMenuButton.click();
    const iSize = element(by.buttonText('4'));
    await browser.wait(ExpectedConditions.elementToBeClickable(iSize), 100000, 'Page Size selector button taking too long to be clickable');
    iSize.click();

    // Wait for the table to update (you may need to wait for data to load)
    await browser.sleep(10000);  // Adjust the sleep time based on your application's behavior

    const list = element.all(by.className('MATable ng-tns-c1107586794-0'));
    const numberOfRows = list.all(by.tagName("tr")).count();
    expect(numberOfRows).toBe(5);

    browser.sleep(1000);
  });

  it('should show 6 records when 6 records per page is selected', async () => {
    browser.refresh();
    // Assuming you have a control to set the number of records per page, replace 'perPageSelector' 
    // with your actual selector
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
    // Set the number of records per page to 4
    dropdownMenuButton.click();
    const iSize = element(by.buttonText('6'));
    await browser.wait(ExpectedConditions.elementToBeClickable(iSize), 100000, 'Page Size selector button taking too long to be clickable');
    iSize.click();

    // Wait for the table to update (you may need to wait for data to load)
    await browser.sleep(10000);  // Adjust the sleep time based on your application's behavior

    const list = element.all(by.className('MATable ng-tns-c1107586794-0'));
    const numberOfRows = list.all(by.tagName("tr")).count();
    expect(numberOfRows).toBe(7);

    browser.sleep(1000);
  });

  it('should show 10 records when 10 records per page is selected', async () => {
    browser.refresh();
    // Assuming you have a control to set the number of records per page, replace 'perPageSelector' 
    // with your actual selector
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
    // Set the number of records per page to 4
    dropdownMenuButton.click();
    const iSize = element(by.buttonText('10'));
    await browser.wait(ExpectedConditions.elementToBeClickable(iSize), 100000, 'Page Size selector button taking too long to be clickable');
    iSize.click();

    // Wait for the table to update (you may need to wait for data to load)
    await browser.sleep(10000);  // Adjust the sleep time based on your application's behavior

    const list = element.all(by.className('MATable ng-tns-c1107586794-0'));
    const numberOfRows = list.all(by.tagName("tr")).count();
    expect(numberOfRows).toBe(11);

    browser.sleep(1000);
  });

    it('should reset all the search filter\'s field when clicking on reset button', async () => {
    browser.refresh();
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');

    const taxCodeInput = element(by.id('taxCode'));
    const taxCodeNameEnInput = element(by.id('taxCodeNameEn'));
    const taxCodeNameBmInput = element(by.id('taxCodeNameBM'));
    const modifiedBy = element(by.id('modifiedBy'));
    const daterange = element(by.name('daterange')); 
    const selectStatus = element(by.css('div.form-group select.form-control'));

    await taxCodeInput.sendKeys('taxcd');
    await taxCodeNameEnInput.sendKeys('taxcd');
    await taxCodeNameBmInput.sendKeys('taxcd');
    await modifiedBy.sendKeys('tester');

    await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');

    const resetItemButton = element(by.className('btn btnReset ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(resetItemButton), 100000, 'Reset Item button taking too long to be clickable');
    await resetItemButton.click();

    browser.sleep(5000);

    expect(taxCodeInput.getAttribute('value')).toEqual('');
    expect(taxCodeNameEnInput.getAttribute('value')).toEqual('');
    expect(taxCodeNameBmInput.getAttribute('value')).toEqual('');
    expect(modifiedBy.getAttribute('value')).toEqual('');
    expect(daterange.getAttribute('value')).toEqual('');
    expect(selectStatus.getAttribute('value')).toEqual('Active');

});

  it('should retrieve all results matching the search filter for Tax Code', async () => {
    browser.refresh();
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
    const taxCodeInput = element(by.id('taxCode'));
    await taxCodeInput.sendKeys('taxcd');
    await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
    browser.sleep(10000);

    const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
    await applyButton.click();

    browser.sleep(5000);

    const tableRows = element.all(by.css('.MATable tbody tr'));

    for (let i = 0; i < await tableRows.count(); i++) {
        const cellInFirstColumn = tableRows.get(i).element(by.css('td:first-child'));
        const textInCell = await cellInFirstColumn.getText();
        expect(textInCell).toContain('taxcd');
    }
});

it('should retrieve all results matching the search filter for Tax Code Name (EN)', async () => {
  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
  const taxCodeNameEnInput = element(by.id('taxCodeNameEn'));
  await taxCodeNameEnInput.sendKeys('tax');
  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(10000);

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  browser.sleep(5000);

  const tableRows = element.all(by.css('.MATable tbody tr'));

  for (let i = 0; i < await tableRows.count(); i++) {
      const cellInThirdColumn = tableRows.get(i).element(by.css('td:nth-child(3)'));

      const textInCell = await cellInThirdColumn.getText();
      expect(textInCell).toContain('tax');
  }
});

it('should retrieve all results matching the search filter for Tax Code Name (BM)', async () => {
  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
  const taxCodeNameBMInput = element(by.id('taxCodeNameBM'));
  await taxCodeNameBMInput.sendKeys('tax');
  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(10000);

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  browser.sleep(5000);

  const tableRows = element.all(by.css('.MATable tbody tr'));

  for (let i = 0; i < await tableRows.count(); i++) {
      const cellInFourthColumn = tableRows.get(i).element(by.css('td:nth-child(4)'));
      const textInCell = await cellInFourthColumn.getText();
      expect(textInCell).toContain('tax');
  }
});

it('should retrieve all results matching the search filter for Modified By', async () => {
  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
  const modifiedByInput = element(by.id('modifiedBy'));
  await modifiedByInput.sendKeys('w');
  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(10000);

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  browser.sleep(5000);

  const tableRows = element.all(by.css('.MATable tbody tr'));

  for (let i = 0; i < await tableRows.count(); i++) {
      const cellInFifthColumn = tableRows.get(i).element(by.css('td:nth-child(5)'));
      const textInCell = await cellInFifthColumn.getText();
      expect(textInCell).toContain('w');
  }
});

it('should not retrieve any results matching the search filter for invalid Tax Code', async () => {
  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
  const taxCodeInput = element(by.id('taxCode'));
  await taxCodeInput.sendKeys('@');
  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(10000);

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  // Wait for the success alert to be present
  const addSuccess = element(by.className('alert alert-warning PA-alert-box ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.presenceOf(addSuccess), 10000, 'Success alert taking too long to appear');
await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  // Assert that the success alert contains the expected text
  await browser.wait(async () => {
      const successText = await addSuccess.getText();
      return successText.includes('Your search did not match any records. Please try adjusting your search criteria.');
  }, 200000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await addSuccess.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

    browser.sleep(1000);
  });

  it('should not retrieve any results matching the search filter for invalid Tax Code Name (En)', async () => {
    browser.refresh();
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
    const taxCodeNameEnInput = element(by.id('taxCodeNameEn'));
    await taxCodeNameEnInput.sendKeys('@');
    await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
    browser.sleep(10000);
  
    const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
    await applyButton.click();
  
    // Wait for the success alert to be present
    const addSuccess = element(by.className('alert alert-warning PA-alert-box ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.presenceOf(addSuccess), 10000, 'Success alert taking too long to appear');
    
    // Assert that the success alert contains the expected text
    await browser.wait(async () => {
        const successText = await addSuccess.getText();
        return successText.includes('Your search did not match any records. Please try adjusting your search criteria.');
    }, 200000, 'Success alert text taking too long to match');
    
      // Additional expectation if needed
      expect(await addSuccess.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');
  
      browser.sleep(1000);
});

it('should not retrieve any results matching the search filter for invalid Tax Code Name (BM)', async () => {
  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
  const taxCodeNameBMInput = element(by.id('taxCodeNameBM'));
  await taxCodeNameBMInput.sendKeys('@');
  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(10000);

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  // Wait for the success alert to be present
  const addSuccess = element(by.className('alert alert-warning PA-alert-box ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.presenceOf(addSuccess), 10000, 'Success alert taking too long to appear');
  
  // Assert that the success alert contains the expected text
  await browser.wait(async () => {
      const successText = await addSuccess.getText();
      return successText.includes('Your search did not match any records. Please try adjusting your search criteria.');
  }, 200000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await addSuccess.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

    browser.sleep(1000);
});

it('should not retrieve any results matching the search filter for invalid Modified By', async () => {
  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
  const modifiedByInput = element(by.id('modifiedBy'));
  await modifiedByInput.sendKeys('@');
  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(10000);

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  // Wait for the success alert to be present
  const addSuccess = element(by.className('alert alert-warning PA-alert-box ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.presenceOf(addSuccess), 10000, 'Success alert taking too long to appear');
  
  // Assert that the success alert contains the expected text
  await browser.wait(async () => {
      const successText = await addSuccess.getText();
      return successText.includes('Your search did not match any records. Please try adjusting your search criteria.');
  }, 200000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await addSuccess.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

    browser.sleep(1000);
});

it('should deactivate the first row of the tax code when clicking on the status button', async () => {
  browser.refresh();
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');
    browser.sleep(5000);

    const firstRowActionButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
    first().element(by.className('form-check-input ng-tns-c1107586794-0'));

    const firstRowTaxCode = element.all(by.className('MATable ng-tns-c1107586794-0')).
    get(0).element(by.className('ng-tns-c1107586794-0 ng-star-inserted'))
    .element(by.className('ng-tns-c1107586794-0'));

    browser.sleep(10000);

    firstRowActionButton.click();

    browser.sleep(10000);

    const firstRowTaxCodeAfterDisable = element.all(by.className('MATable ng-tns-c1107586794-0')).
    get(0).element(by.className('ng-tns-c1107586794-0 ng-star-inserted'))
    .element(by.className('ng-tns-c1107586794-0'));

    expect(firstRowTaxCodeAfterDisable).not.toEqual(firstRowTaxCode);
});

it('should change the status to inactive on the search filter and return all inactive records', async () => {

  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop-down button taking too long to be clickable');
  
  await browser.executeScript('window.scrollBy(0, 300);');

  browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness

  const selectStatus = element(by.css('div.form-group select.form-control'));
  await selectStatus.click();
  
  const inactiveOption = element(by.css('option[value="D"]'));
  await inactiveOption.click();

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  browser.sleep(5000);
  const tableRows = element.all(by.css('.MATable tbody tr'));

    for (let i = 0; i < await tableRows.count(); i++) {
      const cellInLastColumn = tableRows.get(i).element(by.css('td:nth-child(8)'));
      const textInCell = await cellInLastColumn.getText();
      expect(textInCell).toContain('Inactive');
  }
});

it('should change the status to inactive and change back to active on the search filter and return all active records', async () => {
  browser.refresh();
  const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop-down button taking too long to be clickable');
  
  await browser.executeScript('window.scrollBy(0, 300);');

  browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness

  const selectStatus = element(by.css('div.form-group select.form-control'));
  await selectStatus.click();
  
  const inactiveOption = element(by.css('option[value="D"]'));
  await inactiveOption.click();

  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
  await applyButton.click();

  browser.sleep(5000);
  const tableRows = element.all(by.css('.MATable tbody tr'));

    for (let i = 0; i < await tableRows.count(); i++) {
      const cellInLastColumn = tableRows.get(i).element(by.css('td:nth-child(8)'));
      const textInCell = await cellInLastColumn.getText();
      expect(textInCell).toContain('Inactive');
  }

  const activeOption = element(by.css('option[value="A"]'));
  await activeOption.click();

  await applyButton.click();
  browser.sleep(5000);

  for (let i = 0; i < await tableRows.count(); i++) {
    const cellInLastColumn = tableRows.get(i).element(by.css('td:nth-child(8)'));
    const textInCell = await cellInLastColumn.getText();
    expect(textInCell).toContain('Active');
}

browser.sleep(2000);

});

  it('should search for "roy2" and try to deactivate it and ensure it has an error message showing this record is being used by another module', async () => {
    browser.refresh();
    const dropdownMenuButton = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 100000, 'Drop down button taking too long to be clickable');

    const taxCodeInput = element(by.id('taxCode'));
    taxCodeInput.sendKeys('roy2');

    await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
    browser.sleep(5000);

    const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
    await browser.wait(ExpectedConditions.elementToBeClickable(applyButton), 100000, 'Apply Item button taking too long to be clickable');
    await applyButton.click();
    browser.sleep(10000);

    await browser.executeScript('window.scrollTo(0, 0)');
    browser.sleep(10000);

    const firstRowActionButton = element.all(by.className('MATable ng-tns-c1107586794-0')).
    first().element(by.className('form-check-input ng-tns-c1107586794-0'));

    firstRowActionButton.click();

    browser.sleep(3000);

    const moduleInUsed = element(by.className('alert alert-warning PA-alert-box'));
    await browser.wait(ExpectedConditions.presenceOf(moduleInUsed), 1000000, 'module in used alert taking too long to appear');
    
    await browser.wait(async () => {
      const successText = await moduleInUsed.getText();
      return successText.includes('The record cannot be deactivated because it is in use by another module.');
  }, 200000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await moduleInUsed.getText()).toContain
    ('The record cannot be deactivated because it is in use by another module.');

    browser.sleep(1000);
  });


it('should input a valid date range and check records returned is within the date range', async () => {
  browser.refresh();
  // Navigate to the page or perform any necessary setup
  const startDate = '10 Dec 2023';
  const endDate = '15 Dec 2023';

  // Input a valid date range in the "Date Modified Range" field
  const dateRangeInput = element(by.css('[name="daterange"]'));
  await browser.wait(ExpectedConditions.presenceOf(dateRangeInput), 100000, 'Date range input taking too long to appear');
  dateRangeInput.clear();
  await dateRangeInput.sendKeys(`${startDate} to ${endDate}`);

  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(5000);

  // Click the Apply button to apply the date range filter
  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await applyButton.click();
  browser.sleep(10000);

  await browser.executeScript('window.scrollTo(0, 0)');
  browser.sleep(5000);

  // Wait for the table to update with the filtered records
  // You may need to add a wait condition based on your application behavior

  // Get all records in column 6 of the table
  const column6Records = element.all(by.css('.MATable tbody tr td:nth-child(6)'));

  // Iterate through each record using a for loop
  const recordCount = await column6Records.count();
  for (let i = 0; i < recordCount; i++) {
    const record = column6Records.get(i);
    const recordDate = await record.getText();

    // Convert the recordDate to a Date object (adjust this based on your date format)
    const recordDateObject = new Date(recordDate);
    recordDateObject.setHours(recordDateObject.getHours() + 8);

    // Check if the recordDate is within the specified date range
    expect(recordDateObject >= new Date(startDate + ' 00:00:00') && recordDateObject <= new Date(endDate + ' 23:59:59')).toBe(true);
  }
});

it('should input a invalid date range and check there is no records returned', async () => {
  browser.refresh();
  // Navigate to the page or perform any necessary setup
  const startDate = '10 Dec 0202';
  const endDate = '15 Dec 0202';

  // Input a valid date range in the "Date Modified Range" field
  const dateRangeInput = element(by.css('[name="daterange"]'));
  await browser.wait(ExpectedConditions.presenceOf(dateRangeInput), 100000, 'Date range input taking too long to appear');
  dateRangeInput.clear();
  await dateRangeInput.sendKeys(`${startDate} to ${endDate}`);

  await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
  browser.sleep(5000);

  // Click the Apply button to apply the date range filter
  const applyButton = element(by.className('btn btnApply ng-tns-c1107586794-0'));
  await applyButton.click();
  browser.sleep(3000);

  await browser.executeScript('window.scrollTo(0, 0)');
  // browser.sleep(5000);

  const addSuccess = element(by.className('alert alert-warning PA-alert-box ng-tns-c1107586794-0'));
  await browser.wait(ExpectedConditions.presenceOf(addSuccess), 10000, 'Success alert taking too long to appear');
  
  // Assert that the success alert contains the expected text
  await browser.wait(async () => {
      const successText = await addSuccess.getText();
      return successText.includes('Your search did not match any records. Please try adjusting your search criteria.');
  }, 200000, 'Success alert text taking too long to match');
  
    // Additional expectation if needed
    expect(await addSuccess.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

    browser.sleep(1000);
});


// });









// import { ComponentFixture, TestBed } from '@angular/core/testing';

// import { TaxCodeListingComponent } from './tax-code-listing.component';

// describe('TaxCodeListingComponent', () => {
//   let component: TaxCodeListingComponent;
//   let fixture: ComponentFixture<TaxCodeListingComponent>;

//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       declarations: [TaxCodeListingComponent]
//     });
//     fixture = TestBed.createComponent(TaxCodeListingComponent);
//     component = fixture.componentInstance;
//     fixture.detectChanges();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });
//});
