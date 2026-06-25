package com.example.fms.fms.controllers;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.fms.fms.models.ARIRequest;
import com.example.fms.fms.models.ARIRequestDeserializer;
import com.example.fms.fms.models.ARIRequestDeserializerv2;
import com.example.fms.fms.models.ARIRequestv2;
import com.example.fms.fms.models.ARRRequest;
import com.example.fms.fms.models.ARRRequestDeserializer;
import com.example.fms.fms.models.ARRRequestDeserializerv2;
import com.example.fms.fms.models.ARRRequestv2;
import com.example.fms.fms.models.CreditRequest;
import com.example.fms.fms.models.CreditRequestDeserializer;
import com.example.fms.fms.models.CreditRequestDeserializerV2;
import com.example.fms.fms.models.CreditRequestV2;
import com.example.fms.fms.models.DebitRequest;
import com.example.fms.fms.models.DebitRequestDeserializer;
import com.example.fms.fms.models.DebitRequestDeserializerV2;
import com.example.fms.fms.models.DebitRequestV2;
import com.example.fms.fms.models.JournalRequest;
import com.example.fms.fms.models.JournalRequestDeserializer;
import com.example.fms.fms.models.ARCRequest;
import com.example.fms.fms.models.ARCRequestDeserializer;
import com.example.fms.fms.models.ARVRequest;
import com.example.fms.fms.models.ARVRequestDeserializer;
import com.example.fms.fms.models.APIRequest;
import com.example.fms.fms.models.APIRequestDeserializer;

import com.example.fms.fms.utils.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@RestController
@RequestMapping("/api/fms/v1")
public class FMSController {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Map<String, Object> generate200Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("AttributeEXTSYSTEM", "RMS");
        response.put("ReferenceNbr", UUID.randomUUID().toString());
        response.put("CustomerOrder", cust_order);
        response.put("Status", "200");
        response.put("Message", "Record created");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generate200ARRResponse(String cust_order, String batch_no) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("AttributeEXTSYSTEM", "RMS");
        response.put("ReferenceNbr", UUID.randomUUID().toString());
        response.put("PaymentRef", batch_no);
        response.put("Status", "200");
        response.put("Message", "Record created");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    // #region
    // public Map<String, Object> generate200FMSARIResponse(String custOrder) {
    //     Map<String, Object> response = new LinkedHashMap<>();
    //     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    //     String currentDate = formatter.format(new Date());

    //     // Top-level properties
    //     response.put("id", UUID.randomUUID().toString());
    //     response.put("rowNumber", 1);
    //     response.put("note", "");

    //     // Amount object
    //     Map<String, Object> amount = new HashMap<>();
    //     amount.put("value", 0.0);
    //     response.put("Amount", amount);

    //     // Balance object
    //     Map<String, Object> balance = new HashMap<>();
    //     balance.put("value", 0.0);
    //     response.put("Balance", balance);

    //     // BillingPrinted object
    //     Map<String, Object> billingPrinted = new HashMap<>();
    //     billingPrinted.put("value", false);
    //     response.put("BillingPrinted", billingPrinted);

    //     // CreatedDateTime object
    //     Map<String, Object> createdDateTime = new HashMap<>();
    //     createdDateTime.put("value", currentDate);
    //     response.put("CreatedDateTime", createdDateTime);

    //     // Currency object
    //     Map<String, Object> currency = new HashMap<>();
    //     currency.put("value", "MYR");
    //     response.put("Currency", currency);

    //     // Customer object
    //     Map<String, Object> customer = new HashMap<>();
    //     customer.put("value", "C000002");
    //     response.put("Customer", customer);

    //     // CustomerOrder object
    //     Map<String, Object> customerOrder = new HashMap<>();
    //     customerOrder.put("value", custOrder);
    //     response.put("CustomerOrder", customerOrder);

    //     // Date object
    //     Map<String, Object> date = new HashMap<>();
    //     date.put("value", "2024-09-10T00:00:00+08:00");
    //     response.put("Date", date);

    //     // Description object
    //     Map<String, Object> description = new HashMap<>();
    //     description.put("value", "TEST BILLING RMS");
    //     response.put("Description", description);

    //     // Details array
    //     List<Map<String, Object>> details = new ArrayList<>();
    //     Map<String, Object> detail = new LinkedHashMap<>();
    //     detail.put("id", UUID.randomUUID().toString());
    //     detail.put("rowNumber", 1);
    //     detail.put("note", null);

    //     // Account object inside details
    //     Map<String, Object> account = new HashMap<>();
    //     account.put("value", "R111120004");
    //     detail.put("Account", account);

    //     // Add more nested objects in details
    //     detail.put("Amount", Collections.singletonMap("value", 0.0));
    //     detail.put("Branch", Collections.singletonMap("value", "B0100"));
    //     detail.put("DeferralCode", new HashMap<>());
    //     detail.put("DiscountAmount", Collections.singletonMap("value", 0.0));
    //     detail.put("ExtendedPrice", Collections.singletonMap("value", 0.0));
    //     detail.put("InventoryID", new HashMap<>());
    //     detail.put("LastModifiedDateTime", Collections.singletonMap("value", currentDate));
    //     detail.put("LineNbr", Collections.singletonMap("value", 1));
    //     detail.put("ProjectTask", new HashMap<>());
    //     detail.put("Qty", Collections.singletonMap("value", 1.0));
    //     detail.put("Subaccount", Collections.singletonMap("value", "000000"));
    //     detail.put("TaxCategory", new HashMap<>());
    //     detail.put("TermEndDate", new HashMap<>());
    //     detail.put("TermStartDate", new HashMap<>());
    //     detail.put("TransactionDescription", Collections.singletonMap("value", "TEST RMS"));
    //     detail.put("UnitPrice", Collections.singletonMap("value", 0.0));
    //     detail.put("UOM", new HashMap<>());
    //     detail.put("custom", new HashMap<>());

    //     // Links in details
    //     Map<String, Object> links = new HashMap<>();
    //     links.put("files:put", "/FMS/entity/GRP9Default/1/files/PX.Objects.AR.ARInvoiceEntry/Transactions/"
    //             + detail.get("id") + "/{filename}");
    //     detail.put("_links", links);

    //     // Files in details
    //     detail.put("files", new ArrayList<>());

    //     // Add detail to details list
    //     details.add(detail);
    //     response.put("Details", details);

    //     // DueDate object
    //     Map<String, Object> dueDate = new HashMap<>();
    //     dueDate.put("value", "2024-10-10T00:00:00+08:00");
    //     response.put("DueDate", dueDate);

    //     // Hold object
    //     response.put("Hold", Collections.singletonMap("value", true));

    //     // LastModifiedDateTime object
    //     response.put("LastModifiedDateTime", Collections.singletonMap("value", currentDate));

    //     // LinkARAccount object
    //     response.put("LinkARAccount", Collections.singletonMap("value", "A125320003"));

    //     // LinkARSubAccount object
    //     response.put("LinkARSubAccount", Collections.singletonMap("value", "000000"));

    //     // LinkBranch object
    //     response.put("LinkBranch", Collections.singletonMap("value", "B0100"));

    //     // PostPeriod object
    //     response.put("PostPeriod", Collections.singletonMap("value", "092024"));

    //     // Project object
    //     response.put("Project", Collections.singletonMap("value", "X"));

