package com.dani.vbank.test.security.csrf;

import com.dani.vbank.VBankApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment=SpringBootTest.WebEnvironment.MOCK,
        classes = VBankApplication.class)
@AutoConfigureMockMvc
public class BankControllerCsrfTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    protected MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    protected RequestPostProcessor testUser() {
        return SecurityMockMvcRequestPostProcessors.user("testUser").password("userPass").roles("USER");
    }

    @Test
    public void testTransactionNoCsrfToken() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post("/doTransfer").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fromAccount", "11")
                        .param("toAccount", "22")
                        .param("amount", "33")
                        .param("currency", "CHF")
                        .param("note", "hello")
                        .with(testUser())
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testTransaction_WithGoodCsrfToken() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post("/doTransfer").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fromAccount", "11")
                        .param("toAccount", "22")
                        .param("amount", "33")
                        .param("currency", "CHF")
                        .param("note", "hello")
                        .with(testUser())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    public void testTransaction_WithWrongCsrfToken() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post("/doTransfer").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fromAccount", "11")
                        .param("toAccount", "22")
                        .param("amount", "33")
                        .param("currency", "CHF")
                        .param("note", "hello")
                        .with(testUser())
                        .with(SecurityMockMvcRequestPostProcessors.csrf().useInvalidToken())
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }


}
