package com.yetanotherdevblog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanotherdevblog.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestDocsApplication.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@AutoConfigureMockMvc
public class UserControllerDocumentation {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    /**
     * All documentation production happens here.
     * 
     * @throws Exception 
     */
    @Test
    public void documentUsersResource() throws Exception {

        User user = new User(UUID.randomUUID(), "foobar", "Foo", "Bar");
        
        insertUser(user);

        User user1 = new User(null, "foobar1", "Foo1", "Bar1");

        insertUser(user1);

        getUser(user.getUserId());

        getUsers();

        user = new User(user.getUserId(), "foobar_changed", "Foo_changed", "Bar_changed");
        
        updateUser(user);

        user1 = new User(UUID.randomUUID(), "foobar1_changed", "Foo1_changed", "Bar1_changed");

        updateUser(user1);

        deleteUser(user.getUserId());

        deleteUser(UUID.randomUUID());
        
    }

    private void insertUser(User user) throws Exception {
        
        RestDocumentationResultHandler document = documentPrettyPrintReqResp("insertUser");
        
        document.document(
                requestFields(userFields(false)),
                responseFields(userFields(false))
        );
        
        this.mockMvc.perform(post("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document);
    }

    private void getUser(UUID userId) throws Exception {
        
        RestDocumentationResultHandler document = documentPrettyPrintReqResp("getUser");
        
        document.document(
                pathParameters(userPathParams()),
                responseFields(userFields(false))
        );
        
        this.mockMvc.perform(get("/api/v1/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").isNotEmpty())
                .andExpect(jsonPath("firstName").isNotEmpty())
                .andExpect(jsonPath("lastName").isNotEmpty())
                .andExpect(jsonPath("username").isNotEmpty())
                .andDo(document);
    }
    
    private void getUsers() throws Exception {
        
        RestDocumentationResultHandler document = documentPrettyPrintReqResp("getUsers");
        
        document.document(
                pathParameters(
                        parameterWithName("page").description("Page of results"),
                        parameterWithName("size").description("Size of results")
                ),
                responseFields(userFields(true))
        );
        
        this.mockMvc.perform(get("/api/v1/users?page={page}&size={size}", 0, 10)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("[*].userId").isNotEmpty())
                .andExpect(jsonPath("[*].firstName").isNotEmpty())
                .andExpect(jsonPath("[*].lastName").isNotEmpty())
                .andExpect(jsonPath("[*].username").isNotEmpty())
                .andDo(document);
    }
    
    private void updateUser(User user) throws Exception {
        
        RestDocumentationResultHandler document = 
                documentPrettyPrintReqResp("updateUser");
        
        document.document(
                pathParameters(userPathParams()),
                requestFields(userFields(false)),
                responseFields(userFields(false))
        );
        this.mockMvc.perform(
                put("/api/v1/users/{userId}", user.getUserId())
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document);
    }
    
    private void deleteUser(UUID userId) throws Exception {
        
        RestDocumentationResultHandler document = 
                documentPrettyPrintReqResp("deleteUser");
                
        document.document(
                pathParameters(userPathParams()),
                responseFields(userFields(false))
        );
        
        this.mockMvc.perform(delete("/api/v1/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document);
    }
    
    /**
     * Pretty print request and response
     * 
     * @param useCase the name of the snippet
     * @return RestDocumentationResultHandler
     */
    private RestDocumentationResultHandler documentPrettyPrintReqResp(String useCase) {
        return document(useCase,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()));
    }
    
    /**
     * User fields used in requests and responses.
     * An array field equivalent can be proveded 
     * @param isJsonArray if the fields are used in a JsonArray
     * @return 
     */
    private static FieldDescriptor[] userFields(boolean isJsonArray) {
        return isJsonArray ? 
                new FieldDescriptor[]{
                    fieldWithPath("[]").description("Users list"),
                    fieldWithPath("[].userId").description(USERS_ID_DESCRIPTION),
                    fieldWithPath("[].firstName").description(USERS_FIRST_NAME_DESCRIPTION),
                    fieldWithPath("[].lastName").description(USERS_LAST_NAME_DESCRIPTION),
                    fieldWithPath("[].username").description(USERS_USERNAME_DESCRIPTION)
                } : 
                new FieldDescriptor[]{
                    fieldWithPath("userId").description(USERS_ID_DESCRIPTION),
                    fieldWithPath("firstName").description(USERS_FIRST_NAME_DESCRIPTION),
                    fieldWithPath("lastName").description(USERS_LAST_NAME_DESCRIPTION),
                    fieldWithPath("username").description(USERS_USERNAME_DESCRIPTION)
                };
    }
    
    /**
     * userId in path variables
     * @return ParameterDescriptor
     */
    private static ParameterDescriptor[] userPathParams() {
        return new ParameterDescriptor[] {
            parameterWithName("userId").description(USERS_ID_DESCRIPTION)
        };
    }
    
    private static final String USERS_USERNAME_DESCRIPTION = "User's username";
    private static final String USERS_LAST_NAME_DESCRIPTION = "User's last name";
    private static final String USERS_FIRST_NAME_DESCRIPTION = "User's first name";
    private static final String USERS_ID_DESCRIPTION = "User's identifier";
    
}