    //     // ReferenceNbr object
    //     response.put("ReferenceNbr", Collections.singletonMap("value", "IN24000028"));

    //     // Status object
    //     response.put("Status", Collections.singletonMap("value", "On Hold"));

    //     // TaxTotal object
    //     response.put("TaxTotal", Collections.singletonMap("value", 0.0));

    //     // Terms object
    //     response.put("Terms", Collections.singletonMap("value", "30"));

    //     // Type object
    //     response.put("Type", Collections.singletonMap("value", "Invoice"));

    //     // Custom object
    //     Map<String, Object> custom = new HashMap<>();
    //     Map<String, Object> currentDocument = new HashMap<>();
    //     Map<String, Object> attributeSYSNAME = new HashMap<>();
    //     attributeSYSNAME.put("type", "CustomStringField");
    //     attributeSYSNAME.put("value", "RMS");
        
    //     Map<String, Object> attributeGENPDF = new HashMap<>();
    //     attributeGENPDF.put("type", "CustomIntField");
    //     attributeGENPDF.put("value", true);
        
    //     currentDocument.put("AttributeSYSNAME", attributeSYSNAME);
    //     currentDocument.put("AttributeGENPDF", attributeGENPDF);
    //     custom.put("CurrentDocument", currentDocument);
    //     response.put("custom", custom);

    //     // Document object (example PDF value)
    //     response.put("document", Collections.singletonMap("value", "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ZW50IC0yMTINCi9DYXBIZWlnaHQgNzE2DQovRmxhZ3MgMzINCi9Gb250QkJveCBbIC02MjggLTM3NiAyMDAwIDEwNTZdDQovRm9udE5hbWUgL0hlbHZldGljYS1Cb2xkDQovSXRhbGljQW5nbGUgMA0KL1N0ZW1WIDANCi9YSGVpZ2h0IDUxOQ0KPj4NCmVuZG9iag0KNSAwIG9iag0KPDwNCi9UeXBlIC9Gb250DQovU3VidHlwZSAvVHlwZTENCi9CYXNlRm9udCAvSGVsdmV0aWNhDQovRmlyc3RDaGFyIDANCi9MYXN0Q2hhciAyNTUNCi9FbmNvZGluZyAvV2luQW5zaUVuY29kaW5nDQovRm9udERlc2NyaXB0b3IgNiAwIFINCj4+DQplbmRvYmoNCjYgMCBvYmoNCjw8DQovVHlwZSAvRm9udERlc2NyaXB0b3INCi9Bc2NlbnQgOTA1DQovRGVzY2VudCAtMjEyDQovQ2FwSGVpZ2h0IDcxNg0KL0ZsYWdzIDMyDQovRm9udEJCb3ggWyAtNjY1IC0zMjUgMjAwMCAxMDQwXQ0KL0ZvbnROYW1lIC9IZWx2ZXRpY2ENCi9JdGFsaWNBbmdsZSAwDQovU3RlbVYgMA0KL1hIZWlnaHQgNTE5DQo+Pg0KZW5kb2JqDQo5IDAgb2JqDQo8PA0KL1Byb2R1Y2VyKFNVUlVIQU5KQVlBIFNZQVJJS0FUIE1BTEFZU0lBKQ0KL0NyZWF0b3IoU1VSVUhBTkpBWUEgU1lBUklLQVQgTUFMQVlTSUEpDQovQ3JlYXRpb25EYXRlKEQ6MjAyNDExMjUxNjU5MzQrMDgnMDApDQovTW9kRGF0ZShEOjIwMjQxMTI1MTY1OTM0KzA4JzAwKQ0KPj4NCmVuZG9iag0KMTAgMCBvYmoNCjw8DQovVHlwZS9DYXRhbG9nDQovUGFnZU1vZGUgL1VzZU5vbmUNCi9QYWdlcyAxIDAgUg0KPj4NCmVuZG9iag0KeHJlZg0KMCAxMQ0KMDAwMDAwMDAwMCA2NTUzNSBmDQowMDAwMDAxNTIxIDAwMDAwIG4NCjAwMDAwMDAwMTUgMDAwMDAgbg0KMDAwMDAwMTU4NCAwMDAwMCBuDQowMDAwMDAxNzQ2IDAwMDAwIG4NCjAwMDAwMDE5NTAgMDAwMDAgbg0KMDAwMDAwMjEwNyAwMDAwMCBuDQowMDAwMDAwMjQxIDAwMDAwIG4NCjAwMDAwMDAxMzAgMDAwMDAgbg0KMDAwMDAwMjMwNiAwMDAwMCBuDQowMDAwMDAyNDg3IDAwMDAwIG4NCnRyYWlsZXINCjw8DQovSW5mbyA5IDAgUg0KL1Jvb3QgMTAgMCBSDQovU2l6ZSAxMQ0KPj4NCnN0YXJ0eHJlZg0KMjU2Mg0KJSVFT0YNCg=="));

    //     // _links object
    //     Map<String, Object> responseLinks = new HashMap<>();
    //     responseLinks.put("self", "/FMS/entity/GRP9Default/1/Invoice/" + response.get("id"));
    //     responseLinks.put("files:put", "/FMS/entity/GRP9Default/1/files/PX.Objects.AR.ARInvoiceEntry/Document/"
    //             + response.get("id") + "/{filename}");
    //     response.put("_links", responseLinks);

    //     // Files object
    //     response.put("files", new ArrayList<>());

    //     return response;
    // }
    // #endregion

