package king.game.score.system.app.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import king.game.score.system.app.client.event.ErrorEvent;
import king.game.score.system.domain.UserId;
import king.game.score.system.domain.session.SessionManager;
import king.game.score.system.app.client.event.Event;
import king.game.score.system.app.client.event.ResponseEvent;
import king.game.score.system.domain.InvalidArgumentException;
import king.game.score.system.domain.session.SessionKey;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceImplTest {

    private Long userId;
    public static final SessionKey TEST_SESSION = new SessionKey("testSession");

    @Mock
    SessionManager sessionManager;
    @InjectMocks
    LoginServiceImpl testObj;

    @Test
    public void getsSessionForPlayerSuccessfull() throws InvalidArgumentException {
        // arrange
        userId = 1234l;
        Mockito.when(sessionManager.generatePlayerSession(new UserId(userId))).thenReturn(TEST_SESSION);

        // act
        Event sessionKeyResponseEvent = testObj.getSessionKey(userId);

        // assert
        Assert.assertThat(sessionKeyResponseEvent, instanceOf(ResponseEvent.class));
        assertThat(sessionKeyResponseEvent.getResponseBody(), is(TEST_SESSION.getSessionKey()));
    }

    @Test
    public void getsSessionForPlayerWithNegativeUserIdError() throws InvalidArgumentException {
        // arrange
        Long userId= -1l;

        // act
        Event sessionKeyResponseEvent = testObj.getSessionKey(userId);

        // assert
        Assert.assertThat(sessionKeyResponseEvent, instanceOf(ErrorEvent.class));
        assertThat(sessionKeyResponseEvent.getResponseBody(), CoreMatchers.is(UserId.USER_ID_MUST_BE_POSITIVE_VALUE));
    }

}
