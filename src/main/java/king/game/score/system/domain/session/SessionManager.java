package king.game.score.system.domain.session;

import king.game.score.system.domain.UserId;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionManager {

    public static final long TEN_MINUTES = 600000l;
    public static final long SESSION_EXPIRY_TIME = TEN_MINUTES;

    @Resource
    private TimeSource timeSource;

    private SecureRandom random = new SecureRandom();
    private Map<UserId, Session> playersSessions = new HashMap<UserId, Session>();
    private Map<SessionKey, Session> sessionKeyToSessionMap = new HashMap<SessionKey, Session>();

    public boolean isPlayerSessionExpired(SessionKey sessionKey) {
        boolean isExpired = true;
        if (sessionKeyToSessionMap.containsKey(sessionKey)) {
            isExpired = sessionKeyToSessionMap.get(sessionKey).isExpired();
        }
        return isExpired;
    }

    public UserId getUserId(SessionKey sessionKey) {
        UserId userId = null;
        Session session = sessionKeyToSessionMap.get(sessionKey);
        if (session != null) {
            userId = session.getUserId();
        }
        return userId;
    }

    private class Session {
        private final SessionKey sessionKey;
        private final UserId userId;
        private final long creationTimestamp;

        public Session(SessionKey sessionKey, UserId userId, long creationTimestamp) {
            this.sessionKey = sessionKey;
            this.userId = userId;
            this.creationTimestamp = creationTimestamp;
        }

        public SessionKey getSessionKey() {
            return sessionKey;
        }

        public long getCreationTimestamp() {
            return creationTimestamp;
        }

        public UserId getUserId() {
            return userId;
        }

        public boolean isExpired() {
            return timeSource.currentTimeMillis() - getCreationTimestamp() > SESSION_EXPIRY_TIME;
        }
    }


    public SessionKey generatePlayerSession(UserId userId) {
        Session session = null;
        synchronized (SessionManager.class) {
            session = playersSessions.get(userId);
            if (session == null || session.isExpired()) {
                session = generateSession(userId);
                playersSessions.put(userId, session);
                sessionKeyToSessionMap.put(session.getSessionKey(), session);
            }
        }
        return session.getSessionKey();
    }

    public Map<UserId, Session> getPlayersSessions() {
        return new HashMap<UserId, Session>(playersSessions);
    }

    private Session generateSession(UserId userId) {
        return new Session(new SessionKey(new BigInteger(130, random).toString(32)), userId, timeSource.currentTimeMillis());
    }
}