    public Map<String, Object> generate200FMSARIResponse(String custOrder) {
        Map<String, Object> response = new LinkedHashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String currentDate = formatter.format(new Date());

        response.put("id", UUID.randomUUID().toString());
        response.put("rowNumber", 1);
        response.put("note", Collections.singletonMap("value", null));

        response.put("Amount", Collections.singletonMap("value", 100));
        response.put("CustomerID", Collections.singletonMap("value", "C000001"));
        response.put("CustomerOrder", Collections.singletonMap("value", custOrder));
        response.put("Date", Collections.singletonMap("value", "2025-02-21T00:00:00+08:00"));
        response.put("Description", Collections.singletonMap("value", "TEST RMS API"));
        response.put("ReferenceNbr", Collections.singletonMap("value", "IN25000335"));
        response.put("Type", Collections.singletonMap("value", "Invoice"));

        // AttributeSYSNAME
        Map<String, Object> attributeSYSNAME = new HashMap<>();
        attributeSYSNAME.put("type", "CustomStringField");
        attributeSYSNAME.put("value", "RMS");

        // AttributeGENPDF
        Map<String, Object> attributeGENPDF = new HashMap<>();
        attributeGENPDF.put("type", "CustomIntField");
        attributeGENPDF.put("value", true);

        // CurrentDocument
        Map<String, Object> currentDocument = new HashMap<>();
        currentDocument.put("AttributeSYSNAME", attributeSYSNAME);
        currentDocument.put("AttributeGENPDF", attributeGENPDF);

        // Custom
        Map<String, Object> custom = new HashMap<>();
        custom.put("CurrentDocument", currentDocument);
        response.put("custom", custom);

        // Document
        Map<String, Object> document = new HashMap<>();
        document.put("value", "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jgF7Duph7erRop92AjV1ufr3OOWK3lW5GAgs9iNkk8hftrxpo5r4zf3V5nH9cJx2ZYOX9saEI7SHzF97FJqQC8ngg3hG+OGF/9+bKOnKBL+Eb0x4JuEdHU/kI5hYJl3i+NcPYs5liUm6y9Xn6pRfsLkG7Vnkf45/NDwNCmVuZHN0cmVhbQ0KZW5kb2JqDQoyIDAgb2JqDQo8PA0KL0NvdW50IDENCi9UeXBlL1BhZ2VzDQovS2lkcyBbMyAwIFJdDQo+Pg0KZW5kb2JqDQo0IDAgb2JqDQo8PA0KL1R5cGUgL0ZvbnQNCi9TdWJ0eXBlIC9UeXBlMQ0KL0Jhc2VGb250IC9IZWx2ZXRpY2EtQm9sZA0KL0ZpcnN0Q2hhciAwDQovTGFzdENoYXIgMjU1DQovRW5jb2RpbmcgL1dpbkFuc2lFbmNvZGluZw0KL0ZvbnREZXNjcmlwdG9yIDUgMCBSDQo+Pg0KZW5kb2JqDQo1IDAgb2JqDQo8PA0KL1R5cGUgL0ZvbnREZXNjcmlwdG9yDQovQXNjZW50IDkwNQ0KL0Rlc2NlbnQgLTIxMg0KL0NhcEhlaWdodCA3MTYNCi9GbGFncyAzMg0KL0ZvbnRCQm94IFsgLTYyOCAtMzc2IDIwMDAgMTA1Nl0NCi9Gb250TmFtZSAvSGVsdmV0aWNhLUJvbGQNCi9JdGFsaWNBbmdsZSAwDQovU3RlbVYgMA0KL1hIZWlnaHQgNTE5DQo+Pg0KZW5kb2JqDQo2IDAgb2JqDQo8PA0KL1R5cGUgL0ZvbnQNCi9TdWJ0eXBlIC9UeXBlMQ0KL0Jhc2VGb250IC9IZWx2ZXRpY2ENCi9GaXJzdENoYXIgMA0KL0xhc3RDaGFyIDI1NQ0KL0VuY29kaW5nIC9XaW5BbnNpRW5jb2RpbmcNCi9Gb250RGVzY3JpcHRvciA3IDAgUg0KPj4NCmVuZG9iag0KNyAwIG9iag0KPDwNCi9UeXBlIC9Gb250RGVzY3JpcHRvcg0KL0FzY2VudCA5MDUNCi9EZXNjZW50IC0yMTINCi9DYXBIZWlnaHQgNzE2DQovRmxhZ3MgMzINCi9Gb250QkJveCBbIC02NjUgLTMyNSAyMDAwIDEwNDBdDQovRm9udE5hbWUgL0hlbHZldGljYQ0KL0l0YWxpY0FuZ2xlIDANCi9TdGVtViAwDQovWEhlaWdodCA1MTkNCj4+DQplbmRvYmoNCjEwIDAgb2JqDQo8PA0KL1Byb2R1Y2VyKFNVUlVIQU5KQVlBIFNZQVJJS0FUIE1BTEFZU0lBKQ0KL0NyZWF0b3IoU1VSVUhBTkpBWUEgU1lBUklLQVQgTUFMQVlTSUEpDQovQ3JlYXRpb25EYXRlKEQ6MDAwMTAxMDEwMDAwMDArMDgnMDApDQovTW9kRGF0ZShEOjAwMDEwMTAxMDAwMDAwKzA4JzAwKQ0KPj4NCmVuZG9iag0KMTEgMCBvYmoNCjw8DQovVHlwZS9DYXRhbG9nDQovTWV0YWRhdGEgMSAwIFINCi9QYWdlTW9kZSAvVXNlTm9uZQ0KL1BhZ2VzIDIgMCBSDQo+Pg0KZW5kb2JqDQoxIDAgb2JqDQo8PA0KL1N1YnR5cGUgL1hNTA0KL1R5cGUgL01ldGFkYXRhDQovTGVuZ3RoIDUzNg0KPj4NCnN0cmVhbQ0KPD94cGFja2V0IGJlZ2luPSIiIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4NCjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDcuMS1jMDAwIDc5LmNiN2M1YTEsIDIwMjIvMDQvMTQtMDU6MjI6MzUgICAgICAgICI+DQogIDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+DQogICAgPHJkZjpEZXNjcmlwdGlvbiB4bWxuczpwZGY9Imh0dHA6Ly9ucy5hZG9iZS5jb20vcGRmLzEuMy8iIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIgeG1sbnM6cGRmYWlkPSJodHRwOi8vd3d3LmFpaW0ub3JnL3BkZmEvbnMvaWQvIiB4bWxuczpwZGZ4PSJodHRwOi8vbnMuYWRvYmUuY29tL3BkZngvMS4zLyIgeG1sbnM6cHg9Imh0dHA6Ly93d3cuYWN1bWF0aWNhLmNvbS8iPg0KICAgIDwvcmRmOkRlc2NyaXB0aW9uPg0KICA8L3JkZjpSREY+DQo8L3g6eG1wbWV0YT4NCjw/eHBhY2tldCBlbmQ9InciPz4NCmVuZHN0cmVhbQ0KZW5kb2JqDQp4cmVmDQowIDEyDQowMDAwMDAwMDAwIDY1NTM1IGYNCjAwMDAwMDMwMDIgMDAwMDAgbg0KMDAwMDAwMTk0MyAwMDAwMCBuDQowMDAwMDAwMDE1IDAwMDAwIG4NCjAwMDAwMDIwMDYgMDAwMDAgbg0KMDAwMDAwMjE2OCAwMDAwMCBuDQowMDAwMDAyMzcyIDAwMDAwIG4NCjAwMDAwMDI1MjkgMDAwMDAgbg0KMDAwMDAwMDI0MSAwMDAwMCBuDQowMDAwMDAwMTMwIDAwMDAwIG4NCjAwMDAwMDI3MjggMDAwMDAgbg0KMDAwMDAwMjkxMCAwMDAwMCBuDQp0cmFpbGVyDQo8PA0KL0luZm8gMTAgMCBSDQovUm9vdCAxMSAwIFINCi9TaXplIDEyDQovSUQgWzw1Njc4RjAzOTA5OURFREZDMTgzQ0M3MDdENzVFNDk1Qz4gPDU2NzhGMDM5MDk5REVERkMxODNDQzcwN0Q3NUU0OTVDPl0NCj4+DQpzdGFydHhyZWYNCjM2MjkNCiUlRU9GDQo=" ); // example base64
        response.put("document", document);

        // _links
        Map<String, Object> responseLinks = new HashMap<>();
        responseLinks.put("self", "/FMS/entity/INTEGRATION/1/Invoices/" + response.get("id"));
        responseLinks.put("files_put", "/FMS/entity/INTEGRATION/1/files/PX.Objects.AR.ARInvoiceEntry/Document/"
                + response.get("id") + "/{filename}");
        response.put("_links", responseLinks);
        return response;
    }

