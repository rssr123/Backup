// package com.maven.rms;

// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertTrue;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import java.util.Base64;
// import java.util.List;

// import javax.servlet.http.HttpServletRequest;

// import org.joda.time.DateTime;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.transaction.annotation.Transactional;

// import com.fasterxml.jackson.databind.JavaType;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.maven.rms.controllers.FeeGroupController;
// import com.maven.rms.models.FeeGrp;
// import com.maven.rms.models.payload.responses.ApiResponse;
// import com.maven.rms.repositories.IStoreProcedureRepository;
// import com.maven.rms.services.AuthService;
// import com.maven.rms.utils.SystemStatus;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ExtendWith(MockitoExtension.class)
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// public class FeeGroupControllerTest {

//     @Autowired
//     private IStoreProcedureRepository storeProcedureRepository;

//     @Mock
//     private AuthService authService;


//     @InjectMocks
//     private FeeGroupController feeGroupController;

//     @Autowired
//     private MockMvc mockMvc;

//     @Mock
//     private HttpServletRequest request;

//     @Autowired
//     private ObjectMapper objectMapper;

//     // Given current date time so it will be unique
//     // Convert date time to string to pass in as input
//     private DateTime currentDate = DateTime.now();
//     String I_FEE_GRP_NM_EN = new DateTime( currentDate ).toString("yyyy-MM-dd HH:mm:ss");
//     String I_FEE_GRP_NM_BM = new DateTime( currentDate ).toString("yyyy-MM-dd HH:mm:ss");
//     String I_CREATED_BY = "qxchua";
//     String I_MODIFIED_BY = "qxchua";
//     String I_STATUS = SystemStatus.Active.getMessage(); // "A"
//     Integer I_PAGE = 1;
//     Integer I_SIZE = 1;

// //================================================================================================================

// //Get Fee Group Testing Starts Here

//     @Test
//     public void testSpGetFeeGroupV2Unauthorized() throws Exception{

//         String i_fee_grp_nm_en = "test";

    
//         // When invalid credentials are entered
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/getfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("123:456".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 +"\"i_page\":\""+I_PAGE +"\","
//                 +"\"i_size\":\""+I_SIZE +"\","
//                 +"\"i_fee_grp_nm_en\":\""+i_fee_grp_nm_en+"\""
//                 +"}"))
//                 .andReturn();

//         // Response should be 401 UNAUTHORIZED
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), mvcResult.getResponse().getStatus());
   
//     }

//      @Test
//     public void testSpGetFeeGroupV2SuccessResponse() throws Exception{
//         String i_fee_grp_nm_en = "55%";

    
//         // When valid credentials are entered and valid input 
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/getfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 +"\"i_page\":\""+I_PAGE +"\","
//                 +"\"i_size\":\""+I_SIZE +"\","
//                 +"\"i_fee_grp_nm_en\":\""+i_fee_grp_nm_en+"\""
//                 +"}"))
//                 .andReturn();

//         // check the content of the response 
//         String content = mvcResult.getResponse().getContentAsString();
//         JavaType responseType = objectMapper.getTypeFactory().constructParametricType
//         (ApiResponse.class, objectMapper.getTypeFactory().constructCollectionType(List.class, FeeGrp.class));
   
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<List<FeeGrp>> apiResponse = objectMapper.readValue(content, responseType);
   
//         // Assert that the ApiResponse has the expected status code "00"
//         assertEquals("00", apiResponse.getHeader().getStatusCode());
   
//         // Assert that the data is not empty
//         List<FeeGrp> responseData = apiResponse.getData();
//         assertNotNull(responseData);

//         // Assert that fee en name equals to "55%"
//         FeeGrp feeGrp = responseData.get(0);
//         assertEquals("55%", feeGrp.getFee_grp_nm_bm());
//         assertEquals("55% ", feeGrp.getFee_grp_nm_en());
        
//     }

//     @Test
//     public void testSpGetFeeGroupV2NoDataFound() throws Exception{
//         Long i_fee_grp_id = Long.valueOf(0);

    
//         // When valid credentials are entered but with data that is not in database
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/getfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 +"\"i_page\":\""+I_PAGE +"\","
//                 +"\"i_size\":\""+I_SIZE +"\","
//                 +"\"i_fee_grp_id\":\""+i_fee_grp_id+"\""
//                 +"}"))
//                 .andReturn();

