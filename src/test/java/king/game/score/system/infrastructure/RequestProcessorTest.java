package king.game.score.system.infrastructure;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import king.game.score.system.app.client.GameScoreService;
import king.game.score.system.app.client.LoginService;
import king.game.score.system.app.client.event.ErrorEvent;
import king.game.score.system.app.client.event.Event;
import king.game.score.system.app.client.event.ResponseEvent;
import king.game.score.system.domain.UserId;
import king.game.score.system.domain.scoreboard.LevelId;
import king.game.score.system.domain.scoreboard.Score;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sun.net.httpserver.HttpExchange;

@RunWith(MockitoJUnitRunner.class)
public class RequestProcessorTest {

    private static final Long USER_ID = 1234l;
    private static final String USER_ID_STRING = USER_ID.toString();
    private static final Long NEGATIVE_USER_ID = 1234l;
    private static final String SESSION_KEY = "fdsajfhajdlshffhdja";
    private static final Event SESSION_KEY_RESPONSE_EVENT = new ResponseEvent(SESSION_KEY);
    private static final Event SESSION_KEY_RESPONSE_FAILED_EVENT = new ErrorEvent(UserId.USER_ID_MUST_BE_POSITIVE_VALUE);
    private static final Event HAS_SESSION_EXPIRED_RESPONSE_EVENT = new ErrorEvent(Boolean.TRUE.toString());
    private static final Event HAS_SESSION_NOT_EXPIRED_RESPONSE_EVENT = new ResponseEvent(Boolean.FALSE.toString());
    private static final Event EMPTY_BODY_RESPONSE_EVENT = new ResponseEvent(ResponseEvent.EMPTY_BODY);
    private static final Event SCORE_HAS_TO_BE_POSITIVE_INTEGER_RESPONSE_BODY_EVENT = new ErrorEvent(Score.SCORE_MUST_BE_POSITIVE_OR_ZERO);
    private static final Integer LEVEL_ID = 3;
    private static final String LEVEL_ID_STRING = LEVEL_ID.toString();
    private static final Integer NEGATIVE_LEVEL_ID = 3;
    private static final Integer SCORE = 1500;
    private static final String SCORE_STRING = "1500";
    private static final Integer NEGATIVE_SCORE = -1;
    private static final String NEGATIVE_SCORE_STRING = "-1";
    private static final String HIGHEST_USERS_SCORES_CSV_BODY = "4711=1500,131=1220";
    private static final Event HIGHEST_USER_SCORES_RESPONSE_EVENT = new ResponseEvent(HIGHEST_USERS_SCORES_CSV_BODY);
    private static final Event HIGHEST_USER_SCORES_FAILED_EVENT = new ErrorEvent(LevelId.LEVEL_ID_MUST_BE_POSITIVE_VALUE);

    @Mock
    HttpExchange httpExchange;
    @Mock
    InputStream inputStream;
    @Mock
    TextResponseWriter textResponseWriter;
    @Mock
    LoginService loginService;
    @Mock
    GameScoreService gameScoreService;

    @InjectMocks
    RequestProcessor testObj;

    ArgumentCaptor<HttpExchange> httpExchangeArgumentCaptor = ArgumentCaptor.forClass(HttpExchange.class);
    ArgumentCaptor<HttpResponseCode> httpResponseCodeArgumentCaptor = ArgumentCaptor.forClass(HttpResponseCode.class);
    ArgumentCaptor<String> httpResponseBodyArgumentCaptor = ArgumentCaptor.forClass(String.class);

    @Test
    public void handleLoginSuccessfully() throws IOException, URISyntaxException {
        // arrange
        when(loginService.getSessionKey(USER_ID)).thenReturn(SESSION_KEY_RESPONSE_EVENT);

        // act
        HttpResponseEvent httpResponseEvent = testObj.logIn(USER_ID_STRING);

        // assert
        verify(loginService).getSessionKey(USER_ID);
        assertThat(httpResponseEvent, is(new HttpResponseEvent(SESSION_KEY, HttpResponseCode._200_OK)));
    }

