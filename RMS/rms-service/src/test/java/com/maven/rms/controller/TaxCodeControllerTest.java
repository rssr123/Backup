// package com.maven.rms.controller;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.maven.rms.controllers.TaxCodeController;
// import com.maven.rms.models.TaxCd;
// import com.maven.rms.models.payload.responses.ApiResponse;
// import com.maven.rms.repositories.IStoreProcedureRepository;
// import com.maven.rms.services.AuthService;
// import com.maven.rms.utils.SystemStatus;

// import org.springframework.test.web.servlet.MockMvc;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.math.BigDecimal;
// import java.util.Base64;
// import java.util.List;

// import javax.servlet.http.HttpServletRequest;
// import javax.transaction.Transactional;

// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import com.fasterxml.jackson.databind.JavaType;

// @SpringBootTest
// @AutoConfigureMockMvc
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// public class TaxCodeControllerTest {

//     @Autowired
//     private IStoreProcedureRepository storeProcedureRepository;

//     @InjectMocks
//     private TaxCodeController taxCodeController;

//     @Autowired
//     private MockMvc mockMvc;

//     @Mock
//     private AuthService authService;

//     @Mock
//     private HttpServletRequest request;

//     @Autowired
//     private ObjectMapper objectMapper;

//     // ============================================================== Tests for Insert ==============================================================//
//     @Test
//     @Transactional
//     public void testAddTaxCodeSuccess() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         BigDecimal i_tax_pct = BigDecimal.valueOf(0.10);
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = SystemStatus.Active.getMessage();

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":" + i_tax_pct + ","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);

//         // Optionally, you can check if the record is present in the database
//         List<Object[]> records = storeProcedureRepository.
//                 sp_gettaxcode_v2(null, null, null, i_tax_cd, null, null, null, null, null, i_status);

//         assertTrue(records != null && !records.isEmpty());

//     }

//     @Test
//     @Transactional
//     public void testAddTaxCodeUnauthorizedWithoutCredentials() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         BigDecimal i_tax_pct = BigDecimal.valueOf(0.10);
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = "A";

//     // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":" + i_tax_pct + ","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testAddTaxCodeUnauthorizedWithInvalidCredentials() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         BigDecimal i_tax_pct = BigDecimal.valueOf(0.10);
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = "A";

//     // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("invalid:invpass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":" + i_tax_pct + ","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testAddTaxCodeUnauthorizedWithInvalidInput() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         BigDecimal i_tax_pct = BigDecimal.valueOf(0.10);
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = "A";

//     // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy;pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":" + i_tax_pct + ","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testAddTaxCodeInvalidFormatNegativeTaxPct() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         BigDecimal i_tax_pct_invalid = BigDecimal.valueOf(-0.15);  // Provide an invalid value for i_tax_pct
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = "A";
    
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":\"" + i_tax_pct_invalid + "\","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    
//         // Optionally, you can check if the record is present in the database
//         // Note: Since the request has an invalid format, the record should not be inserted.
//         List<Object[]> records = storeProcedureRepository.
//                 sp_gettaxcode_v2(null, null, null, i_tax_cd, null, null, null, null, null, i_status);
    
//         assertTrue(records == null || records.isEmpty());

//     }

//     @Test
//     @Transactional
//     public void testAddTaxCodeInvalidFormatDataType() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         String i_tax_pct_invalid = "ABC";  // Provide an invalid value for i_tax_pct
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = "A";
    
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":\"" + i_tax_pct_invalid + "\","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testAddTaxCodeInternalServerErrorInvalidJsonObject() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         BigDecimal i_tax_pct_invalid = BigDecimal.valueOf(0.30);  // Provide an invalid value for i_tax_pct
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = "A";
    
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":\"" + i_tax_pct_invalid + "\","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testAddTaxCodeInternalServerErrorDuplicateRecord() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "tax_cd_nm_en99";
//         String i_tax_cd_nm_bm = "tax_cd_nm_bm99";
//         BigDecimal i_tax_pct_invalid = BigDecimal.valueOf(-0.15);  // Provide an invalid value for i_tax_pct
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = "A";

//         // When
//         // First attempt to insert
//         MvcResult firstInsertResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":\"" + i_tax_pct_invalid + "\","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();

//         // Then (Optional) - Check the status code if needed
//         int firstInsertStatusCode = firstInsertResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), firstInsertStatusCode);

//         // Optionally, you can check if the record is present in the database
//         List<Object[]> firstInsertRecords = storeProcedureRepository
//                 .sp_gettaxcode_v2(null, null, null, i_tax_cd, null, null, null, null, null, i_status);
//         assertTrue(firstInsertRecords != null && !firstInsertRecords.isEmpty());

//         // When
//         // Second attempt to insert (Duplicate Record)
//         MvcResult secondInsertResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":\"" + i_tax_pct_invalid + "\","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();

