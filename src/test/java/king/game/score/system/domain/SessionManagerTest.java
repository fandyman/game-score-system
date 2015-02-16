package king.game.score.system.domain;

import king.game.score.system.domain.session.TimeSource;
import king.game.score.system.Main;
import king.game.score.system.domain.session.SessionKey;
import king.game.score.system.domain.session.SessionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest {

    private static final UserId USER_ID = new UserId(1234l);
    public static final long TEN_MINUTES_MINUS_ONE_MILLIS = 599999l;
    public static final long TEN_MINUTES_PLUS_ONE_MILLIS = 600001l;

    @Mock
    TimeSource timeSource;

    @InjectMocks
    SessionManager testObj;

    @Test
    public void testReturnsGeneratedSessionKey() {
        // arrange
        Mockito.when(timeSource.currentTimeMillis()).thenReturn(System.currentTimeMillis()).thenReturn(System.currentTimeMillis() + TEN_MINUTES_MINUS_ONE_MILLIS);

        SessionKey sessionKey = testObj.generatePlayerSession(USER_ID);

        // act
        SessionKey sessionKeyBeforeExpiry = testObj.generatePlayerSession(USER_ID);

        // assert
        assertEquals(sessionKey, sessionKeyBeforeExpiry);
    }

    @Test
    public void testExpiredSessionGeneratesNewSessionKey() {
        // arrange
        Mockito.when(timeSource.currentTimeMillis()).thenReturn(System.currentTimeMillis()).thenReturn(System.currentTimeMillis() + TEN_MINUTES_PLUS_ONE_MILLIS);

        SessionKey sessionKey = testObj.generatePlayerSession(USER_ID);

        // act
        SessionKey sessionKeyAfterExpiry = testObj.generatePlayerSession(USER_ID);

        // assert
        assertEquals(1, testObj.getPlayersSessions().size());
        assertNotEquals(sessionKey, sessionKeyAfterExpiry);
    }
}
