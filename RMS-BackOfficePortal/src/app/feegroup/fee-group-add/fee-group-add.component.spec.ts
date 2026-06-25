const { browser, element, by, ExpectedConditions } = require('protractor');

describe('Fee Group Add', () => {
    beforeAll(async () => {
        jasmine.DEFAULT_TIMEOUT_INTERVAL = 100000000000;
        browser.waitForAngularEnabled(false); 
        await browser.manage().window().maximize();
        browser.get('https://localhost:4200/fee-group-listing');
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
        username.sendKeys('qxchua@persys-tech.com');
        const nextButton = element(by.id('idSIButton9'));
        nextButton.click();

        const password = element(by.id('i0118'));
        await browser.wait(ExpectedConditions.elementToBeClickable(password), 100000);
        password.sendKeys('!CKKcqx2003');
        const signinButton = element(by.id('idSIButton9'));
        signinButton.click();

        const yesButton = element(by.id('idSIButton9'));
        await browser.wait(ExpectedConditions.elementToBeClickable(yesButton), 100000);
        yesButton.click();

    });

    it('should add fee group successully', async () => {

        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);

        // Click the add button
        await addButton.click();

        const uniqueName = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        await feeGroupNameENInput.sendKeys(uniqueName + ' EN');
        await feeGroupNameBMInput.sendKeys(uniqueName + ' BM');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        const tableContainer = element(by.css('body > div > div.left-section73.ng-tns-c1058870932-0 > div.MATableContainer.TablePosition-relative.ng-tns-c1058870932-0.ng-star-inserted')); // Adjust the selector based on your actual table structure
        // const tableRow = element(by.css('body > div > div.left-section73.ng-tns-c1058870932-0 > div.MATableContainer.TablePosition-relative.ng-tns-c1058870932-0.ng-star-inserted > div.MALoadingContainer.ng-tns-c1058870932-0 > table > tbody'))
        
        // Wait for the success alert to be present
        const addSuccess = element(by.className('alert alert-success PA-alert-box'));
        await browser.wait(ExpectedConditions.presenceOf(addSuccess), 100000, 'Success alert taking too long to appear');

        // Wait for the table container to be present
        await browser.wait(ExpectedConditions.presenceOf(tableContainer), 100000);
        // await browser.wait(ExpectedConditions.presenceOf(tableRow), 100000);

        // Assert that the success alert is displayed correctly
        expect(await addSuccess.getText()).toContain('Added successfully!');

    });

    it('should not add fee group when Fee Group Name (EN) is left empty', async () => {

        browser.refresh();

            // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields without entering Fee Group Name (EN)
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        const uniqueNameBM = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');

        await feeGroupNameBMInput.sendKeys(uniqueNameBM + ' BM');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-text ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 10000, 'Error alert taking too long to appear');

        // Assert that the error alert is displayed correctly
        expect(await addError.getText()).toContain('Fee Group Name (EN) is required');

        // Close the box
        const closeButton = element(by.buttonText('Close'));
        await closeButton.click();

    });

    it('should not add fee group when Fee Group Name (BM) is left empty', async () => {

        browser.refresh();

        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields without entering Fee Group Name (BM)
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));

        const uniqueNameEN = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');

        await feeGroupNameENInput.sendKeys(uniqueNameEN + ' EN');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-text ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 10000, 'Error alert taking too long to appear');

        // Assert that the error alert is displayed correctly
        expect(await addError.getText()).toContain('Fee Group Name (BM) is required');

        // Close the box
        const closeButton = element(by.buttonText('Close'));
        await closeButton.click();

        // Optionally, you can assert specific validation error messages if available
        // const validationErrorBM = element(by.css('.error-text')); // Adjust the selector based on your actual structure
        // expect(await validationErrorBM.getText()).toContain('Fee Group Name (BM) is required');
    });

    it ('should not add fee group when both fields are left empty', async () => {

        browser.refresh();

        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);

        // Click the add button
        await addButton.click();

        // Submit the form without entering Fee Group Name (EN) and (BM)
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element.all(by.className('error-text ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 100000, 'Error alert taking too long to appear');

        // Assert that error messages are displayed for each mandatory field
        expect(await addError.count()).toBe(2); // Adjust the count based on the number of mandatory fields

        const errorMessageEn = addError.get(0);
        expect(await errorMessageEn.getText()).toContain('Fee Group Name (EN) is required');

        const errorMessageBm = addError.get(1);
        expect(await errorMessageBm.getText()).toContain('Fee Group Name (BM) is required');

        // Close the box
        const closeButton = element(by.buttonText('Close'));
        await closeButton.click();

    });

    it('should close the add fee group form when the close button is clicked', async () => {
        
        browser.refresh();

        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);
        const tableContainer = element(by.css('body > div > div.left-section73.ng-tns-c1058870932-0 > div.MATableContainer.TablePosition-relative.ng-tns-c1058870932-0.ng-star-inserted')); // Adjust the selector based on your actual table structure

        // Click the add button
        await addButton.click();

        // Wait for the "Add" box to be present
        const addBox = element(by.className('mat-mdc-dialog-container mdc-dialog cdk-dialog-container mdc-dialog--open')); // Adjust the selector based on your actual structure
        await browser.wait(ExpectedConditions.presenceOf(addBox), 10000);

        // Click the close button
        const closeButton = element(by.buttonText('Close'));
        await closeButton.click();

        // Wait for the "Add" box to be absent
        await browser.wait(ExpectedConditions.stalenessOf(addBox), 1000000, '"Add" box taking too long to disappear');

        // Assert that the "Add" box is not present anymore
        expect(await addBox.isPresent()).toBe(false);
        expect(await tableContainer.isPresent()).toBe(true);

    });

    it ('should not add fee group when Fee Group Name (EN) is duplicated', async () => {

        browser.refresh();

        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);

        // Click the add button
        await addButton.click();

        const uniqueName = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        await feeGroupNameENInput.sendKeys(uniqueName + ' EN duplicate');
        await feeGroupNameBMInput.sendKeys(uniqueName + ' BM duplicate');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the success alert to be present
        const addSuccess = element(by.className('alert alert-success PA-alert-box'));
        await browser.wait(ExpectedConditions.presenceOf(addSuccess), 100000, 'Success alert taking too long to appear');

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        await feeGroupNameENInput.sendKeys(uniqueName + ' EN duplicate');
        await feeGroupNameBMInput.sendKeys(uniqueName + ' BM');

        // Submit the form
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-box ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 100000, 'Error alert taking too long to appear');

        // Assert that the error alert is displayed correctly
        expect(addError.getText()).toContain('Fee Group Name (EN) is duplicate');

        // Close the box
        const closeButton = element(by.buttonText('Close'));
        await closeButton.click();
    });

    //failedddddd
    it ('should not add fee group when Fee Group Name (BM) is duplicated', async () => {

        browser.refresh();

        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);

        // Click the add button
        await addButton.click();

        const uniqueName = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        await feeGroupNameENInput.sendKeys(uniqueName + ' EN duplicate');
        await feeGroupNameBMInput.sendKeys(uniqueName + ' BM duplicate');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the success alert to be present
        const addSuccess = element(by.className('alert alert-success PA-alert-box'));
        await browser.wait(ExpectedConditions.presenceOf(addSuccess), 100000, 'Success alert taking too long to appear');

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        await feeGroupNameENInput.sendKeys(uniqueName + ' EN');
        await feeGroupNameBMInput.sendKeys(uniqueName + ' BM duplicate');

        // Submit the form
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-box ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 10000, 'Error alert taking too long to appear');

        // Assert that the error alert is displayed correctly
        expect(addError.getText()).toContain('Fee Group Name (BM) is duplicate');
        // browser.sleep(10000)

        // Close the box
        const closeButton = element(by.buttonText('Close'));
        await closeButton.click();
    });

    //failll
    it ('should not continue input when Fee Group Name (EN) is more than 50 characters', async () => {

        browser.refresh();
            
        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000000);

        browser.sleep(2000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        const uniqueName = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');
        const input = uniqueName + ' ' + uniqueName
        feeGroupNameENInput.sendKeys(input);
        feeGroupNameBMInput.sendKeys(uniqueName);

        expect(await feeGroupNameENInput.getAttribute('value')).toBe(input.substring(0, 50));
        browser.sleep(3000);


    });

    //faillll
    it ('should not continue input when Fee Group Name (BM) is more than 50 characters', async () => {

        browser.refresh();
                
        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000);

        browser.sleep(2000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        const uniqueName = 'Test Fee Group ' + new Date().toISOString().replace(/[:.]/g, '');
        const input = uniqueName + ' ' + uniqueName
        feeGroupNameENInput.sendKeys(uniqueName);
        feeGroupNameBMInput.sendKeys(input);

        expect(await feeGroupNameBMInput.getAttribute('value')).toBe(input.substring(0, 50));
        browser.sleep(3000);

});