//         // Then - Check the status code for the duplicate record (Expected to be an error status)
//         int secondInsertStatusCode = secondInsertResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), secondInsertStatusCode);

//     }

//     // ============================================================== Tests for Update ==============================================================//
//     @Test
//     @Transactional
//     public void testUpdateTaxCodeSuccess() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(0.15);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);

//         // Optionally, you can check if the record is updated in the database
//         List<Object[]> updatedRecords = storeProcedureRepository.sp_gettaxcode_v2(
//                 null, null, null, i_tax_cd, null, null, null, null, null, SystemStatus.Active.getMessage());

//         assertTrue(updatedRecords != null && !updatedRecords.isEmpty());

//         // Validate that the fields are updated
//         Object[] updatedRecord = updatedRecords.get(0);
//         assertEquals(updatedTaxCdNmEn, updatedRecord[2]);
//         assertEquals(updatedTaxCdNmBm, updatedRecord[3]);
//         assertEquals(updatedTaxPct, updatedRecord[4]);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeSuccessByInsertANewData() throws Exception {
//         String i_tax_cd = "tax_cd99";
//         String i_tax_cd_nm_en = "test insert";
//         String i_tax_cd_nm_bm = "cuba insert";
//         BigDecimal i_tax_pct = BigDecimal.valueOf(2.45);
//         String i_created_by = "wewong";
//         String i_modified_by = "wewong";
//         String i_status = SystemStatus.Active.getMessage();

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/addtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + i_tax_cd_nm_en + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + i_tax_cd_nm_bm + "\","
//                         + "\"i_tax_pct\":" + i_tax_pct + ","
//                         + "\"i_created_by\":\"" + i_created_by + "\","
//                         + "\"i_modified_by\":\"" + i_modified_by + "\","
//                         + "\"i_status\":\"" + i_status + "\""
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);

//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(0.15);

//         // When
//         MvcResult mvcResultUpdate = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCodeUpdate = mvcResultUpdate.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCodeUpdate);

//         // Optionally, you can check if the record is updated in the database
//         List<Object[]> updatedRecords = storeProcedureRepository.sp_gettaxcode_v2(
//                 null, null, null, i_tax_cd, null, null, null, null, null, SystemStatus.Active.getMessage());

//         assertTrue(updatedRecords != null && !updatedRecords.isEmpty());

//         // Validate that the fields are updated
//         Object[] updatedRecord = updatedRecords.get(0);
//         assertEquals(updatedTaxCdNmEn, updatedRecord[2]);
//         assertEquals(updatedTaxCdNmBm, updatedRecord[3]);
//         assertEquals(updatedTaxPct, updatedRecord[4]);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeUnauthorizedWithoutCredentials() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(0.15);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeUnauthorizedWithInvalidCredentials() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(0.15);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("wei:password".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeUnauthorizedWithInvalidInput() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd35";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(0.15);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy*pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeInvalidFormatNegativeTaxPct() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(-0.15);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeInvalidFormatDataType() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         String updatedTaxPct = "abc";

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeInternalServerError() throws Exception {
//         // Given
//         String i_tax_cd = "tax_cd99";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(0.15);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testUpdateTaxCodeInternalServerErrorInvalidRecords() throws Exception {
//         // Given
//         String i_tax_cd = "invalid";
//         String updatedTaxCdNmEn = "new english name";
//         String updatedTaxCdNmBm = "new malay name";
//         BigDecimal updatedTaxPct = BigDecimal.valueOf(0.15);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/updtaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd\":\"" + i_tax_cd + "\","
//                         + "\"i_tax_cd_nm_en\":\"" + updatedTaxCdNmEn + "\","
//                         + "\"i_tax_cd_nm_bm\":\"" + updatedTaxCdNmBm + "\","
//                         + "\"i_tax_pct\":" + updatedTaxPct
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);

//     }

// // ============================================================== Tests for Get ==============================================================//
//     @Test
//     public void testGetTaxCodeUnauthorizedWithInvalidCredentials() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(33);
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:invpass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     public void testGetTaxCodeUnauthorizedWithoutCredentials() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(33);
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_page\":" + 1
//                         + ", \"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     public void testGetTaxCodeUnauthorizedWithInvalidInput() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(33);
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy-pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     public void testGetTaxCodeITaxCdID() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(108);
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);

//     }

//     @Test
//     public void testGetTaxCodeIPageISize() throws Exception {
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_page\":" + 1
//                         + ", \"i_size\":" + 10
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);
    
//         // Optionally, you can check the content of the response if needed
//         String content = mvcResult.getResponse().getContentAsString();
    
//         // Construct the JavaType for ApiResponse<List<TaxCd>>
//         JavaType responseType = objectMapper.getTypeFactory().constructParametricType
//         (ApiResponse.class, objectMapper.getTypeFactory().constructCollectionType(List.class, TaxCd.class));
    
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<List<TaxCd>> apiResponse = objectMapper.readValue(content, responseType);
    