//          // check the content of the response
//         String content = mvcResult.getResponse().getContentAsString();
//         JavaType responseType = objectMapper.getTypeFactory().constructParametricType
//         (ApiResponse.class, objectMapper.getTypeFactory().constructCollectionType(List.class, FeeGrp.class));
   
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<List<FeeGrp>> apiResponse = objectMapper.readValue(content, responseType);
   
//         // Assert that the ApiResponse has the expected status code "01", which means no data found in ApiResponse class
//         assertEquals("01", apiResponse.getHeader().getStatusCode());
   
//     }

//     @Test
//     public void testSpGetFeeGroupV2InvalidFormatSize() throws Exception{
//         String i_fee_size = "invalid_size";
//         String i_fee_grp_nm_en = "55%";

    
//         // When valid credentials are entered but with invalid input (i_fee_size is supposed to be integer, but string is entered)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/getfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 +"\"i_page\":\""+I_PAGE +"\","
//                 +"\"i_size\":\""+ i_fee_size +"\","
//                 +"\"i_fee_grp_nm_en\":\""+i_fee_grp_nm_en+"\""
//                 +"}"))
//                 .andReturn();

//         // Assert that the Http status code is 400 BAD REQUEST
//         assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

    
//     }

//     @Test
//     public void testSpGetFeeGroupV2InvalidFormatId() throws Exception{
//         String i_fee_grp_id = "invalid_id";

//         // When valid credentials are entered but with invalid input (i_fee_grp_id is supposed to be integer, but string is entered)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/getfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 +"\"i_page\":\""+I_PAGE +"\","
//                 +"\"i_size\":\""+ I_SIZE +"\","
//                 +"\"i_fee_grp_id\":\""+i_fee_grp_id+"\""
//                 +"}"))
//                 .andReturn();

//         // Assert that the Http status code is 400 BAD REQUEST
//         assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
   
//     }

//     @Test
//     public void testSpGetFeeGroupV2InvalidFormatPage() throws Exception{
//         String i_fee_page = "invalid_page";
//         Long i_fee_grp_id = Long.valueOf(1);
    
//         // When valid credentials are entered but with invalid input (i_fee_page is supposed to be integer, but string is entered)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/getfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 +"\"i_page\":\""+i_fee_page +"\","
//                 +"\"i_size\":\""+ I_SIZE +"\","
//                 +"\"i_fee_grp_id\":\""+i_fee_grp_id+"\""
//                 +"}"))
//                 .andReturn();

//         // Assert that the http status code is 400 BAD REQUEST
//         assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
   
//     }

//     @Test
//     public void testSpGetFeeGroupV2InternalServerError() throws Exception{
//         String i_fee_grp_nm_en = "5%";

    
//         // When valid credentials are entered but with invalid JSON type(initial: i_fee_grp_nm_en)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/getfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 +"\"i_fee_grp_nm\":\""+i_fee_grp_nm_en+"\""
//                 +"}"))
//                 .andReturn();

//         // Assert that the http status code is 500 INTERNAL SERVER ERROR
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult.getResponse().getStatus());
   
//     }


// //Get Fee Group Testing Ends Here
// //================================================================================================================
// //Add Fee Group Testing Starts Here

//     @Test
//     @Transactional
//     public void testAddFeeCodeUnauthorized() throws Exception {

//         // When invalid credentials are entered
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/addfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:p".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_created_by\":\"" + I_CREATED_BY + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 401 UNAUTHORIZED
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);
        
//     }


//     @Test
//     @Transactional
//     public void testAddFeeCodeSuccess() throws Exception {
 
//         // When valid credentials are entered and valid input
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/addfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_created_by\":\"" + I_CREATED_BY + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 200 OK
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);


//     }

//     @Test
//     @Transactional
//     public void testSpInsFeeGroupInternalServerErrorNegativeResult() throws Exception{

