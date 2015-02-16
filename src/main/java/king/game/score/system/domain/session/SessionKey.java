package king.game.score.system.domain.session;

public class SessionKey {

    private final String sessionKey;

    public SessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionKey that = (SessionKey) o;

        if (!sessionKey.equals(that.sessionKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sessionKey.hashCode();
    }

    @Override
    public String toString() {
        return "SessionKey{" +
                "sessionKey='" + sessionKey + '\'' +
                '}';
    }
}
