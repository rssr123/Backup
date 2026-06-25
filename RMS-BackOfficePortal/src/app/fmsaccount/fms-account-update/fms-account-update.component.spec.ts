describe('FMS Account Update', () => {
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

  it('should update fee group successfully', async () => {

      browser.refresh();

      // Wait for the edit button to be available for the first row
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000);

      // Click the edit button
      await editButton.click();

      // Wait for the "Update" box to be present
      const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
      await browser.wait(ExpectedConditions.presenceOf(updateBox), 10000, '"Update" box taking too long to appear');

      const uniqueName = 'Update Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');

      // Fill in the form fields
      const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
      const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

      await feeGroupNameENInput.clear();
      await feeGroupNameBMInput.clear();

      await feeGroupNameENInput.sendKeys(uniqueName + ' EN updated');
      await feeGroupNameBMInput.sendKeys(uniqueName + ' BM updated');

      // Submit the form
      const submitButton = element(by.buttonText('Update'));
      await submitButton.click();

      // Wait for the success alert to be present
      const updateSuccess = element(by.className('alert alert-info PA-alert-box'));
      await browser.wait(ExpectedConditions.presenceOf(updateSuccess), 9000, 'Success alert taking too long to appear');

      // Assert that the success alert is displayed correctly
      expect(await updateSuccess.getText()).toContain('Updated successfully!');
      browser.refresh();
  });

  it ('should not update fee group if Fee Group Name (EN) is duplicated', async () => {

      browser.refresh();

      // Wait for the edit button to be available for the first row
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      const tableRows = element.all(by.css('.MATable tbody tr'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000, 'Edit button taking too long to appear');

      // Get the text of the first cell in the second row
      const cellInFirstColumn = tableRows.get(1).element(by.css('td:nth-child(2)'));
      const textInCell = await cellInFirstColumn.getText();
        
      // Click the edit button
      await editButton.click();

      // Wait for the "Update" box to be present
      const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
      await browser.wait(ExpectedConditions.presenceOf(updateBox), 10000, '"Update" box taking too long to appear');

      // Fill in the form fields
      const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
      await feeGroupNameENInput.clear();

      await feeGroupNameENInput.sendKeys(textInCell);

      // Submit the form
      const submitButton = element(by.buttonText('Update'));
      await submitButton.click();

      // Wait for the error alert to be present
      const updateError = element(by.className('error-box ng-star-inserted'));
      await browser.wait(ExpectedConditions.presenceOf(updateError), 3000, 'Error alert taking too long to appear');

      // Assert that the error alert is displayed correctly
      expect(await updateError.getText()).toContain('Fee Group Name (EN) is duplicate');
    

  });

  it ('should not update fee group if Fee Group Name (BM) is duplicated', async () => {

      browser.refresh();

      // Wait for the edit button to be available for the first row
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000, 'Edit button taking too long to appear');
  
      // Click the edit button
      await editButton.click();
  
      // Wait for the "Update" box to be present
      const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
      await browser.wait(ExpectedConditions.presenceOf(updateBox), 10000, '"Update" box taking too long to appear');
  
      // Fill in the form fields
      const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));
      await feeGroupNameBMInput.clear();


      const tableRows = element.all(by.css('.MATable tbody tr'));
      // Get the text of the first cell in the second row
      const cellInFirstColumn = tableRows.get(1).element(by.css('td:nth-child(3)'));
      const textInCell = await cellInFirstColumn.getText();

      await feeGroupNameBMInput.sendKeys(textInCell);

      // Submit the form
      const submitButton = element(by.buttonText('Update'));
      await submitButton.click();
  
      // Wait for the error alert to be present
      const updateError = element(by.className('error-box ng-star-inserted'));
      await browser.wait(ExpectedConditions.presenceOf(updateError), 9000, 'Error alert taking too long to appear');
  
      // Assert that the error alert is displayed correctly
      expect(await updateError.getText()).toContain('Fee Group Name (BM) is duplicate');

  });

  //faillled
  it ('should not update fee group if Fee Group Name (EN) is whitespace', async () => {

      browser.refresh();
      
      // Wait for the edit button to be available for the first row
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000);
  
      // Click the edit button
      await editButton.click();
  
      // Wait for the "Update" box to be present
      const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
      await browser.wait(ExpectedConditions.presenceOf(updateBox), 100000, '"Update" box taking too long to appear');
  
      // Fill in the form fields
      const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
      const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));
  
      await feeGroupNameENInput.clear(); // ' '
      await feeGroupNameBMInput.clear();
  
      await feeGroupNameBMInput.sendKeys('BM to be updated');
  
      // Submit the form
      const submitButton = element(by.buttonText('Update'));
      await submitButton.click();
  
      // Wait for the error alert to be present
      const updateError = element(by.className('error-text ng-star-inserted'));
      await browser.wait(ExpectedConditions.presenceOf(updateError), 9000, 'Error alert taking too long to appear');
  
      // Assert that the error alert is displayed correctly
      expect(await updateError.getText()).toContain('Fee Group Name (EN) is required');

      browser.refresh();
  });

  //failed
  it ('should not update fee group if Fee Group Name (BM) is whitespace', async () => {

      browser.refresh();
      
      // Wait for the edit button to be available for the first row
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000);
  
      // Click the edit button
      await editButton.click();
  
      // Wait for the "Update" box to be present
      const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
      await browser.wait(ExpectedConditions.presenceOf(updateBox), 100000, '"Update" box taking too long to appear');
  
      // Fill in the form fields
      const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
      const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));
  
      await feeGroupNameENInput.clear(); // ' '
      await feeGroupNameBMInput.clear();
  
      await feeGroupNameENInput.sendKeys('EN to be updated');
  
      // Submit the form
      const submitButton = element(by.buttonText('Update'));
      await submitButton.click();
  
      // Wait for the error alert to be present
      const updateError = element(by.className('error-text ng-star-inserted'));
      await browser.wait(ExpectedConditions.presenceOf(updateError), 9000, 'Error alert taking too long to appear');
  
      // Assert that the error alert is displayed correctly
      expect(await updateError.getText()).toContain('Fee Group Name (BM) is required');

  });

  it ('should not continue input if Fee Group Name (EN) is more than 50 characters', async () => {
          
          browser.refresh();
          
          // Wait for the edit button to be available for the first row
          const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
          await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000);
      
          // Click the edit button
          await editButton.click();
      
          // Wait for the "Update" box to be present
          const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
          await browser.wait(ExpectedConditions.presenceOf(updateBox), 100000, '"Update" box taking too long to appear');
      
          // Fill in the form fields
          const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
          const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));
      
          await feeGroupNameENInput.clear();
          await feeGroupNameBMInput.clear();
      
          const uniqueName = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');
          const input = uniqueName + ' ' + uniqueName
          feeGroupNameENInput.sendKeys(uniqueName);
          feeGroupNameBMInput.sendKeys(input);

          expect(await feeGroupNameENInput.getAttribute('value')).toBe(input.substring(0, 50));
          
      });

  it ('should not continue input if Fee Group Name (BM) is more than 50 characters', async () => {
              
              browser.refresh();
              
              // Wait for the edit button to be available for the first row
              const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
              await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000);
          
              // Click the edit button
              await editButton.click();
          
              // Wait for the "Update" box to be present
              const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
              await browser.wait(ExpectedConditions.presenceOf(updateBox), 100000, '"Update" box taking too long to appear');
          
              // Fill in the form fields
              const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
              const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));
          
              await feeGroupNameENInput.clear();
              await feeGroupNameBMInput.clear();
          
              const uniqueName = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');
              const input = uniqueName + ' ' + uniqueName
              feeGroupNameBMInput.sendKeys(uniqueName);
              feeGroupNameENInput.sendKeys(input);
  
              expect(await feeGroupNameBMInput.getAttribute('value')).toBe(input.substring(0, 50));
              
          });


  it ('should close the update box when the "Cancel" button is clicked', async () => {

      browser.refresh();

      // Wait for the edit button to be available for the first row
      const editButton = element(by.className('btn btnEdit ng-tns-c1058870932-0'));
      await browser.wait(ExpectedConditions.elementToBeClickable(editButton), 10000000);

      // Click the edit button
      await editButton.click();

      // Wait for the "Update" box to be present
      const updateBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
      await browser.wait(ExpectedConditions.presenceOf(updateBox), 10000, '"Update" box taking too long to appear');

      // Click the close button
      const closeButton = element(by.buttonText('Close'));
      await closeButton.click();

      // Wait for the "Update" box to be present
      await browser.wait(ExpectedConditions.stalenessOf(updateBox), 10000, '"Update" box taking too long to disappear');

      // Assert that the "Update" box is no longer present
      expect(await updateBox.isPresent()).toBe(false);
  });
});
