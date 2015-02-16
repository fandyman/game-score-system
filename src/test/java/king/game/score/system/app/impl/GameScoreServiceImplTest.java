package king.game.score.system.app.impl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.SortedSet;
import java.util.TreeSet;

import king.game.score.system.app.client.event.ErrorEvent;
import king.game.score.system.app.client.event.Event;
import king.game.score.system.app.client.event.ResponseEvent;
import king.game.score.system.domain.UserId;
import king.game.score.system.domain.scoreboard.LevelId;
import king.game.score.system.domain.scoreboard.Score;
import king.game.score.system.domain.session.SessionManager;
import king.game.score.system.domain.scoreboard.ScoreBoardManager;
import king.game.score.system.domain.scoreboard.UserScore;
import king.game.score.system.domain.session.SessionKey;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GameScoreServiceImplTest {

    public static final String SESSION_KEY_VALUE = "fdsajfhajdlshffhdja";
    private static final SessionKey SESSION_KEY = new SessionKey(SESSION_KEY_VALUE);
    public static final int LEVEL_ID_VALUE = 3;
    public static final int BAD_LEVEL_ID_VALUE = -3;
    private static final LevelId LEVEL_ID = new LevelId(LEVEL_ID_VALUE);
    public static final long USER_ID_VALUE = 1234l;
    private static final UserId USER_ID = new UserId(USER_ID_VALUE);
    public static final int SCORE_VALUE = 1500;
    private static final Score SCORE = new Score(SCORE_VALUE);
    private static final Integer BAD_SCORE_VALUE = -5;
    private static final UserScore USER_SCORE = new UserScore(USER_ID, SCORE);
    private static final UserId USER_ID_2 = new UserId(1236l);
    private static final Score SCORE_2 = new Score(1800);
    private static final UserScore USER_SCORE_2 = new UserScore(USER_ID_2, SCORE_2);
    private static final SortedSet<UserScore> HIGHEST_SCORES = new TreeSet<UserScore>();

    ArgumentCaptor<LevelId> levelIdCaptor = ArgumentCaptor.forClass(LevelId.class);
    ArgumentCaptor<UserScore> userScoreCaptor = ArgumentCaptor.forClass(UserScore.class);

    @Mock
    ScoreBoardManager scoreBoardManager;
    @Mock
    SessionManager sessionManager;

    @InjectMocks
    GameScoreServiceImpl testObj;

    @Test
    public void addsScoreForLevelSuccessful() {
        // arrange
        when(sessionManager.getUserId(SESSION_KEY)).thenReturn(USER_ID);

        // act
        Event successEvent = testObj.addScore(LEVEL_ID_VALUE, SCORE_VALUE, SESSION_KEY_VALUE);

        // assert
        verify(scoreBoardManager).addScore(levelIdCaptor.capture(), userScoreCaptor.capture());
        assertThat(successEvent, instanceOf(ResponseEvent.class));
        assertThat(successEvent.getResponseBody(), is(ResponseEvent.EMPTY_BODY));
        assertThat(levelIdCaptor.getValue(), is(LEVEL_ID));
        assertThat(userScoreCaptor.getValue().getUserId(), is(USER_ID.getValue()));
        assertThat(userScoreCaptor.getValue().getScore(), is(SCORE.getValue()));
    }

    @Test
    public void addsScoreForLevelWithNegativeScoreError() {
        // arrange
        when(sessionManager.getUserId(SESSION_KEY)).thenReturn(USER_ID);

        // act
        Event errorEvent = testObj.addScore(LEVEL_ID_VALUE, BAD_SCORE_VALUE, SESSION_KEY_VALUE);

        // assert
        verify(scoreBoardManager, never()).addScore(any(LevelId.class), any(UserScore.class));
        assertThat(errorEvent, instanceOf(ErrorEvent.class));
        assertThat(errorEvent.getResponseBody(), CoreMatchers.is(Score.SCORE_MUST_BE_POSITIVE_OR_ZERO));
    }

    @Test
    public void addsScoreForLevelWithNegativeLevelError() {
        // arrange
        when(sessionManager.getUserId(SESSION_KEY)).thenReturn(USER_ID);

        // act
        Event errorEvent = testObj.addScore(BAD_LEVEL_ID_VALUE, SCORE_VALUE, SESSION_KEY_VALUE);

        // assert
        verify(scoreBoardManager, never()).addScore(any(LevelId.class), any(UserScore.class));
        assertThat(errorEvent, instanceOf(ErrorEvent.class));
        assertThat(errorEvent.getResponseBody(), CoreMatchers.is(LevelId.LEVEL_ID_MUST_BE_POSITIVE_VALUE));
    }

    @Test
    public void getsHighestScoresForLevel() {
        // arrange
        HIGHEST_SCORES.add(USER_SCORE);
        HIGHEST_SCORES.add(USER_SCORE_2);
        when(scoreBoardManager.getLevelScores(LEVEL_ID)).thenReturn(HIGHEST_SCORES);

        // act
        Event responseEvent = testObj.getHighestScores(LEVEL_ID_VALUE);

        // assert
        verify(scoreBoardManager).getLevelScores(LEVEL_ID);
        assertThat(responseEvent, instanceOf(ResponseEvent.class));
        assertThat(responseEvent.getResponseBody(), is("1236=1800,1234=1500"));
    }

    @Test
    public void getsHighestScoresForLevelWithNegativeLevelId() {
        // arrange

        // act
        Event responseEvent = testObj.getHighestScores(BAD_LEVEL_ID_VALUE);

        // assert
        verify(scoreBoardManager, never()).getLevelScores(any(LevelId.class));
        assertThat(responseEvent, instanceOf(ErrorEvent.class));
        assertThat(responseEvent.getResponseBody(), CoreMatchers.is(LevelId.LEVEL_ID_MUST_BE_POSITIVE_VALUE));
    }

}