    @Test
    public void handleLoginFailed() throws IOException, URISyntaxException {
        // arrange
        when(loginService.getSessionKey(NEGATIVE_USER_ID)).thenReturn(SESSION_KEY_RESPONSE_FAILED_EVENT);

        // act
        HttpResponseEvent httpResponseEvent = testObj.logIn(USER_ID_STRING);

        // assert
        verify(loginService).getSessionKey(NEGATIVE_USER_ID);
        assertThat(httpResponseEvent, is(new HttpResponseEvent(UserId.USER_ID_MUST_BE_POSITIVE_VALUE, HttpResponseCode._404_NOT_FOUND)));
    }

    @Test
    public void handleSessionExpired() throws IOException, URISyntaxException {
        // arrange
        when(loginService.hasSessionKeyExpired(SESSION_KEY)).thenReturn(HAS_SESSION_EXPIRED_RESPONSE_EVENT);

        // act
        boolean hasExpired = testObj.hasSessionExpired(SESSION_KEY);

        // assert
        verify(loginService).hasSessionKeyExpired(SESSION_KEY);
        assertThat(hasExpired, is(true));
    }

    @Test
    public void handleSessionNotExpired() throws IOException, URISyntaxException {
        // arrange
        when(loginService.hasSessionKeyExpired(SESSION_KEY)).thenReturn(HAS_SESSION_NOT_EXPIRED_RESPONSE_EVENT);

        // act
        boolean hasExpired = testObj.hasSessionExpired(SESSION_KEY);

        // assert
        verify(loginService).hasSessionKeyExpired(SESSION_KEY);
        assertThat(hasExpired, is(false));
    }

    @Test
    public void handleGetHighestScoresSuccessfull() throws IOException, URISyntaxException {
        // arrange
        when(gameScoreService.getHighestScores(LEVEL_ID)).thenReturn(HIGHEST_USER_SCORES_RESPONSE_EVENT);

        // act
        HttpResponseEvent httpResponseEvent = testObj.getHighestScores(LEVEL_ID_STRING);

        // assert
        verify(gameScoreService).getHighestScores(LEVEL_ID);
        assertThat(httpResponseEvent, is(new HttpResponseEvent(HIGHEST_USERS_SCORES_CSV_BODY, HttpResponseCode._200_OK)));
    }

    @Test
    public void handleGetHighestScoresFailed() throws IOException, URISyntaxException {
        // arrange
        when(gameScoreService.getHighestScores(NEGATIVE_LEVEL_ID)).thenReturn(HIGHEST_USER_SCORES_FAILED_EVENT);

        // act
        HttpResponseEvent httpResponseEvent = testObj.getHighestScores(LEVEL_ID_STRING);

        // assert
        verify(gameScoreService).getHighestScores(NEGATIVE_LEVEL_ID);
        assertThat(httpResponseEvent, is(new HttpResponseEvent(LevelId.LEVEL_ID_MUST_BE_POSITIVE_VALUE, HttpResponseCode._404_NOT_FOUND)));
    }

    @Test
    public void handleAddScoreSuccessfull() throws IOException, URISyntaxException {
        // arrange
        when(gameScoreService.addScore(LEVEL_ID, SCORE, SESSION_KEY)).thenReturn(EMPTY_BODY_RESPONSE_EVENT);

        // act
        HttpResponseEvent httpResponseEvent = testObj.addScore(LEVEL_ID_STRING, SCORE_STRING, SESSION_KEY);

        // assert
        verify(gameScoreService).addScore(LEVEL_ID, SCORE, SESSION_KEY);
        assertThat(httpResponseEvent, is(new HttpResponseEvent(ResponseEvent.EMPTY_BODY, HttpResponseCode._200_OK)));
    }

    @Test
    public void handleAddScoreFailed() throws IOException, URISyntaxException {
        // arrange
        when(gameScoreService.addScore(LEVEL_ID, NEGATIVE_SCORE, SESSION_KEY)).thenReturn(SCORE_HAS_TO_BE_POSITIVE_INTEGER_RESPONSE_BODY_EVENT);

        // act
        HttpResponseEvent httpResponseEvent = testObj.addScore(LEVEL_ID_STRING, NEGATIVE_SCORE_STRING, SESSION_KEY);

        // assert
        verify(gameScoreService).addScore(LEVEL_ID, NEGATIVE_SCORE, SESSION_KEY);
        assertThat(httpResponseEvent, is(new HttpResponseEvent(Score.SCORE_MUST_BE_POSITIVE_OR_ZERO, HttpResponseCode._404_NOT_FOUND)));
    }

}
