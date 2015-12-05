package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.somePrivateUrl;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.someUrl;

/**
 * Tests for UrlShortenerController, testing both REDIRECT functionality
 * and SHORTENER functionality.
 */
public class RedirectControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ShortURLRepository shortURLRepository;

	@Mock
	private ClickRepository clickRespository;

	@InjectMocks
	private RedirectController redirectController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(redirectController).build();
	}

	@Test
	/*
	Test that REDIRECT over a NON-PRIVATE link redirects if KEY EXISTS.
	 */
	public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
			throws Exception {

		//Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}

	@Test
	/*
	Test that REDIRECT over a PRIVATE link redirects if KEY EXISTS
	and Private Token IS CORRECT.
	 */
	public void thatRedirectToPrivateReturnsTemporaryRedirectIfKeyExistsAndPrivateTokenCorrect()
			throws Exception {

		//Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")
				.param("privateToken","privateToken"))
				.andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}

	@Test
	/*
	Test that REDIRECT over a PRIVATE link gives error 401 if key exists
	and Private Token IS NOT CORRECT.
	 */
	public void thatRedirectToPrivateReturnsTemporaryRedirectIfKeyExistsAndPrivateTokenIncorrect()
			throws Exception {

		//Mock URLrepository response to a private URL.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		//Test that 401 Unauthorized is returned (Bad Private token)
		mockMvc.perform(get("/{id}", "someKey")
				.param("privateToken","incorrectToken"))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	/*
	Test that REDIRECT over a PRIVATE link gives error 401 if key exists
	and Private Token IS NOT SUPPLIED.
	 */
	public void thatRedirectToPrivateReturnsTemporaryRedirectIfKeyExistsAndPrivateTokenNotSupplied()
			throws Exception {

		//Mock URLrepository response to a private URL.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		//Test that 401 Unauthorized is returned (Bad Private token)
		mockMvc.perform(get("/{id}", "someKey"))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	/*
	Test that REDIRECT over an ID that does NOT EXIST gives error 404.
	 */
	public void thatRedirectToReturnsNotFoundIdIfKeyDoesNotExist()
			throws Exception {

		//Mock URLRepository to return null -> Not found
		when(shortURLRepository.findByHash("someKey")).thenReturn(null);

		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isNotFound());
	}



}