    private Map<String, Object> generate200CRMemoResponse(String custOrderValue) {
        Map<String, Object> response = new LinkedHashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String currentDate = formatter.format(new Date());
    
        response.put("id", UUID.randomUUID().toString());
        response.put("rowNumber", 1);
        response.put("note", Collections.singletonMap("value", null));
        response.put("Amount", Collections.singletonMap("value", 100));
        response.put("CustomerID", Collections.singletonMap("value", "C000001"));
        response.put("CustomerOrder", Collections.singletonMap("value", custOrderValue));
        response.put("Date", Collections.singletonMap("value", currentDate));
        response.put("Description", Collections.singletonMap("value", "TEST CREDIT MEMO RMS"));
        response.put("ReferenceNbr", Collections.singletonMap("value", "CN25000036"));
        response.put("Type", Collections.singletonMap("value", "Credit Memo"));
    
        Map<String, Object> attributeSYSNAME = new HashMap<>();
        attributeSYSNAME.put("type", "CustomStringField");
        attributeSYSNAME.put("value", "RMS");
    
        Map<String, Object> attributeGENPDF = new HashMap<>();
        attributeGENPDF.put("type", "CustomIntField");
        attributeGENPDF.put("value", true);
    
        Map<String, Object> currentDocument = new HashMap<>();
        currentDocument.put("AttributeSYSNAME", attributeSYSNAME);
        currentDocument.put("AttributeGENPDF", attributeGENPDF);
    
        Map<String, Object> custom = new HashMap<>();
        custom.put("CurrentDocument", currentDocument);
        response.put("custom", custom);
    
        response.put("document", Collections.singletonMap("value", "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A/Yc1MPa1cNFP+0Eaupy9e9xyhW9q3IxEFjsR8gmkb9sedtGNfGb+6vN4/rhOO3KBi/tjQlHaA+Zv/YoNCEXksEH8ZTwwwv/vzhR0pUJfgnfmPBMwjs6nsiHMLFMusTx7x/EnMsSk3SXq8/VKb9gcw3as8j/YDw0mA0KZW5kc3RyZWFtDQplbmRvYmoNCjIgMCBvYmoNCjw8DQovQ291bnQgMQ0KL1R5cGUvUGFnZXMNCi9LaWRzIFszIDAgUl0NCj4+DQplbmRvYmoNCjQgMCBvYmoNCjw8DQovVHlwZSAvRm9udA0KL1N1YnR5cGUgL1R5cGUxDQovQmFzZUZvbnQgL0hlbHZldGljYS1Cb2xkDQovRmlyc3RDaGFyIDANCi9MYXN0Q2hhciAyNTUNCi9FbmNvZGluZyAvV2luQW5zaUVuY29kaW5nDQovRm9udERlc2NyaXB0b3IgNSAwIFINCj4+DQplbmRvYmoNCjUgMCBvYmoNCjw8DQovVHlwZSAvRm9udERlc2NyaXB0b3INCi9Bc2NlbnQgOTA1DQovRGVzY2VudCAtMjEyDQovQ2FwSGVpZ2h0IDcxNg0KL0ZsYWdzIDMyDQovRm9udEJCb3ggWyAtNjI4IC0zNzYgMjAwMCAxMDU2XQ0KL0ZvbnROYW1lIC9IZWx2ZXRpY2EtQm9sZA0KL0l0YWxpY0FuZ2xlIDANCi9TdGVtViAwDQovWEhlaWdodCA1MTkNCj4+DQplbmRvYmoNCjYgMCBvYmoNCjw8DQovVHlwZSAvRm9udA0KL1N1YnR5cGUgL1R5cGUxDQovQmFzZUZvbnQgL0hlbHZldGljYQ0KL0ZpcnN0Q2hhciAwDQovTGFzdENoYXIgMjU1DQovRW5jb2RpbmcgL1dpbkFuc2lFbmNvZGluZw0KL0ZvbnREZXNjcmlwdG9yIDcgMCBSDQo+Pg0KZW5kb2JqDQo3IDAgb2JqDQo8PA0KL1R5cGUgL0ZvbnREZXNjcmlwdG9yDQovQXNjZW50IDkwNQ0KL0Rlc2NlbnQgLTIxMg0KL0NhcEhlaWdodCA3MTYNCi9GbGFncyAzMg0KL0ZvbnRCQm94IFsgLTY2NSAtMzI1IDIwMDAgMTA0MF0NCi9Gb250TmFtZSAvSGVsdmV0aWNhDQovSXRhbGljQW5nbGUgMA0KL1N0ZW1WIDANCi9YSGVpZ2h0IDUxOQ0KPj4NCmVuZG9iag0KMTAgMCBvYmoNCjw8DQovUHJvZHVjZXIoU1VSVUhBTkpBWUEgU1lBUklLQVQgTUFMQVlTSUEpDQovQ3JlYXRvcihTVVJVSEFOSkFZQSBTWUFSSUtBVCBNQUxBWVNJQSkNCi9DcmVhdGlvbkRhdGUoRDowMDAxMDEwMTAwMDAwMCswOCcwMCkNCi9Nb2REYXRlKEQ6MDAwMTAxMDEwMDAwMDArMDgnMDApDQo+Pg0KZW5kb2JqDQoxMSAwIG9iag0KPDwNCi9UeXBlL0NhdGFsb2cNCi9NZXRhZGF0YSAxIDAgUg0KL1BhZ2VNb2RlIC9Vc2VOb25lDQovUGFnZXMgMiAwIFINCj4+DQplbmRvYmoNCjEgMCBvYmoNCjw8DQovU3VidHlwZSAvWE1MDQovVHlwZSAvTWV0YWRhdGENCi9MZW5ndGggNTM2DQo+Pg0Kc3RyZWFtDQo8P3hwYWNrZXQgYmVnaW49IiIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/Pg0KPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNy4xLWMwMDAgNzkuY2I3YzVhMSwgMjAyMi8wNC8xNC0wNToyMjozNSAgICAgICAgIj4NCiAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4NCiAgICA8cmRmOkRlc2NyaXB0aW9uIHhtbG5zOnBkZj0iaHR0cDovL25zLmFkb2JlLmNvbS9wZGYvMS4zLyIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczpwZGZhaWQ9Imh0dHA6Ly93d3cuYWlpbS5vcmcvcGRmYS9ucy9pZC8iIHhtbG5zOnBkZng9Imh0dHA6Ly9ucy5hZG9iZS5jb20vcGRmeC8xLjMvIiB4bWxuczpweD0iaHR0cDovL3d3dy5hY3VtYXRpY2EuY29tLyI+DQogICAgPC9yZGY6RGVzY3JpcHRpb24+DQogIDwvcmRmOlJERj4NCjwveDp4bXBtZXRhPg0KPD94cGFja2V0IGVuZD0idyI/Pg0KZW5kc3RyZWFtDQplbmRvYmoNCnhyZWYNCjAgMTINCjAwMDAwMDAwMDAgNjU1MzUgZg0KMDAwMDAwMzAwMSAwMDAwMCBuDQowMDAwMDAxOTQyIDAwMDAwIG4NCjAwMDAwMDAwMTUgMDAwMDAgbg0KMDAwMDAwMjAwNSAwMDAwMCBuDQowMDAwMDAyMTY3IDAwMDAwIG4NCjAwMDAwMDIzNzEgMDAwMDAgbg0KMDAwMDAwMjUyOCAwMDAwMCBuDQowMDAwMDAwMjQxIDAwMDAwIG4NCjAwMDAwMDAxMzAgMDAwMDAgbg0KMDAwMDAwMjcyNyAwMDAwMCBuDQowMDAwMDAyOTA5IDAwMDAwIG4NCnRyYWlsZXINCjw8DQovSW5mbyAxMCAwIFINCi9Sb290IDExIDAgUg0KL1NpemUgMTINCi9JRCBbPDU2NzhGMDM5MDk5REVERkMxODNDQzcwN0Q3NUU0OTVDPiA8NTY3OEYwMzkwOTlERURGQzE4M0NDNzA3RDc1RTQ5NUM+XQ0KPj4NCnN0YXJ0eHJlZg0KMzYyOA0KJSVFT0YNCg=="));
        Map<String, String> links = new HashMap<>();
        links.put("self", "/FMS/entity/INTEGRATION/1/Invoices/" + response.get("id"));
        links.put("files:put", "/FMS/entity/INTEGRATION/1/files/PX.Objects.AR.ARInvoiceEntry/Document/"
                + response.get("id") + "/{filename}");
        response.put("_links", links);
    
        return response;
    }