it ('should not add fee group when Fee Group Name (EN) is empty string', async () => {

        browser.refresh();

        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000);

        browser.sleep(2000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        await feeGroupNameENInput.sendKeys('');
        await feeGroupNameBMInput.sendKeys('Test Fee Group BM');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-text ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 10000, 'Error alert taking too long to appear');

        // Assert that the error alert is displayed correctly
        expect(await addError.getText()).toContain('Fee Group Name (EN) is required');

        browser.refresh();
    });

    it ('should not add fee group when Fee Group Name (BM) is empty string', async () => {

        browser.refresh();
      
        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000);

        browser.sleep(2000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        await feeGroupNameENInput.sendKeys('Test Fee Group EN');
        await feeGroupNameBMInput.sendKeys('');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-text ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 100000, 'Error alert taking too long to appear');

        // Assert that the error alert is displayed correctly
        expect(await addError.getText()).toContain('Fee Group Name (BM) is required');

        browser.refresh();      
        });

    it ('should not add fee group when Fee Group Name (EN) is whitespace', async () => {

        browser.refresh();
        
        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000);

        browser.sleep(2000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        await feeGroupNameENInput.sendKeys(' ');
        await feeGroupNameBMInput.sendKeys('Test Fee Group BM');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-text ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 5000, 'Error alert taking too long to appear');

        const notSupposedToAppear = element(by.className('error-box ng-star-inserted'));
        expect(await notSupposedToAppear.isPresent()).toBe(false);

        // Assert that the error alert is displayed correctly
        expect(await addError.getText()).toContain('Fee Group Name (EN) is required');

        browser.refresh();
        });

    it ('should not add fee group when Fee Group Name (BM) is whitespace', async () => {

        browser.refresh();
                
        // Wait for the add button to be available
        const addButton = element(by.buttonText('Add'));
        await browser.wait(ExpectedConditions.elementToBeClickable(addButton), 10000);

        browser.sleep(2000);

        // Click the add button
        await addButton.click();

        // Fill in the form fields
        const feeGroupNameENInput = element(by.id('feeGroupNameEN'));
        const feeGroupNameBMInput = element(by.name('feeGroupNameBM'));

        await feeGroupNameENInput.sendKeys('Test Fee Group EN');
        await feeGroupNameBMInput.sendKeys(' ');

        // Submit the form
        const submitButton = element(by.buttonText('Submit'));
        await submitButton.click();

        // Wait for the error alert to be present
        const addError = element(by.className('error-text ng-star-inserted'));
        await browser.wait(ExpectedConditions.presenceOf(addError), 5000, 'Error alert taking too long to appear');

        const notSupposedToAppear = element(by.className('error-box ng-star-inserted'));
        expect(await notSupposedToAppear.isPresent()).toBe(false);

        // Assert that the error alert is displayed correctly
        expect(await addError.getText()).toContain('Fee Group Name (BM) is required');

        browser.refresh();
        });

});
