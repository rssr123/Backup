describe('Fee Group Listing', () => {
  // beforeAll(async () => {
  //     jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000000;
  //     browser.waitForAngularEnabled(false); 
  //     await browser.manage().window().maximize();
  //     browser.get('https://localhost:4200/fee-group-listing');
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
  //     username.sendKeys('qxchua@persys-tech.com');
  //     const nextButton = element(by.id('idSIButton9'));
  //     nextButton.click();

  //     const password = element(by.id('i0118'));
  //     await browser.wait(ExpectedConditions.elementToBeClickable(password), 100000);
  //     password.sendKeys('!CKKcqx2003');
  //     const signinButton = element(by.id('idSIButton9'));
  //     signinButton.click();

  //     const yesButton = element(by.id('idSIButton9'));
  //     await browser.wait(ExpectedConditions.elementToBeClickable(yesButton), 100000);
  //     yesButton.click();
    
  // });

  it ('should collapse the filter panel', async () => {

      browser.refresh();
      const filterButton = element(by.className('btn btn-primary ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 100000);
      filterButton.click();

      const fiterBar = element(by.className('right-section73 collapse ng-tns-c1058870932-0 show'));
      expect(fiterBar.isPresent()).toBe(false);

  });

  it ('should expand the filter panel', async () => {
    
      browser.refresh();
      const filterButton = element(by.className('btn btn-primary ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 100000);
      filterButton.click();
      filterButton.click();
  
      const fiterBar = element(by.className('right-section73 collapse ng-tns-c1058870932-0 show'));
      expect(fiterBar.isPresent()).toBe(true);

  });

  it ('should display two rows of data per page', async () => {
      
      browser.refresh();
      const dropdown = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1058870932-0'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdown), 8000, 'Drop button taking too long to appear in the DOM');
      dropdown.click();

      const dropdownItem = element(by.cssContainingText('.dropdown-item', '2'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 5000, 'Drop button taking too long to appear in the DOM');
      dropdownItem.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      const result = element(by.className('ng-tns-c1058870932-0'));
      // const numberOfResults = result.getText().split(' ')[3];
      // if (numberOfResults <2) {
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 100000, 'Edit button taking too long to appear in the DOM');
      browser.sleep(6000);
      expect(tableRows.count()).toBe(2);
      const nextButton = element(by.xpath('/html/body/app-root/div/body/main/app-fee-group-listing/body/div/div[1]/div[2]/div[2]/div[2]/ngb-pagination/ul/li[8]'));
      //await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
      await browser.wait(ExpectedConditions.elementToBeClickable(nextButton), 100000, 'Next button taking too long to appear in the DOM');
      await browser.executeScript('window.scrollTo(0, 120)');
      browser.sleep(3000);
      nextButton.click();
      browser.sleep(6000);
      expect(tableRows.count()).toBe(2);
      // }
  });

  it ('should display four rows of data per page', async () => {

      browser.refresh();
      const dropdown = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1058870932-0'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdown), 8000, 'Drop button taking too long to appear in the DOM');
      dropdown.click();

      const dropdownItem = element(by.cssContainingText('.dropdown-item', '4'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 5000, 'Drop button taking too long to appear in the DOM');
      dropdownItem.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      const result = element(by.className('ng-tns-c1058870932-0'));
      // const numberOfResults = result.getText()
      // numberOfResults.then(async (numberOfResults: string) => {
      //     // Assuming text is something like "Showing 1 out of 20 results"
      //     const parts = numberOfResults.split(' '); // Split the text by space
        
      //     // Access individual parts
      //     const outOfIndex = parts.indexOf('of');
      //     const totalResults = parseInt(parts[outOfIndex + 1]);
      //     console.log('Total results:', totalResults);
        
      // if (totalResults <4) {
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 100000, 'Edit button taking too long to appear in the DOM');     
      browser.sleep(6000);    
      expect(tableRows.count()).toBe(4);
      const nextButton = element(by.xpath('/html/body/app-root/div/body/main/app-fee-group-listing/body/div/div[1]/div[2]/div[2]/div[2]/ngb-pagination/ul/li[8]'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
      await browser.wait(ExpectedConditions.elementToBeClickable(nextButton), 100000, 'Next button taking too long to appear in the DOM');
      await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
      browser.sleep(3000);
      nextButton.click();
      browser.sleep(6000);
      expect(tableRows.count()).toBe(4);
      // browser.sleep(10000);
  //     }

  // });
  });

  it ('should display six rows of data per page', async () => {
        
      browser.refresh();
      const dropdown = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1058870932-0'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdown), 8000, 'Drop button taking too long to appear in the DOM');
      dropdown.click();

      const dropdownItem = element(by.cssContainingText('.dropdown-item', '6'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 5000, 'Drop button taking too long to appear in the DOM');
      dropdownItem.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      const result = element(by.className('ng-tns-c1058870932-0'));
      // const numberOfResults = result.getText().split(' ')[3];
      // if (numberOfResults <6) {
      // browser.sleep(10000);
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 100000, 'Edit button taking too long to appear in the DOM');
      browser.sleep(6000);
      expect(tableRows.count()).toBe(6);
      const nextButton = element(by.xpath('/html/body/app-root/div/body/main/app-fee-group-listing/body/div/div[1]/div[2]/div[2]/div[2]/ngb-pagination/ul/li[8]'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
      await browser.wait(ExpectedConditions.elementToBeClickable(nextButton), 100000, 'Next button taking too long to appear in the DOM');
      await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
      browser.sleep(3000);
      nextButton.click();
      browser.sleep(3000);
      expect(tableRows.count()).toBe(6);
      // }
  });

  it ('should display ten rows of data per page', async () => {

      browser.refresh();
      const dropdown = element(by.className('btn btn-secondary dropdown-toggle PaginControl ng-tns-c1058870932-0'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdown), 8000, 'Drop button taking too long to appear in the DOM');
      dropdown.click();

      const dropdownItem = element(by.cssContainingText('.dropdown-item', '10'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight/2)');
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 5000, 'Drop button taking too long to appear in the DOM');
      dropdownItem.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      const result = element(by.className('ng-tns-c1058870932-0'));
      // const numberOfResults = result.getText().split(' ')[3];
      // if (numberOfResults <10) {
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 100000, 'Edit button taking too long to appear in the DOM');
      browser.sleep(6000);
      expect(tableRows.count()).toBe(10);
      const nextButton = element(by.xpath('/html/body/app-root/div/body/main/app-fee-group-listing/body/div/div[1]/div[2]/div[2]/div[2]/ngb-pagination/ul/li[8]'));
      // await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
      await browser.wait(ExpectedConditions.elementToBeClickable(nextButton), 100000, 'Next button taking too long to appear in the DOM');
      await browser.executeScript('window.scrollTo(0, document.body.scrollHeight)');
      browser.sleep(3000);
      nextButton.click();
      browser.sleep(3000);
      expect(tableRows.count()).toBe(10);
      // }
  });

  it ('should reset the filter', async () => {
        
      browser.refresh();
      const feeId = element(by.id('feeGroupId'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeId), 100000);
      feeId.sendKeys('1');

      const feeNameEn = element(by.id('feeGroupNameEn'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeNameEn), 100000);
      feeNameEn.sendKeys('Fee Group 1');

      const feeNameBm = element(by.id('feeGroupNameBM'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeNameBm), 100000, 'Fee Name BM taking too long to appear in the DOM');
      feeNameBm.sendKeys('Kumpulan Yuran 1');

      await browser.executeScript('window.scrollBy(0, 300);');
      browser.sleep(1000); // Adjust the sleep time based on your application's responsiveness

      const modifiedBy = element(by.id('modifiedBy'));
      await browser.wait(ExpectedConditions.elementToBeClickable(modifiedBy), 100000);
      modifiedBy.sendKeys('qxchua');

      const modifiedDate = element(by.css('input[placeholder="Date Range Picker"]'));
      await browser.wait(ExpectedConditions.elementToBeClickable(modifiedDate), 100000);
      modifiedDate.sendKeys('01 Nov 2023 to 01 Dec 2023');


      const dropdownMenuButton = element(by.css('div.form-group select.form-control'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 10000, 'Drop-down button taking too long to be clickable');
      dropdownMenuButton.click();

      const dropdownItem = element(by.css('option[value="D"]'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 10000, 'Drop-down item taking too long to be clickable');
      dropdownItem.click();

      browser.sleep(1000); // Adjust the sleep time based on your application's responsiveness

      const resetButton = element(by.className('btn btnReset ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(resetButton), 10000);
      resetButton.click();

      await browser.executeScript('window.scrollBy(0, -300);');
      browser.sleep(1000); // Adjust the sleep time based on your application's responsiveness

      expect(feeId.getAttribute('value')).toEqual('');
      expect(feeNameEn.getAttribute('value')).toEqual('');
      expect(feeNameBm.getAttribute('value')).toEqual('');

      await browser.executeScript('window.scrollBy(0, 300);');
      browser.sleep(1000); // Adjust the sleep time based on your application's responsiveness
      
      expect(modifiedBy.getAttribute('value')).toEqual('');
      // expect(modifiedDate.getAttribute('value')).toEqual('09 Nov 2023 to 20 Dec 2023');
      expect(dropdownMenuButton.getText()).toContain('All');
  });


  it ('should display the correct data based on the filter for Fee Group ID', async () => {

      browser.refresh();
      const feeId = element(by.id('feeGroupId'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeId), 10000);
      feeId.sendKeys('1');

      await browser.executeScript('window.scrollTo(0, 250)');
      browser.sleep(1000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      await browser.executeScript('window.scrollTo(0, -250)');
      browser.sleep(1000);

      const tableRows = element.all(by.css('.MATable tbody tr'));
      browser.sleep(2000); // Adjust the sleep time based on your application's responsiveness
      // await browser.wait(ExpectedConditions.presenceOf(editButton), 10000);
      for (let i = 0; i < tableRows.count(); i++) {
          const cellInFirstColumn = tableRows.get(i).element(by.css('td:first-child'));
          const textInCell = await cellInFirstColumn.getText();
          expect(textInCell).toContain('1');
      }
    
  });

  it ('should display the correct data based on the filter for Fee Group Name (EN)', async () => {

      browser.refresh();
      const feeNameEn = element(by.id('feeGroupNameEn'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeNameEn), 10000);
      feeNameEn.sendKeys('Test');

      await browser.executeScript('window.scrollTo(0, 250)');
      browser.sleep(1000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000);
      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness
      for (let i = 0; i < tableRows.count(); i++) {
          const cellInFirstColumn = tableRows.get(i).element(by.css('td:nth-child(2)'));
          const textInCell = await cellInFirstColumn.getText();
          expect(textInCell).toContain('Test');
      }

  });

  it ('should display the correct data based on the filter for Fee Group Name (BM)', async () => {
      
      browser.refresh();
      const feeNameBm = element(by.id('feeGroupNameBM'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeNameBm), 10000);
      feeNameBm.sendKeys('Test');

      await browser.executeScript('window.scrollTo(0, 250)');
      browser.sleep(1000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000);
      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness
      for (let i = 0; i < tableRows.count(); i++) {
          const cellInFirstColumn = tableRows.get(i).element(by.css('td:nth-child(3)'));
          const textInCell = await cellInFirstColumn.getText();
          expect(textInCell).toContain('Test');
      }
  });

  it ('should display the correct data based on the filter for modified by', async () => {

      browser.refresh();
      const modifiedBy = element(by.id('modifiedBy'));
      await browser.wait(ExpectedConditions.elementToBeClickable(modifiedBy), 10000);
      modifiedBy.sendKeys('qxchua');

      await browser.executeScript('window.scrollTo(0, 250)');
      browser.sleep(1000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000);
      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness
      for (let i = 0; i < tableRows.count(); i++) {
          const cellInFirstColumn = tableRows.get(i).element(by.css('td:nth-child(4)'));
          const textInCell = await cellInFirstColumn.getText();
          expect(textInCell).toContain('qxchua');
      }

  });

  it ('should display the correct data based on the filter for modified date', async () => {
      
      browser.refresh();
      const modifiedDate = element(by.css('input[placeholder="Date Range Picker"]'));
      // Navigate to the page or perform any necessary setup
      const startDate = '10 Dec 2023';
      const endDate = '15 Dec 2023';
    
      // Input a valid date range in the "Date Modified Range" field
      await browser.wait(ExpectedConditions.presenceOf(modifiedDate), 10000, 'Date range input taking too long to appear');
      modifiedDate.clear();
      await modifiedDate.sendKeys(`${startDate} to ${endDate}`);
    
      await browser.executeScript('window.scrollTo(0, 250)');
      browser.sleep(1000);
    
      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness
    
      // Get all records in column 5 of the table
      const tableRows = element.all(by.css('.MATable tbody tr'));
     
      // Iterate through each record using a for loop
      const recordCount = await tableRows.count();
      for (let i = 0; i < recordCount; i++) {
        const record = tableRows.get(i).getWebElement().findElement(by.css('td:nth-child(5)'));
        const recordDate = await record.getText();
    
        // Convert the recordDate to a Date object (adjust this based on your date format)
        const recordDateObject = new Date(recordDate);
        recordDateObject.setHours(recordDateObject.getHours() + 8);

        browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness

    
        // Check if the recordDate is within the specified date range
        expect(recordDateObject >= new Date(startDate + ' 00:00:00') && recordDateObject <= new Date(endDate + ' 23:59:59')).toBe(true);

      }
  });

  it ('should display the correct data based on the filter for status', async () => {
          
      browser.refresh();

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(5000);

      const dropdownMenuButton = element(by.css('div.form-group select.form-control'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 10000, 'Drop-down button taking too long to be clickable');
      dropdownMenuButton.click();

      const dropdownItem = element(by.css('option[value="D"]'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 10000, 'Drop-down item taking too long to be clickable');
      dropdownItem.click();

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000);
      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness
      for (let i = 0; i < tableRows.count(); i++) {
          const cellInFirstColumn = tableRows.get(i).element(by.css('td:nth-child(7)'));
          const textInCell = await cellInFirstColumn.getText();
          expect(textInCell).toContain('Inactive');
      }
  });

  it ('should change the status of the fee group to active', async () => {
            
      browser.refresh();

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(5000);

      const dropdownMenuButton = element(by.css('div.form-group select.form-control'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 10000, 'Drop-down button taking too long to be clickable');
      dropdownMenuButton.click();

      const dropdownItem = element(by.css('option[value="D"]'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 10000, 'Drop-down item taking too long to be clickable');
      dropdownItem.click();

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000);
      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness

      const firstRowStatus = tableRows.get(0).element(by.className('form-check-input ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(firstRowStatus), 10000);
      firstRowStatus.click();

      browser.sleep(9000); // Adjust the sleep time based on your application's responsiveness

      const otherFirstRowStatus = tableRows.get(0).element(by.className('form-check-input ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(otherFirstRowStatus), 10000);
      expect(otherFirstRowStatus).not.toEqual(firstRowStatus);


  });

  it ('should change the status of the fee group to inactive', async () => {
              
      browser.refresh();

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(5000);

      const dropdownMenuButton = element(by.css('div.form-group select.form-control'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 10000, 'Drop-down button taking too long to be clickable');
      dropdownMenuButton.click();

      const dropdownItem = element(by.css('option[value="A"]'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 10000, 'Drop-down item taking too long to be clickable');
      dropdownItem.click();

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000);
      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness

      const firstRowStatus = tableRows.get(0).element(by.className('form-check-input ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(firstRowStatus), 10000);
      firstRowStatus.click();

      browser.sleep(9000); // Adjust the sleep time based on your application's responsiveness

      const otherFirstRowStatus = tableRows.get(0).element(by.className('form-check-input ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(otherFirstRowStatus), 10000);
      expect(otherFirstRowStatus).not.toEqual(firstRowStatus);

  });

  it('should not change active module status to inactive', async () => {
        
      browser.refresh();

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(5000);

      const dropdownMenuButton = element(by.css('div.form-group select.form-control'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownMenuButton), 10000, 'Drop-down button taking too long to be clickable');
      dropdownMenuButton.click();

      const feeId = element(by.id('feeGroupId'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeId), 10000);
      feeId.sendKeys('1');

      const dropdownItem = element(by.css('option[value="A"]'));
      await browser.wait(ExpectedConditions.elementToBeClickable(dropdownItem), 10000, 'Drop-down item taking too long to be clickable');
      dropdownItem.click();

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      const tableRows = element.all(by.css('.MATable tbody tr'));
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000);
      browser.sleep(5000); // Adjust the sleep time based on your application's responsiveness

      const firstRowStatus = tableRows.get(0).element(by.className('form-check-input ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(firstRowStatus), 10000);
      firstRowStatus.click();

      // browser.sleep(9000); // Adjust the sleep time based on your application's responsiveness

      await browser.executeScript('window.scrollTo(0, -300)');
      

      const alert = element(by.className('alert alert-warning PA-alert-box'));
      await browser.wait(ExpectedConditions.elementToBeClickable(alert), 10000);
      expect(alert.isPresent()).toBe(true);
      expect(alert.getText()).toContain('The record cannot be deactivated because it is in use by another module.');
      expect(tableRows.get(0).element(by.css('td:nth-child(7)')).getText()).toContain('Active');
  });
        
  it ('should show no results for invalid Fee Group ID', async () => {
                
      browser.refresh();

      const feeId = element(by.id('feeGroupId'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeId), 10000);
      feeId.sendKeys('1000');

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(3000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      await browser.executeScript('window.scrollTo(0, -300)');

      const alert = element(by.className('alert alert-warning PA-alert-box'));
      await browser.wait(ExpectedConditions.elementToBeClickable(alert), 10000);
      expect(alert.isPresent()).toBe(true);
      expect(alert.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness
      const tableRows = element.all(by.css('.MATable tbody tr'));
      expect(tableRows.count()).toBe(0);

  });

  it ('should show no results for invalid Fee Group Name (EN)', async () => {
                      
      browser.refresh();

      const feeNameEn = element(by.id('feeGroupNameEn'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeNameEn), 10000);
      feeNameEn.sendKeys('74ry3udb36dhu3ru');

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(3000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      await browser.executeScript('window.scrollTo(0, -300)');

      const alert = element(by.className('alert alert-warning PA-alert-box'));
      await browser.wait(ExpectedConditions.elementToBeClickable(alert), 10000);
      expect(alert.isPresent()).toBe(true);
      expect(alert.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness
      const tableRows = element.all(by.css('.MATable tbody tr'));
      expect(tableRows.count()).toBe(0);

  });

  it ('should show no results for invalid Fee Group name (BM)', async () => {
                  
      browser.refresh();

      const feeNameBm = element(by.id('feeGroupNameBM'));
      await browser.wait(ExpectedConditions.elementToBeClickable(feeNameBm), 10000);
      feeNameBm.sendKeys('74ry3udb36dhu3ru');

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(3000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      await browser.executeScript('window.scrollTo(0, -300)');

      const alert = element(by.className('alert alert-warning PA-alert-box'));
      await browser.wait(ExpectedConditions.elementToBeClickable(alert), 10000);
      expect(alert.isPresent()).toBe(true);
      expect(alert.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness
      const tableRows = element.all(by.css('.MATable tbody tr'));
      expect(tableRows.count()).toBe(0);

  } );

  it('should show no results for invalid modified by', async () => {
                    
      browser.refresh();

      const modifiedBy = element(by.id('modifiedBy'));
      await browser.wait(ExpectedConditions.elementToBeClickable(modifiedBy), 10000);
      modifiedBy.sendKeys('74ry3udb36dhu3ru');

      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(3000);

      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      await browser.executeScript('window.scrollTo(0, -300)');

      const alert = element(by.className('alert alert-warning PA-alert-box'));
      await browser.wait(ExpectedConditions.elementToBeClickable(alert), 10000);
      expect(alert.isPresent()).toBe(true);
      expect(alert.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness
      const tableRows = element.all(by.css('.MATable tbody tr'));
      expect(tableRows.count()).toBe(0);

      });

  it ('should show no results for invalid modified date', async () => {
                        
      browser.refresh();

      const modifiedDate = element(by.css('input[placeholder="Date Range Picker"]'));
      // Navigate to the page or perform any necessary setup
      const startDate = '10 Dec 0202';
      const endDate = '15 Dec 0202';
    
      // Input a valid date range in the "Date Modified Range" field
      await browser.wait(ExpectedConditions.presenceOf(modifiedDate), 10000, 'Date range input taking too long to appear');
      modifiedDate.clear();
      await modifiedDate.sendKeys(`${startDate} to ${endDate}`);
    
      await browser.executeScript('window.scrollTo(0, 300)');
      browser.sleep(1000);
    
      const filterButton = element(by.className('btn btnApply ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(filterButton), 10000);
      filterButton.click();

      await browser.executeScript('window.scrollTo(0, -300)');

      const alert = element(by.className('alert alert-warning PA-alert-box'));
      await browser.wait(ExpectedConditions.elementToBeClickable(alert), 10000);
      expect(alert.isPresent()).toBe(true);
      expect(alert.getText()).toContain('Your search did not match any records. Please try adjusting your search criteria.');

      browser.sleep(3000); // Adjust the sleep time based on your application's responsiveness
      const tableRows = element.all(by.css('.MATable tbody tr'));
      expect(tableRows.count()).toBe(0);

  });


});