    private Map<String, Object> generate200DRMemoResponse(String custOrderValue) {
        Map<String, Object> response = new LinkedHashMap<>();

        // Use fixed expected date format
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String currentDate = formatter.format(new Date());

        response.put("id", UUID.randomUUID().toString());
        response.put("rowNumber", 1);
        response.put("note", Collections.singletonMap("value", null));
        response.put("Amount", Collections.singletonMap("value", 100));
        response.put("CustomerID", Collections.singletonMap("value", "C000001"));
        response.put("CustomerOrder", Collections.singletonMap("value", custOrderValue));
        response.put("Date", Collections.singletonMap("value", currentDate));
        response.put("Description", Collections.singletonMap("value", "TEST DEBIT MEMO RMS"));
        response.put("ReferenceNbr", Collections.singletonMap("value", "DN25000035"));
        response.put("Type", Collections.singletonMap("value", "Debit Memo"));
    
        // Custom fields
        Map<String, Object> attributeSYSNAME = new HashMap<>();
        attributeSYSNAME.put("type", "CustomStringField");
        attributeSYSNAME.put("value", "RMS");
    
        Map<String, Object> attributeGENPDF = new HashMap<>();
        attributeGENPDF.put("type", "CustomIntField");
        attributeGENPDF.put("value", true);
    
        Map<String, Object> currentDocument = new HashMap<>();
        currentDocument.put("AttributeSYSNAME", attributeSYSNAME);
        currentDocument.put("AttributeGENPDF", attributeGENPDF);
    
        Map<String, Object> custom = new HashMap<>();
        custom.put("CurrentDocument", currentDocument);
        response.put("custom", custom);
    
        // Static PDF base64 (truncated for brevity – keep your full one in actual code)
        response.put("document", Collections.singletonMap("value", "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DmqEYMeAPQf1sHb1cNFPO4Gaulz9e5xyRe+qXAwEFvsRsknkL1vetlFN/Ob+avO4fjhOu7LBS3tjwhHaQ+avPQpNyIVk8EE8Jfzwwv8vTpR0ZYJfwjcmPJPwjo4n8iFMLJMucfz7BzHnssQk3eXqc3XKL9hcg/Ys8j8yTzSgDQplbmRzdHJlYW0NCmVuZG9iag0KMiAwIG9iag0KPDwNCi9Db3VudCAxDQovVHlwZS9QYWdlcw0KL0tpZHMgWzMgMCBSXQ0KPj4NCmVuZG9iag0KNCAwIG9iag0KPDwNCi9UeXBlIC9Gb250DQovU3VidHlwZSAvVHlwZTENCi9CYXNlRm9udCAvSGVsdmV0aWNhLUJvbGQNCi9GaXJzdENoYXIgMA0KL0xhc3RDaGFyIDI1NQ0KL0VuY29kaW5nIC9XaW5BbnNpRW5jb2RpbmcNCi9Gb250RGVzY3JpcHRvciA1IDAgUg0KPj4NCmVuZG9iag0KNSAwIG9iag0KPDwNCi9UeXBlIC9Gb250RGVzY3JpcHRvcg0KL0FzY2VudCA5MDUNCi9EZXNjZW50IC0yMTINCi9DYXBIZWlnaHQgNzE2DQovRmxhZ3MgMzINCi9Gb250QkJveCBbIC02MjggLTM3NiAyMDAwIDEwNTZdDQovRm9udE5hbWUgL0hlbHZldGljYS1Cb2xkDQovSXRhbGljQW5nbGUgMA0KL1N0ZW1WIDANCi9YSGVpZ2h0IDUxOQ0KPj4NCmVuZG9iag0KNiAwIG9iag0KPDwNCi9UeXBlIC9Gb250DQovU3VidHlwZSAvVHlwZTENCi9CYXNlRm9udCAvSGVsdmV0aWNhDQovRmlyc3RDaGFyIDANCi9MYXN0Q2hhciAyNTUNCi9FbmNvZGluZyAvV2luQW5zaUVuY29kaW5nDQovRm9udERlc2NyaXB0b3IgNyAwIFINCj4+DQplbmRvYmoNCjcgMCBvYmoNCjw8DQovVHlwZSAvRm9udERlc2NyaXB0b3INCi9Bc2NlbnQgOTA1DQovRGVzY2VudCAtMjEyDQovQ2FwSGVpZ2h0IDcxNg0KL0ZsYWdzIDMyDQovRm9udEJCb3ggWyAtNjY1IC0zMjUgMjAwMCAxMDQwXQ0KL0ZvbnROYW1lIC9IZWx2ZXRpY2ENCi9JdGFsaWNBbmdsZSAwDQovU3RlbVYgMA0KL1hIZWlnaHQgNTE5DQo+Pg0KZW5kb2JqDQoxMCAwIG9iag0KPDwNCi9Qcm9kdWNlcihTVVJVSEFOSkFZQSBTWUFSSUtBVCBNQUxBWVNJQSkNCi9DcmVhdG9yKFNVUlVIQU5KQVlBIFNZQVJJS0FUIE1BTEFZU0lBKQ0KL0NyZWF0aW9uRGF0ZShEOjAwMDEwMTAxMDAwMDAwKzA4JzAwKQ0KL01vZERhdGUoRDowMDAxMDEwMTAwMDAwMCswOCcwMCkNCj4+DQplbmRvYmoNCjExIDAgb2JqDQo8PA0KL1R5cGUvQ2F0YWxvZw0KL01ldGFkYXRhIDEgMCBSDQovUGFnZU1vZGUgL1VzZU5vbmUNCi9QYWdlcyAyIDAgUg0KPj4NCmVuZG9iag0KMSAwIG9iag0KPDwNCi9TdWJ0eXBlIC9YTUwNCi9UeXBlIC9NZXRhZGF0YQ0KL0xlbmd0aCA1MzYNCj4+DQpzdHJlYW0NCjw/eHBhY2tldCBiZWdpbj0iIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+DQo8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA3LjEtYzAwMCA3OS5jYjdjNWExLCAyMDIyLzA0LzE0LTA1OjIyOjM1ICAgICAgICAiPg0KICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPg0KICAgIDxyZGY6RGVzY3JpcHRpb24geG1sbnM6cGRmPSJodHRwOi8vbnMuYWRvYmUuY29tL3BkZi8xLjMvIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOnBkZmFpZD0iaHR0cDovL3d3dy5haWltLm9yZy9wZGZhL25zL2lkLyIgeG1sbnM6cGRmeD0iaHR0cDovL25zLmFkb2JlLmNvbS9wZGZ4LzEuMy8iIHhtbG5zOnB4PSJodHRwOi8vd3d3LmFjdW1hdGljYS5jb20vIj4NCiAgICA8L3JkZjpEZXNjcmlwdGlvbj4NCiAgPC9yZGY6UkRGPg0KPC94OnhtcG1ldGE+DQo8P3hwYWNrZXQgZW5kPSJ3Ij8+DQplbmRzdHJlYW0NCmVuZG9iag0KeHJlZg0KMCAxMg0KMDAwMDAwMDAwMCA2NTUzNSBmDQowMDAwMDAzMDA2IDAwMDAwIG4NCjAwMDAwMDE5NDcgMDAwMDAgbg0KMDAwMDAwMDAxNSAwMDAwMCBuDQowMDAwMDAyMDEwIDAwMDAwIG4NCjAwMDAwMDIxNzIgMDAwMDAgbg0KMDAwMDAwMjM3NiAwMDAwMCBuDQowMDAwMDAyNTMzIDAwMDAwIG4NCjAwMDAwMDAyNDEgMDAwMDAgbg0KMDAwMDAwMDEzMCAwMDAwMCBuDQowMDAwMDAyNzMyIDAwMDAwIG4NCjAwMDAwMDI5MTQgMDAwMDAgbg0KdHJhaWxlcg0KPDwNCi9JbmZvIDEwIDAgUg0KL1Jvb3QgMTEgMCBSDQovU2l6ZSAxMg0KL0lEIFs8NTY3OEYwMzkwOTlERURGQzE4M0NDNzA3RDc1RTQ5NUM+IDw1Njc4RjAzOTA5OURFREZDMTgzQ0M3MDdENzVFNDk1Qz5dDQo+Pg0Kc3RhcnR4cmVmDQozNjMzDQolJUVPRg0K")); // Use full PDF base64 in real code
    
        // Links
        Map<String, String> links = new HashMap<>();
        links.put("self", "/FMS/entity/INTEGRATION/1/Invoices/" + response.get("id"));
        links.put("files_put", "/FMS/entity/INTEGRATION/1/files/PX.Objects.AR.ARInvoiceEntry/Document/" + response.get("id") + "/{filename}");
        response.put("_links", links);
    
        return response;
    }
    

