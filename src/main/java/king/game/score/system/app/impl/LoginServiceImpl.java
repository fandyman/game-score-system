package king.game.score.system.app.impl;

import king.game.score.system.app.client.LoginService;
import king.game.score.system.app.client.event.ErrorEvent;
import king.game.score.system.app.client.event.Event;
import king.game.score.system.app.client.event.ResponseEvent;
import king.game.score.system.domain.InvalidArgumentException;
import king.game.score.system.domain.UserId;
import king.game.score.system.domain.session.SessionManager;
import king.game.score.system.domain.session.SessionKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LoginServiceImpl implements LoginService {

    @Resource
    private SessionManager sessionManager;

    @Override
    public Event getSessionKey(Long userId) {
        Event responseEvent = null;
        try {
            String sessionKey = sessionManager.generatePlayerSession(new UserId(userId)).getSessionKey();
            responseEvent = new ResponseEvent(sessionKey);
        } catch (InvalidArgumentException e) {
            return new ErrorEvent(e.getMessage());
        }
        return responseEvent;
    }

    @Override
    public Event hasSessionKeyExpired(String sessionKey) {
        Boolean playerSessionExpired = sessionManager.isPlayerSessionExpired(new SessionKey(sessionKey));
        return new ResponseEvent(playerSessionExpired.toString());
    }
}