//         //To prevent repeatition with previous test/data
//         String i_fee_grp_nm_en = I_FEE_GRP_NM_EN+" repeat";
//         String i_fee_grp_nm_bm = I_FEE_GRP_NM_BM+" repeat";



//         // When first valid item is added
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/addfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_nm_en\":\"" + i_fee_grp_nm_en + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + i_fee_grp_nm_bm + "\","
//                 + "\"i_created_by\":\"" + I_CREATED_BY + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Check status code inside the ApiResponse
//         assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

//         //Add same item
//         MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/addfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_nm_en\":\"" + i_fee_grp_nm_en + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + i_fee_grp_nm_bm + "\","
//                 + "\"i_created_by\":\"" + I_CREATED_BY + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}")).andReturn();

//         // Check status code inside the ApiResponse
//         // Should be 500 INTERNAL SERVER ERROR because repeated data is not allowed
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult2.getResponse().getStatus());

//     }

//     @Test
//     @Transactional
//     public void testSpInsFeeGroupInternalServerError() throws Exception{

//         // When valid credentials are entered but with invalid JSON type(initial: i_fee_grp_nm_en and i_fee_grp_nm_bm)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/addfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_nm\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_created\":\"" + I_CREATED_BY + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Check status code inside the ApiResponse
//         // Should be 500 INTERNAL SERVER ERROR because invalid JSON type is entered
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult.getResponse().getStatus());

//     }


// //"Add Fee Group Testing Ends Here"
// //================================================================================================================

// //"Update Fee Group Testing Starts Here"

//     @Test
//     @Transactional
//     public void testSpUpdFeeGroupUnauthorized() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(31);
 
//         // When invalid credentials are entered
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/updatefeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\","
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 401 UNAUTHORIZED
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);
        
//     }   

//     @Test
//     @Transactional
//     public void testSpUpdFeeGroupSuccessResponseExisting() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(31);
//         String i_fee_grp_nm_en= I_FEE_GRP_NM_EN + " updated";
//         String i_fee_grp_nm_bm = I_FEE_GRP_NM_BM + " updated";
        
//         // When valid credentials are entered and valid input
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/updatefeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\","
//                 + "\"i_fee_grp_nm_en\":\"" + i_fee_grp_nm_en + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + i_fee_grp_nm_bm + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 200 OK
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);
        
//     }

//     @Test
//     //@Transactional
//     public void testSpUpdFeeGroupSuccessResponseNew() throws Exception {

//         // When valid credentials are entered and valid input
//          MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/addfeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_created_by\":\"" + I_CREATED_BY + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//          String content = mvcResult.getResponse().getContentAsString();
   
        
//         // Assuming you have a method to deserialize the response content to ApiResponse
//         ApiResponse<Integer> apiResponse = objectMapper.readValue(content, ApiResponse.class);

//         // Get the id of the newly added record
//         Integer i_fee_grp_id2 = apiResponse.getData();

//         // Record to be updated
//         String i_fee_grp_nm_en2 = "fee_example_updated "+ I_FEE_GRP_NM_EN;
//         String i_fee_grp_nm_bm2 = "fee_example_updated " + I_FEE_GRP_NM_BM ;
 
//         // When valid credentials are entered and valid input (update the record)
//         MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/updatefeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id2 + "\","
//                 + "\"i_fee_grp_nm_en\":\"" + i_fee_grp_nm_en2 + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + i_fee_grp_nm_bm2 + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 200 OK, result updated
//         int statusCode = mvcResult2.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);
        
//     }

    
//     @Test
//     @Transactional
//     public void testSpUpdFeeGroupInternalServerErrorNegativeResult() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(1000);
  
//         // When valid credentials are entered but with invalid input (i_fee_grp_id is not in database)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/updatefeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\","
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 500 INTERNAL SERVER ERROR
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);
        
//     }

//      @Test
//     @Transactional
//     public void testSpUpdFeeGroupInvalidFormatString() throws Exception {

//         String i_fee_grp_id = "invalid_format";
 
//         // When valid credentials are entered but with invalid input (i_fee_grp_id is supposed to be integer, but string is entered)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/updatefeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\","
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 400 BAD REQUEST
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
        
//     }

//     @Test
//     @Transactional
//     public void testSpUpdFeeGroupInvalidFormatNegativeNumber() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(-1);
 