    Map<String, Object> generate400Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("AttributeEXTSYSTEM", "RMS");
        response.put("ReferenceNbr", UUID.randomUUID().toString());
        response.put("CustomerOrder", cust_order);
        response.put("Status", "400");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generate500Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("AttributeEXTSYSTEM", "RMS");
        response.put("ReferenceNbr", UUID.randomUUID().toString());
        response.put("CustomerOrder", cust_order);
        response.put("Status", "500");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateARC200Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        // response.put("AttributeEXTSYSTEM", "RMS");
        // response.put("ReferenceNbr", UUID.randomUUID().toString());
        response.put("UsrIdentityNbr", cust_order);
        response.put("CustomerID", "CID"+cust_order);
        response.put("CustomerName", "PABLO DOLLAH SDN BHD");
        response.put("AddressLine1", "1 JALAN SEJARAH");
        response.put("AddressLine2", "TAMAN SEJARAH");
        response.put("AddressLine3", null);
        response.put("Email", "ssotest@ssm.com.my");
        response.put("Phone", "0123456789");
        response.put("ContactName", "MS TEST");
        response.put("PostalCode", "56000");
        response.put("CountryName", "Malaysia");
        response.put("StateName", "WP KUALA LUMPUR - WP KUALA LUMPUR");
        response.put("City", "KUALA LUMPUR");
        response.put("CustomerStatus", "Active");

        response.put("Status", "200");
        response.put("Message", "Record created");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateARC400Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        // response.put("AttributeEXTSYSTEM", "RMS");
        // response.put("ReferenceNbr", UUID.randomUUID().toString());
        response.put("UsrIdentityNbr", cust_order);
        response.put("CustomerID", "");
        response.put("Status", "400");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateARC500Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        // response.put("AttributeEXTSYSTEM", "RMS");
        // response.put("ReferenceNbr", UUID.randomUUID().toString());
        response.put("UsrIdentityNbr", cust_order);
        response.put("CustomerID", "");
        response.put("Status", "500");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateARV200Response(String ReferenceNbrValue) {
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, String> typeMap = new LinkedHashMap<>();
        typeMap.put("value", "Payment");
        response.put("Type", typeMap);

        Map<String, String> referenceNbrMap = new LinkedHashMap<>();
        referenceNbrMap.put("value", ReferenceNbrValue);
        response.put("ReferenceNbr", referenceNbrMap);

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("value", "Payment Successfully Cancelled");
        response.put("Message", messageMap);

        return response;
    }

    Map<String, Object> generateARV400Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("AttributeEXTSYSTEM", "RMS");
        // response.put("ReferenceNbr", UUID.randomUUID().toString());
        // response.put("CustomerOrder", cust_order);
        response.put("Status", "400");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateARV500Response(String cust_order) {
        Map<String, Object> response = new LinkedHashMap<>();
        // response.put("AttributeEXTSYSTEM", "RMS");
        // response.put("ReferenceNbr", UUID.randomUUID().toString());
        // response.put("CustomerOrder", cust_order);
        response.put("Status", "500");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateAPI200Response(String vendorRefValue, String descriptionValue) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("AttributeEXTSYSTEM", "RMS");
        response.put("ReferenceNbr", "BILL-"+descriptionValue);
        response.put("VendorRef", vendorRefValue);
        response.put("Status", "200");
        response.put("Message", "Record created");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateAPI400Response(String vendorRefValue) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("Status", "200");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    Map<String, Object> generateAPI500Response(String vendorRefValue) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("Status", "500");
        response.put("Message", "Error Message");
        response.put("Date", formatter.format(new Date()).toString());
        return response;
    }

    @PostMapping(value = "/ari")
    public ResponseEntity<Map<String, Object>> ari(HttpServletRequest request) {

        String requestBody = null;

        Map<String, Object> response;
        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ARIRequest.class, new ARIRequestDeserializer());
            mapper.registerModule(module);
            ARIRequest ariRequest = mapper.readValue(requestBody, ARIRequest.class);
            String linkBranchValue = ariRequest.getLinkBranch().getValue();
            String custOrderValue = ariRequest.getCustomerOrder().getValue();
            // String branchVal = Integer.parseInt(linkBranchValue);
            String branchVal = linkBranchValue;


