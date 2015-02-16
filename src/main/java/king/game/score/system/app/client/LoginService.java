package king.game.score.system.app.client;

import king.game.score.system.app.client.event.Event;

public interface LoginService {

    Event getSessionKey(Long userId);

    Event hasSessionKeyExpired(String sessionKey);
}
