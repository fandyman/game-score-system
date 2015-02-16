package king.game.score.system.infrastructure;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import king.game.score.system.app.client.event.ResponseEvent;
import king.game.score.system.domain.scoreboard.Score;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sun.net.httpserver.HttpExchange;

@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerTest {

    private static final int _200_OK = 200;
    private static final String EMPTY_BODY = "";
    private static final String USER_ID = "1234";
    private static final int _403_FORBIDDEN = 403;
    private static final int _404_NOT_FOUND = 404;
    private static final String SESSION_KEY = "fdsajfhajdlshffhdja";
    private static final HttpResponseEvent SESSION_KEY_RESPONSE_EVENT = new HttpResponseEvent(SESSION_KEY, HttpResponseCode._200_OK);
    private static final boolean SESSION_EXPIRED = true;
    private static final boolean SESSION_NOT_EXPIRED = false;
    private static final HttpResponseEvent EMPTY_BODY_RESPONSE_EVENT = new HttpResponseEvent(ResponseEvent.EMPTY_BODY, HttpResponseCode._200_OK);
    private static final HttpResponseEvent SCORE_HAS_TO_BE_POSITIVE_INTEGER_RESPONSE_BODY_EVENT = new HttpResponseEvent(Score.SCORE_MUST_BE_POSITIVE_OR_ZERO,HttpResponseCode._404_NOT_FOUND);
    private static final String LEVEL_ID = "3";
    private static final String SCORE = "1500";
    private static final String NEGATIVE_SCORE = "-1";
    private static final String BAD_SCORE = "fsdafds";
    private static final String HIGHEST_USERS_SCORES_CSV_BODY = "4711=1500,131=1220";
    private static final HttpResponseEvent HIGHEST_USER_SCORES_RESPONSE_EVENT = new HttpResponseEvent(HIGHEST_USERS_SCORES_CSV_BODY, HttpResponseCode._200_OK);
    public static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(String.valueOf(EMPTY_BODY).getBytes());
    public static final InputStream SCORE_INPUT_STREAM = new ByteArrayInputStream(String.valueOf(SCORE).getBytes());

    @Mock
    HttpExchange httpExchange;
    @Mock
    InputStream inputStream;
    @Mock
    TextResponseWriter textResponseWriter;
    @Mock
    RequestProcessor requestProcessor;

    @InjectMocks
    RequestHandler testObj;

    ArgumentCaptor<HttpExchange> httpExchangeArgumentCaptor = ArgumentCaptor.forClass(HttpExchange.class);
    ArgumentCaptor<HttpResponseCode> httpResponseCodeArgumentCaptor = ArgumentCaptor.forClass(HttpResponseCode.class);
    ArgumentCaptor<String> httpResponseBodyArgumentCaptor = ArgumentCaptor.forClass(String.class);

    @Test
    public void processesLoginSuccessfully() throws IOException, URISyntaxException {
        // arrange
        URI requestURI = new URI("/"+USER_ID+"/login");
        when(httpExchange.getRequestURI()).thenReturn(requestURI);
        when(requestProcessor.logIn(USER_ID)).thenReturn(SESSION_KEY_RESPONSE_EVENT);
        when(httpExchange.getRequestBody()).thenReturn(EMPTY_INPUT_STREAM);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor).logIn(USER_ID);
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_200_OK));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), is(SESSION_KEY));
    }

    @Test
    public void failsToFindHandlerForLogin() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_NOT_EXPIRED);
        URI requestURI = new URI("/"+USER_ID+"/loginn");
        when(httpExchange.getRequestURI()).thenReturn(requestURI);
        when(httpExchange.getRequestBody()).thenReturn(EMPTY_INPUT_STREAM);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor, never()).hasSessionExpired(anyString());
        verify(requestProcessor, never()).logIn(anyString());
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_404_NOT_FOUND));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), CoreMatchers.is(HttpResponseEvent.BAD_REQUEST_RESPONSE_BODY));
    }

    @Test
    public void processesAddScoreSuccessfully() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_NOT_EXPIRED);
        when(requestProcessor.addScore(LEVEL_ID, SCORE, SESSION_KEY)).thenReturn(EMPTY_BODY_RESPONSE_EVENT);
        InputStream scoreInputStream = new ByteArrayInputStream(String.valueOf(SCORE).getBytes());
        when(httpExchange.getRequestBody()).thenReturn(scoreInputStream);
        URI requestURI = new URI("/"+LEVEL_ID+"/score?sessionkey="+SESSION_KEY);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor).hasSessionExpired(SESSION_KEY);
        verify(requestProcessor).addScore(LEVEL_ID, SCORE, SESSION_KEY);
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_200_OK));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), is(EMPTY_BODY));
    }

    @Test
    public void failsToFindHandlerForBadAddScoreRequestUri() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_NOT_EXPIRED);
        URI requestURI = new URI("/"+LEVEL_ID+"/scores?sessionKey="+SESSION_KEY);
        when(httpExchange.getRequestBody()).thenReturn(EMPTY_INPUT_STREAM);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor, never()).hasSessionExpired(anyString());
        verify(requestProcessor, never()).addScore(anyString(), anyString(), anyString());
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_404_NOT_FOUND));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), CoreMatchers.is(HttpResponseEvent.BAD_REQUEST_RESPONSE_BODY));
    }

    @Test
    public void processingAddScoreFailedDueToLoginExpiry() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_EXPIRED);
        URI requestURI = new URI("/"+LEVEL_ID+"/score?sessionkey="+SESSION_KEY);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);
        when(httpExchange.getRequestBody()).thenReturn(SCORE_INPUT_STREAM);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor).hasSessionExpired(SESSION_KEY);
        verify(requestProcessor, never()).addScore(anyString(), anyString(), anyString());
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_403_FORBIDDEN));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), CoreMatchers.is(HttpResponseEvent.SESSION_EXPIRED_RESPONSE_BODY));
    }

    @Test
    public void processingAddScoreFailedDueToBadScoreData() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_NOT_EXPIRED);
        InputStream scoreInputStream = new ByteArrayInputStream(BAD_SCORE.getBytes());
        when(httpExchange.getRequestBody()).thenReturn(scoreInputStream);
        URI requestURI = new URI("/"+LEVEL_ID+"/score?sessionkey="+SESSION_KEY);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor, never()).hasSessionExpired(SESSION_KEY);
        verify(requestProcessor, never()).addScore(anyString(), anyString(), anyString());
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_404_NOT_FOUND));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), CoreMatchers.is(HttpResponseEvent.BAD_REQUEST_RESPONSE_BODY));
    }

    @Test
    public void processingAddScoreFailedDueToNegativeScoreData() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_NOT_EXPIRED);
        when(requestProcessor.addScore(LEVEL_ID, NEGATIVE_SCORE, SESSION_KEY)).thenReturn(SCORE_HAS_TO_BE_POSITIVE_INTEGER_RESPONSE_BODY_EVENT);
        InputStream scoreInputStream = new ByteArrayInputStream(String.valueOf(NEGATIVE_SCORE).getBytes());
        when(httpExchange.getRequestBody()).thenReturn(scoreInputStream);
        URI requestURI = new URI("/"+LEVEL_ID+"/score?sessionkey="+SESSION_KEY);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor, never()).hasSessionExpired(anyString());
        verify(requestProcessor, never()).addScore(anyString(), anyString(), anyString());
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_404_NOT_FOUND));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), CoreMatchers.is(HttpResponseEvent.BAD_REQUEST_RESPONSE_BODY));
    }

    @Test
    public void processesGetHighScoreListSuccessfully() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_NOT_EXPIRED);
        URI requestURI = new URI("/"+LEVEL_ID+"/highscorelist?sessionkey="+SESSION_KEY);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);
        when(requestProcessor.getHighestScores(LEVEL_ID)).thenReturn(HIGHEST_USER_SCORES_RESPONSE_EVENT);
        when(httpExchange.getRequestBody()).thenReturn(EMPTY_INPUT_STREAM);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor).hasSessionExpired(SESSION_KEY);
        verify(requestProcessor).getHighestScores(LEVEL_ID);
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_200_OK));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), is(HIGHEST_USERS_SCORES_CSV_BODY));
    }

    @Test
    public void failsToFindHandlerForBadGetHighScoreListRequestUri() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_NOT_EXPIRED);
        URI requestURI = new URI("/"+LEVEL_ID+"/highScoreList?sessionKey="+SESSION_KEY);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);
        when(httpExchange.getRequestBody()).thenReturn(EMPTY_INPUT_STREAM);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor, never()).hasSessionExpired(anyString());
        verify(requestProcessor, never()).getHighestScores(anyString());
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_404_NOT_FOUND));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), CoreMatchers.is(HttpResponseEvent.BAD_REQUEST_RESPONSE_BODY));
    }

    @Test
    public void processesingGetHighScoreFailedDueToLoginExpiry() throws IOException, URISyntaxException {
        // arrange
        when(requestProcessor.hasSessionExpired(SESSION_KEY)).thenReturn(SESSION_EXPIRED);
        URI requestURI = new URI("/"+LEVEL_ID+"/highscorelist?sessionkey="+SESSION_KEY);
        when(httpExchange.getRequestURI()).thenReturn(requestURI);
        when(httpExchange.getRequestBody()).thenReturn(EMPTY_INPUT_STREAM);

        // act
        testObj.handle(httpExchange);

        // assert
        verify(requestProcessor).hasSessionExpired(SESSION_KEY);
        verify(requestProcessor, never()).getHighestScores(anyString());
        verify(textResponseWriter).write(httpExchangeArgumentCaptor.capture(), httpResponseCodeArgumentCaptor.capture(), httpResponseBodyArgumentCaptor.capture());
        assertThat(httpResponseCodeArgumentCaptor.getValue().getHttpCode().intValue(), is(_403_FORBIDDEN));
        assertThat(httpResponseBodyArgumentCaptor.getValue(), CoreMatchers.is(HttpResponseEvent.SESSION_EXPIRED_RESPONSE_BODY));
    }
}