//         // When valid credentials are entered but with invalid input (i_fee_grp_id is supposed to be positive integer, but negative integer is entered)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/updatefeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\","
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 400 BAD REQUEST
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);
        
//     }

//     @Test
//     @Transactional
//     public void testSpUpdFeeGroupInternalServerError() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(13);
 
//         // When valid credentials are entered but with invalid JSON type(initial: i_fee_grp_id)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/updatefeegroup")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grpid\":\"" + i_fee_grp_id + "\","
//                 + "\"i_fee_grp_nm_en\":\"" + I_FEE_GRP_NM_EN + "\","
//                 + "\"i_fee_grp_nm_bm\":\"" + I_FEE_GRP_NM_BM + "\","
//                 + "\"i_modified_by\":\"" + I_MODIFIED_BY + "\","
//                 + "\"i_status\":\"" + I_STATUS + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 500 INTERNAL SERVER ERROR
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);
        
//     }



// // Update Fee Group Testing Ends Here
// //================================================================================================================
// // Check Fee Group Testing Starts Here

//      @Test
//     @Transactional
//     public void testSpCheckFeeGroupByIdUnauthorized() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(37);
 
//         // When invalid credentials are entered
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/checkfeegroupexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("ro:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 401 UNAUTHORIZED
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);

//     }

//     @Test
//     @Transactional
//     public void testSpCheckFeeGroupByIdSuccessResponse() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(37);
 
//         // When valid credentials are entered and valid input
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/checkfeegroupexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the header status code should be 00
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);
//         String content = mvcResult.getResponse().getContentAsString();
//         ApiResponse<?> apiResponse = objectMapper.readValue(content, ApiResponse.class);
//         assertEquals("00", apiResponse.getHeader().getStatusCode());

//     }

//      @Test
//     @Transactional
//     public void testSpCheckFeeGroupByIdRecordInUsed() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(1);
 
//         // When valid credentials are entered and valid input but record is in used
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/checkfeegroupexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the header status code should be 03
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.OK.value(), statusCode);
//         String content = mvcResult.getResponse().getContentAsString();
//         ApiResponse<?> apiResponse = objectMapper.readValue(content, ApiResponse.class);
//         assertEquals("03", apiResponse.getHeader().getStatusCode());

//     }

//     @Test
//     @Transactional
//     public void testSpCheckFeeGroupByIdInternalServerError() throws Exception {

//         Long i_fee_grp_id = Long.valueOf(10);
 
//         // When valid credentials are entered but with invalid JSON type(initial: i_fee_grp_id)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/checkfeegroupexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grpid\":\"" + i_fee_grp_id + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 500 INTERNAL SERVER ERROR
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statusCode);

//     }

//      @Test
//     @Transactional
//     public void testSpCheckFeeGroupByIdInvalidFormatString() throws Exception {

//         String i_fee_grp_id = "a";
 
//         // When valid credentials are entered but with invalid input (i_fee_grp_id is supposed to be integer, but string is entered)
//         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/checkfeegroupexist")
//                 .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//                 .encodeToString("roy:pass".getBytes()))
//                 .contentType("application/json")
//                 .content("{"
//                 + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\""
//                 + "}"))
//                 .andReturn();

//         // Then the response should be 400 BAD REQUEST
//         int statusCode = mvcResult.getResponse().getStatus();
//         assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    
//     }

//     //  @Test
//     // //@Transactional
//     // public void testSpCheckFeeGroupByIdInvalidFormatInteger() throws Exception {

//     //     Integer i_fee_grp_id = 16;
 
//     //     // When
//     //     MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/fg/v1/checkfeegroupexist")
//     //             .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
//     //             .encodeToString("roy:pass".getBytes()))
//     //             .contentType("application/json")
//     //             .content("{"
//     //             + "\"i_fee_grp_id\":\"" + i_fee_grp_id + "\""
//     //             + "}"))
//     //             .andReturn();

//     //     // Then
//     //     int statusCode = mvcResult.getResponse().getStatus();
//     //     assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    
//     // }


// // Check Fee Group Testing Ends Here
// //================================================================================================================




// }