//         // Assert that the ApiResponse has the expected status code "00"
//         assertEquals("00", apiResponse.getHeader().getStatusCode());
    
//         // Assert that the data is not empty
//         List<TaxCd> responseData = apiResponse.getData();
//         assertNotNull(responseData);
    
//         // Assert that the number of records returned matches the expected size
//         assertEquals(10, responseData.size());

//     }
    
//     @Test
//     public void testGetTaxCodeNoDataFound() throws Exception {
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + 0
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);
    
//         // Optionally, you can check the content of the response if needed
//         String content = mvcResult.getResponse().getContentAsString();
    
//         // Construct the JavaType for ApiResponse<List<TaxCd>>
//         JavaType responseType = objectMapper.getTypeFactory().constructParametricType
//         (ApiResponse.class, objectMapper.getTypeFactory().constructCollectionType(List.class, TaxCd.class));
    
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<List<TaxCd>> apiResponse = objectMapper.readValue(content, responseType);
    
//         // Assert that the ApiResponse has the expected status code "01"
//         assertEquals("01", apiResponse.getHeader().getStatusCode());
    
//     }

//     @Test
//     public void testGetTaxCodeInvalidFormatIntegerAsFloat() throws Exception {
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_size\":" + 2.5
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    
//         // Optionally, you can check the content of the response if needed
//         String content = mvcResult.getResponse().getContentAsString();
    
//         // Construct the JavaType for ApiResponse<List<TaxCd>>
//         JavaType responseType = objectMapper.getTypeFactory().constructParametricType
//         (ApiResponse.class, objectMapper.getTypeFactory().constructCollectionType(List.class, TaxCd.class));
    
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<List<TaxCd>> apiResponse = objectMapper.readValue(content, responseType);
    
//         // Assert that the ApiResponse has the expected status code "00"
//         assertEquals("02", apiResponse.getHeader().getStatusCode());

//     }

//     @Test
//     public void testGetTaxCodeInvalidFormatIntegerAsString() throws Exception {
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_size\": \"abc\""
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

//     }

//     @Test
//     public void testGetTaxCodeInternalServerError() throws Exception {
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/gettaxcode")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_pa\":" + 1
//                         + ", \"i_sze\":" + 10
//                         + ",\"i_tax_c_id\":" + 0
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);

//     }

// // ============================================================== Tests for CheckTaxCodeExist==============================================================//
//     @Test
//     public void testCheckTaxCodeExistRecordInUseUnauthorizedWithInvalidCredentials() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(108);
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/checktaxcodeexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("invalid:invpass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     public void testCheckTaxCodeExistRecordInUseUnauthorizedWithoutCredentials() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(108);
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/checktaxcodeexist")
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     public void testCheckTaxCodeExistRecordInUseUnauthorizedWithInvalidFormat() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(108);
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/checktaxcodeexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy)pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     public void testCheckTaxCodeExistRecordInUse() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(33);
    
//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/checktaxcodeexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();
    
//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);
    
//         // Optionally, you can check the content of the response if needed
//         String content = mvcResult.getResponse().getContentAsString();
        
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<?> apiResponse = objectMapper.readValue(content, ApiResponse.class);
    
//         // Assert that the ApiResponse has the expected status code "03"
//         assertEquals("03", apiResponse.getHeader().getStatusCode());

//     }


//     @Test
//     public void testCheckTaxCodeExistSuccessResponse() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(109);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/checktaxcodeexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);

//         // Optionally, you can check the content of the response if needed
//         String content = mvcResult.getResponse().getContentAsString();
        
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<?> apiResponse = objectMapper.readValue(content, ApiResponse.class);
    
//         // Assert that the ApiResponse has the expected status code "00"
//         assertEquals("00", apiResponse.getHeader().getStatusCode());

//     }

//     @Test
//     public void testCheckTaxCodeExistInvalidFormat() throws Exception {
//         // Given
//         String existingTaxCodeId = "randomvalue";

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/checktaxcodeexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_cd_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

//     }

//     @Test
//     public void testCheckTaxCodeExistInternalServerError() throws Exception {
//         // Given
//         Long existingTaxCodeId = Long.valueOf(109);

//         // When
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tc/v1/checktaxcodeexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                         + "\"i_tax_c_id\":" + existingTaxCodeId
//                         + "}"))
//                 .andReturn();

//         // Then
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);

//         // Optionally, you can check the content of the response if needed
//         String content = mvcResult.getResponse().getContentAsString();
        
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<?> apiResponse = objectMapper.readValue(content, ApiResponse.class);
    
//         // Assert that the ApiResponse has the expected status code "99"
//         assertEquals("99", apiResponse.getHeader().getStatusCode());

//     }
// }



