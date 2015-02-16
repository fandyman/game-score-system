package king.game.score.system.domain;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import king.game.score.system.domain.scoreboard.LevelId;
import king.game.score.system.domain.scoreboard.Score;
import king.game.score.system.domain.scoreboard.ScoreBoardManager;
import king.game.score.system.domain.scoreboard.UserScore;

import org.junit.Test;

public class ScoreBoardManagerTest {

    private static final String SESSION_KEY = "fdsajfhajdlshffhdja";
    private static final LevelId LEVEL_ID = new LevelId(1);
    private static final LevelId LEVEL_ID_2 = new LevelId(2);
    private static final UserId USER_ID = new UserId(1234l);
    private static final Score SCORE = new Score(1500);
    private static final UserScore USER_SCORE_1 = new UserScore(USER_ID, SCORE);
    private static final UserId USER_ID_2 = new UserId(1236l);
    private static final Score SCORE_2 = new Score(1800);
    private static final UserScore USER_SCORE_1_UPDATE_2 = new UserScore(USER_ID, SCORE_2);
    private static final UserScore USER_SCORE_2 = new UserScore(USER_ID_2, SCORE_2);
    private static final UserId USER_ID_3 = new UserId(1237l);
    private static final Score SCORE_3 = new Score(1900);
    private static final UserScore USER_SCORE_3 = new UserScore(USER_ID_3, SCORE_3);
    private static final UserId USER_ID_4 = new UserId(1238l);
    private static final Score SCORE_4 = new Score(2100);
    private static final UserScore USER_SCORE_4 = new UserScore(USER_ID_4, SCORE_4);
    private static final SortedMap<Score, UserId> HIGHEST_SCORES = new TreeMap<Score, UserId>();
    public static final int MAX_NUMBER_OF_SCORES_RETURNED = 2;


    ScoreBoardManager testObj = new ScoreBoardManager();

    @Test
    public void addsScore() {
        // arrange

        // act
        testObj.addScore(LEVEL_ID, USER_SCORE_1);

        // assert
        SortedSet<UserScore> highestScores = testObj.getLevelScores(LEVEL_ID);
        assertEquals(1, highestScores.size());
        assertThat(highestScores, hasItem(USER_SCORE_1));
    }

    @Test
    public void twoScoresForSameLevelButDifferentUsersAreStoredInDescendingOrder() {
        // arrange
        testObj.addScore(LEVEL_ID, USER_SCORE_1);

        // act
        testObj.addScore(LEVEL_ID, USER_SCORE_2);

        // assert
        SortedSet<UserScore> highestScores = testObj.getLevelScores(LEVEL_ID);
        assertEquals(2, highestScores.size());
        assertThat(highestScores, hasItems(USER_SCORE_2, USER_SCORE_1));
    }

    @Test
    public void twoScoresForSameUserAndLevelStoresTheHighestOne() {
        // arrange
        testObj.addScore(LEVEL_ID, USER_SCORE_1);

        // act
        testObj.addScore(LEVEL_ID, USER_SCORE_1_UPDATE_2);

        // assert
        SortedSet<UserScore> highestScores = testObj.getLevelScores(LEVEL_ID);
        assertEquals(1, highestScores.size());
        assertThat(highestScores, hasItem(USER_SCORE_1_UPDATE_2));
    }

    @Test
    public void returnsOnlyMaxNumberOfScoresIfMoreExist() {
        // arrange
        testObj.setMaxNumberOfScores(2);
        testObj.addScore(LEVEL_ID, USER_SCORE_1);
        testObj.addScore(LEVEL_ID, USER_SCORE_2);
        testObj.addScore(LEVEL_ID, USER_SCORE_3);
        testObj.addScore(LEVEL_ID, USER_SCORE_4);

        // act
        SortedSet<UserScore> highestScores = testObj.getLevelScores(LEVEL_ID);

        // assert
        assertEquals(2, highestScores.size());
        assertThat(highestScores, hasItems(USER_SCORE_4, USER_SCORE_3));
    }

}