            switch (branchVal) {
                case "HQ":
                    response = generate200Response(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
                case "000":
                    response = generate200Response(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
                case "1":
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case "2":
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generate400Response(custOrderValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    // 250320: Roy- enhance default should be 200 response
                    response = generate200Response(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/ariV2")
    public ResponseEntity<Map<String, Object>> ariV2(HttpServletRequest request) {

        String requestBody = null;

        Map<String, Object> response;
        try {

            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ARIRequestv2.class, new ARIRequestDeserializerv2());
            mapper.registerModule(module);
            ARIRequestv2 ariRequest = mapper.readValue(requestBody, ARIRequestv2.class);
            String linkBranchValue = ariRequest.getLinkBranch().getValue();
            String custOrderValue = ariRequest.getCustomerOrder().getValue();
            // String branchVal = Integer.parseInt(linkBranchValue);
            String branchVal = linkBranchValue;
            
            switch (branchVal) {
                case "HQ":
                    if(ariRequest.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !ariRequest.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200FMSARIResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
                case "000":
                    if(ariRequest.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !ariRequest.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200FMSARIResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);

                case "1":
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case "2":
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generate400Response(custOrderValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    // 250320: Roy- enhance default should be 200 response
                    if(ariRequest.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !ariRequest.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200FMSARIResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/debit")
    public ResponseEntity<Map<String, Object>> debit(HttpServletRequest request) {

        String requestBody = null;
        Map<String, Object> response = new HashMap<>();

        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(DebitRequest.class, new DebitRequestDeserializer());
            mapper.registerModule(module);
            DebitRequest debitRequest = mapper.readValue(requestBody, DebitRequest.class);
            String linkBranchValue = debitRequest.getLinkBranch().getValue();
            String custOrderValue = debitRequest.getCustomerOrder().getValue();
            // int branchVal = Integer.parseInt(linkBranchValue);

            switch (linkBranchValue) {
                case "0":
                    response = generate200Response(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);

                case "1":
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case "2":
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generate400Response(custOrderValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    // 250320: Roy- enhance default should be 200 response
                    response = generate200Response(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/debitV2")
    public ResponseEntity<Map<String, Object>> debitV2(HttpServletRequest request) {

        String requestBody = null;

        Map<String, Object> response;
        try {

            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(DebitRequestV2.class, new DebitRequestDeserializerV2());
            mapper.registerModule(module);
            DebitRequestV2 debitRequestV2 = mapper.readValue(requestBody, DebitRequestV2.class);
            String linkBranchValue = debitRequestV2.getLinkBranch().getValue();
            String custOrderValue = debitRequestV2.getCustomerOrder().getValue();
            // String branchVal = Integer.parseInt(linkBranchValue);
            String branchVal = linkBranchValue;
            
            switch (branchVal) {
                case "HQ":
                    if(debitRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !debitRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200DRMemoResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
                case "00000":
                    if(debitRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !debitRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200DRMemoResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);

                case "1":
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case "2":
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // 250320: Roy- enhance default should be 200 response
                    if(debitRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !debitRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200DRMemoResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/credit")
    public ResponseEntity<Map<String, Object>> credit(HttpServletRequest request) {

        String requestBody = null;
        Map<String, Object> response = new HashMap<>();

        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(CreditRequest.class, new CreditRequestDeserializer());
            mapper.registerModule(module);
            System.out.println(requestBody);
            CreditRequest creditRequest = mapper.readValue(requestBody, CreditRequest.class);
            String linkBranchValue = creditRequest.getLinkBranch().getValue();
            String custOrderValue = creditRequest.getCustomerOrder().getValue();
            int branchVal = Integer.parseInt(linkBranchValue);

            switch (branchVal) {
                case 0:
                    response = generate200Response(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);

                case 1:
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case 2:
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generate400Response(custOrderValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    // 250320: Roy- enhance default should be 200 response
                    response = generate200Response(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/creditV2")
    public ResponseEntity<Map<String, Object>> creditV2(HttpServletRequest request) {

        String requestBody = null;

        Map<String, Object> response;
        try {

            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(CreditRequestV2.class, new CreditRequestDeserializerV2());
            mapper.registerModule(module);
            CreditRequestV2 creditRequestV2 = mapper.readValue(requestBody, CreditRequestV2.class);
            String linkBranchValue = creditRequestV2.getLinkBranch().getValue();
            String custOrderValue = creditRequestV2.getCustomerOrder().getValue();
            // String branchVal = Integer.parseInt(linkBranchValue);
            String branchVal = linkBranchValue;
            
            switch (branchVal) {
                case "HQ":
                    if(creditRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !creditRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200CRMemoResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
                case "00000":
                    if(creditRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !creditRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200CRMemoResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);

                case "1":
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case "2":
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // 250320: Roy- enhance default should be 200 response
                    if(creditRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF() == null || !creditRequestV2.getCustom().getCurrentDocument().getAttributeGENPDF().getValue()){
                        response = generate200Response(custOrderValue);
                        return APIResponse.SuccessResponseExternal(response);
                    }
                    response = generate200CRMemoResponse(custOrderValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/arr")
    public ResponseEntity<Map<String, Object>> arr(HttpServletRequest request) {

        String requestBody = null;
        Map<String, Object> response = new HashMap<>();

        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ARRRequest.class, new ARRRequestDeserializer());
            mapper.registerModule(module);
            ARRRequest arrRequest = mapper.readValue(requestBody, ARRRequest.class);
            String branchValue = arrRequest.getBranch().getValue();

            String custOrderValue = arrRequest.getCustomerID().getValue();
            String batch_no = arrRequest.getPaymentRef().getValue();
            int branchVal = Integer.parseInt(branchValue);

            switch (branchVal) {
                case 0:
                    response = generate200ARRResponse(custOrderValue, batch_no);
                    return APIResponse.SuccessResponseExternal(response);

                case 1:
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case 2:
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generate400Response(custOrderValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    // 250320: Roy- enhance default should be 200 response
                    response = generate200ARRResponse(custOrderValue, batch_no);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/arrV2")
    public ResponseEntity<Map<String, Object>> arrV2(HttpServletRequest request) {

        String requestBody = null;
        Map<String, Object> response = new HashMap<>();

        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ARRRequestv2.class, new ARRRequestDeserializerv2());
            mapper.registerModule(module);
            ARRRequestv2 arrRequest = mapper.readValue(requestBody, ARRRequestv2.class);
            String branchValue = arrRequest.getBranch().getValue();

            String custOrderValue = arrRequest.getCustomerID().getValue();
            int branchVal = Integer.parseInt(branchValue);
            String batch_no = arrRequest.getPaymentRef().getValue();

            switch (branchVal) {
                case 0:
                    response = generate200ARRResponse(custOrderValue, batch_no);
                    return APIResponse.SuccessResponseExternal(response);

                case 1:
                    response = generate400Response(custOrderValue);
                    return APIResponse.InvalidFormatExternal(response);

                case 2:
                    response = generate500Response(custOrderValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generate400Response(custOrderValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    // 250320: Roy- enhance default should be 200 response
                    response = generate200ARRResponse(custOrderValue, batch_no);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/journal")
    public ResponseEntity<Map<String, Object>> journal(HttpServletRequest request) {

        String requestBody = null;
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(JournalRequest.class, new JournalRequestDeserializer());
            mapper.registerModule(module);
            JournalRequest journalRequest = mapper.readValue(requestBody, JournalRequest.class);
            String moduleValue = journalRequest.getModule().getValue();
            String attrExtRefNbr = journalRequest.getCustom().getBatchModule().getAttributeEXTREFNBR().getValue();

            if (moduleValue.equals("GL")) {
                response.put("AttributeEXTSYSTEM", "RMS");
                response.put("BatchNbr", UUID.randomUUID().toString());
                response.put("ExtRefNbr", attrExtRefNbr);
                response.put("Status", "200");
                response.put("Message", "Record created");
                response.put("Date", formatter.format(new Date()).toString());
                return APIResponse.SuccessResponseExternal(response);
            } else {
                response.put("AttributeEXTSYSTEM", "RMS");
                response.put("BatchNbr", UUID.randomUUID().toString());
                response.put("ExtRefNbr", attrExtRefNbr);
                response.put("Status", "400");
                response.put("Message", "Error Msg");
                response.put("Date", formatter.format(new Date()).toString());
                return APIResponse.InvalidFormatExternal(response);
            }

        } catch (NumberFormatException e) {
            response.put("AttributeEXTSYSTEM", "RMS");
            response.put("BatchNbr", UUID.randomUUID().toString());
            response.put("ExtRefNbr", "N/A");
            response.put("Status", "400");
            response.put("Message", "Error Msg");
            response.put("Date", formatter.format(new Date()).toString());
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response.put("AttributeEXTSYSTEM", "RMS");
            response.put("BatchNbr", UUID.randomUUID().toString());
            response.put("ExtRefNbr", "N/A");
            response.put("Status", "500");
            response.put("Message", "Error Msg");
            response.put("Date", formatter.format(new Date()).toString());
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/arc")
    public ResponseEntity<Map<String, Object>> arc(HttpServletRequest request) {

        String requestBody = null;
        
        Map<String, Object> response;
        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ARCRequest.class, new ARCRequestDeserializer());
            mapper.registerModule(module);
            ARCRequest arcRequest = mapper.readValue(requestBody, ARCRequest.class);

            String custClassValue = arcRequest.getCustomerClass().getValue();
            String usrIdentityNbrValue = arcRequest.getCustom().getCurrentCustomer().getUsrIdentityNbr().getValue();

            // int custClassVal = Integer.parseInt(custClassValue);

            switch (custClassValue) {
                case "LOCAL":
                    response = generateARC200Response(usrIdentityNbrValue);
                    return APIResponse.SuccessResponseExternal(response);

                case "LOCAL1":
                    response = generateARC400Response(usrIdentityNbrValue);
                    return APIResponse.InvalidFormatExternal(response);

                case "LOCAL2":
                    response = generateARC500Response(usrIdentityNbrValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generate400Response(custOrderValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    // 250320: Roy- enhance default should be 200 response
                    response = generateARC200Response(usrIdentityNbrValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generateARC400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generateARC500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/arv")
    public ResponseEntity<Map<String, Object>> arv(HttpServletRequest request) {
        String requestBody = null;
        Map<String, Object> response = new HashMap<>();

        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            System.out.println("Request Body: " + requestBody);

            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ARVRequest.class, new ARVRequestDeserializer());
            mapper.registerModule(module);

            ARVRequest arvRequest = mapper.readValue(requestBody, ARVRequest.class);

            // Validate deserialized fields
            if (arvRequest == null || arvRequest.getCustom() == null || arvRequest.getCustom().getDocument() == null
                    || arvRequest.getCustom().getDocument().getUsrVoidReason() == null) {
                throw new IllegalArgumentException("Invalid or incomplete request payload");
            }

            String ursrVoidReasonValue = arvRequest.getCustom().getDocument().getUsrVoidReason().getValue();
            if (ursrVoidReasonValue == null) {
                throw new IllegalArgumentException("UsrVoidReason value is null");
            }

            ursrVoidReasonValue = ursrVoidReasonValue.trim();

            // Extract ReferenceNbr
            if (arvRequest.getEntity() == null || arvRequest.getEntity().getReferenceNbr() == null) {
                throw new IllegalArgumentException("Entity or ReferenceNbr is null");
            }
            String referenceNbrValue = arvRequest.getEntity().getReferenceNbr().getValue();
            if (referenceNbrValue == null) {
                throw new IllegalArgumentException("ReferenceNbr value is null");
            }

            // Parse UsrVoidReason value
            // int ursrVoidReasonVal = Integer.parseInt(ursrVoidReasonValue);

            // Switch Case Logic
            switch (ursrVoidReasonValue) {
                case "RC1":
                    response = generateARV200Response(referenceNbrValue);
                    return APIResponse.SuccessResponseExternal(response);

                case "RC2":
                    response = generateARV400Response(referenceNbrValue);
                    return APIResponse.InvalidFormatExternal(response);

                case "RC3":
                    response = generateARV500Response(referenceNbrValue);
                    return APIResponse.InternalServerErrorExternal(response);

                default:
                    // response = generateARV400Response(referenceNbrValue);
                    // return APIResponse.InvalidFormatExternal(response);
                    response = generateARV200Response(referenceNbrValue);
                    return APIResponse.SuccessResponseExternal(response);
            }

        } catch (NumberFormatException e) {
            response = generateARV400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);

        } catch (Exception e) {
            response = generateARV500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    @PostMapping(value = "/api")
    public ResponseEntity<Map<String, Object>> api(HttpServletRequest request) {
        String requestBody = null;
        Map<String, Object> response = new HashMap<>();
        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(APIRequest.class, new APIRequestDeserializer());
            mapper.registerModule(module);
            APIRequest apiRequest = mapper.readValue(requestBody, APIRequest.class);

            // Debug statements
            System.out.println("Custom: " + apiRequest.getCustom());
            System.out.println("Document: " + apiRequest.getCustom().getDocument());
            System.out.println("AttributeSYSNAME: " + apiRequest.getCustom().getDocument().getAttributeSYSNAME());
            System.out.println("Value: " + apiRequest.getCustom().getDocument().getAttributeSYSNAME().getValue());

            // Access the InvoiceHeader object
            if (apiRequest.getInvoiceHeader() != null) {
                for (APIRequest.InvoiceHeader invoiceHeader : apiRequest.getInvoiceHeader()) {
                    System.out.println("vendorRef: " + invoiceHeader.getVendorRef().getValue());
                }

                String moduleValue = apiRequest.getCustom().getDocument().getAttributeSYSNAME().getValue();
                String vendorRefValue = apiRequest.getInvoiceHeader().get(0).getVendorRef().getValue();
                String descriptionValue = apiRequest.getVendorInfo().get(0).getPaymentInstructions().get(0).getDescription().getValue();
                System.out.println("descriptionValue: " + descriptionValue);

                switch (moduleValue) {
                    case "RMS":
                        response = generateAPI200Response(vendorRefValue, descriptionValue);
                        return APIResponse.SuccessResponseExternal(response);
                    case "RMS1":
                        response = generateAPI400Response(vendorRefValue);
                        return APIResponse.InvalidFormatExternal(response);
                    case "RMS2":
                        response = generateAPI500Response(vendorRefValue);
                        return APIResponse.InternalServerErrorExternal(response);
                    default:
                        // response = generateAPI400Response(vendorRefValue);
                        // return APIResponse.InvalidFormatExternal(response);
                        response = generateAPI200Response(vendorRefValue, descriptionValue);
                        return APIResponse.SuccessResponseExternal(response);
                }
            } else {
                response = generate400Response("InvoiceHeader is missing");
                return APIResponse.InvalidFormatExternal(response);
            }
        } catch (NumberFormatException e) {
            response = generate400Response("N/A");
            return APIResponse.InvalidFormatExternal(response);
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging
            response = generate500Response("N/A");
            return APIResponse.InternalServerErrorExternal(response);
        }
    }

    ////refrence number take from description (prefix BILL-)

}