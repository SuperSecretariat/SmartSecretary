package com.example.demo;

import com.example.demo.controller.FormRequestsController;
import com.example.demo.dto.FormRequestFieldsDTO;
import com.example.demo.response.FormRequestResponse;
import com.example.demo.model.enums.FormRequestStatus;
import com.example.demo.entity.FormRequest;
import com.example.demo.dto.FormRequestDTO;
import com.example.demo.service.FormRequestService;
import com.example.demo.util.JwtUtil;
import com.example.demo.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FormRequestsControllerTest {

   private MockMvc mockMvc;

   @Mock
   private FormRequestService formRequestService;

   @Mock
   private JwtUtil jwtUtil;

   @InjectMocks
   private FormRequestsController formRequestsController;

   @BeforeEach
   public void setUp() {
      MockitoAnnotations.openMocks(this);
      mockMvc = MockMvcBuilders.standaloneSetup(formRequestsController).build();
   }

   @Test
   public void testGetAllFormsForUserWithId_Success() throws Exception {
      String token = "validToken";
      String header = "Bearer " + token;

      FormRequestResponse response1 = new FormRequestResponse(1L, "Form A", FormRequestStatus.PENDING, 100L);
      FormRequestResponse response2 = new FormRequestResponse(2L, "Form B", FormRequestStatus.APPROVED, 101L);
      List<FormRequestResponse> responses = Arrays.asList(response1, response2);

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      when(formRequestService.getFormRequestsByUserRegistrationNumber(token)).thenReturn(responses);

      mockMvc.perform(get("/api/form-requests/submitted")
                      .header("Authorization", header))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON));
   }

   @Test
   public void testGetAllFormsForUserWithId_Unauthorized() throws Exception {
      String token = "invalid";
      String header = "Bearer " + token;

      when(jwtUtil.validateJwtToken(token)).thenReturn(false);

      mockMvc.perform(get("/api/form-requests/submitted")
                      .header("Authorization", header))
              .andExpect(status().isUnauthorized());
   }

   @Test
   public void testGetAllFormsForReview_Success() throws Exception {
      String token = "validToken";
      String header = "Bearer " + token;

      FormRequestResponse pending = new FormRequestResponse(1L, "Form A", FormRequestStatus.PENDING, 100L);
      FormRequestResponse inReview = new FormRequestResponse(2L, "Form B", FormRequestStatus.IN_REVIEW, 101L);
      List<FormRequestResponse> pendingList = Collections.singletonList(pending);
      List<FormRequestResponse> inReviewList = Collections.singletonList(inReview);

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      when(formRequestService.getFormRequestsByStatus(FormRequestStatus.PENDING)).thenReturn(pendingList);
      when(formRequestService.getFormRequestsByStatus(FormRequestStatus.IN_REVIEW)).thenReturn(inReviewList);

      mockMvc.perform(get("/api/form-requests/review-requests")
                      .header("Authorization", header))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON));
   }

   @Test
   public void testGetAllFormsForReview_Unauthorized() throws Exception {
      String token = "invalid";
      String header = "Bearer " + token;

      when(jwtUtil.validateJwtToken(token)).thenReturn(false);

      mockMvc.perform(get("/api/form-requests/review-requests")
                      .header("Authorization", header))
              .andExpect(status().isUnauthorized());
   }

   @Test
   public void testGetFormRequestById_Success() throws Exception {
      Long id = 1L;
      String token = "validToken";
      String header = "Bearer " + token;
      FormRequest formRequest = new FormRequest();

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      when(formRequestService.getFormRequestById(id)).thenReturn(formRequest);

      mockMvc.perform(get("/api/form-requests/{id}", id)
                      .header("Authorization", header))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON));
   }

   @Test
   public void testGetFormRequestById_BadRequest() throws Exception {
      Long id = 1L;
      String token = "validToken";
      String header = "Bearer " + token;

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      when(formRequestService.getFormRequestById(id)).thenThrow(new InvalidFormRequestIdException("Invalid ID"));

      mockMvc.perform(get("/api/form-requests/{id}", id)
                      .header("Authorization", header))
              .andExpect(status().isBadRequest());
   }

   @Test
   public void testCreateFormRequest_Success() throws Exception {
      String token = "validToken";
      String header = "Bearer " + token;

      // Construct DTO with required fields
      FormRequestDTO requestDto = new FormRequestDTO(token, 100L, Arrays.asList("field1", "field2"));
      FormRequest created = new FormRequest();
      created.setId(1L);

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      when(formRequestService.createFormRequest(any(FormRequestDTO.class))).thenReturn(created);

      String jsonContent = "{"
              + "\"jwtToken\": \"validToken\","
              + "\"formId\": 100,"
              + "\"fieldsData\": [\"field1\", \"field2\"]"
              + "}";

      mockMvc.perform(post("/api/form-requests/create")
                      .header("Authorization", header)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonContent))
              .andExpect(status().isCreated())
              .andExpect(header().string("Location", "http://localhost/api/form-requests/create/1"));
   }

   @Test
   public void testCreateFormRequest_BadRequest() throws Exception {
      String token = "validToken";
      String header = "Bearer " + token;

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      doThrow(new FormRequestFieldDataException("Invalid data")).when(formRequestService).createFormRequest(any(FormRequestDTO.class));

      String jsonContent = "{"
              + "\"jwtToken\": \"validToken\","
              + "\"formId\": 0,"
              + "\"fieldsData\": []"
              + "}";

      mockMvc.perform(post("/api/form-requests/create")
                      .header("Authorization", header)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonContent))
              .andExpect(status().isBadRequest());
   }

   @Test
   public void testUpdateFormRequestStatus_Success() throws Exception {
      Long id = 1L;
      String statusUpdate = "APPROVED";
      String jsonContent = "\"" + statusUpdate + "\"";

      mockMvc.perform(patch("/api/form-requests/{id}/status", id)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonContent))
              .andExpect(status().isNoContent())
              .andExpect(content().string("The status of the form request has been updated successfully."));
   }

   @Test
   public void testUpdateFormRequestStatus_BadRequest() throws Exception {
      Long id = 1L;
      String statusUpdate = "INVALID";
      String jsonContent = "\"" + statusUpdate + "\"";

      doThrow(new InvalidFormRequestStatusException("Invalid status")).when(formRequestService).updateFormRequestStatus(eq(id), anyString());

      mockMvc.perform(patch("/api/form-requests/{id}/status", id)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonContent))
              .andExpect(status().isBadRequest());
   }

   @Test
   public void testGetFormImage_Success() throws Exception {
      Long id = 1L;
      String token = "validToken";
      String header = "Bearer " + token;
      byte[] imageBytes = new byte[]{0x1, 0x2};

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      when(formRequestService.getFormRequestImage(id)).thenReturn(imageBytes);

      mockMvc.perform(get("/api/form-requests/{id}/image", id)
                      .header("Authorization", header))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.IMAGE_PNG));
   }

   @Test
   public void testGetFormImage_ServerError() throws Exception {
      Long id = 1L;
      String token = "validToken";
      String header = "Bearer " + token;

      when(jwtUtil.validateJwtToken(token)).thenReturn(true);
      when(formRequestService.getFormRequestImage(id)).thenThrow(new IOException("IO Error"));

      mockMvc.perform(get("/api/form-requests/{id}/image", id)
                      .header("Authorization", header))
              .andExpect(status().isInternalServerError());
   }

   @Test
   public void testGetFormRequestFieldsById_Success() throws Exception {
      Long id = 1L;
      FormRequestFieldsDTO fieldsDTO = new FormRequestFieldsDTO();

      when(formRequestService.getFormRequestFieldsById(id)).thenReturn(fieldsDTO);

      mockMvc.perform(get("/api/form-requests/{id}/fields", id))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON));
   }

   @Test
   public void testGetFormRequestFieldsById_BadRequest() throws Exception {
      Long id = 1L;

      doThrow(new InvalidFormRequestIdException("Invalid ID")).when(formRequestService).getFormRequestFieldsById(id);

      mockMvc.perform(get("/api/form-requests/{id}/fields", id))
              .andExpect(status().isBadRequest());
   }

   @Test
   public void testDeleteFormRequestById_Success() throws Exception {
      Long id = 1L;

      mockMvc.perform(delete("/api/form-requests/{id}", id))
              .andExpect(status().isNoContent());
   }

   @Test
   public void testDeleteFormRequestById_BadRequest() throws Exception {
      Long id = 1L;

      doThrow(new InvalidFormRequestIdException("Invalid ID")).when(formRequestService).deleteFormRequestById(id);

      mockMvc.perform(delete("/api/form-requests/{id}", id))
              .andExpect(status().isBadRequest());
   }
}
