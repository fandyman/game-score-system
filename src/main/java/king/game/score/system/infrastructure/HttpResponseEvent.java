package king.game.score.system.infrastructure;

public class HttpResponseEvent {

    public static final String SESSION_EXPIRED_RESPONSE_BODY = "Session expired";
    public static final String BAD_REQUEST_RESPONSE_BODY = "Bad request";

    private final String responseBody;
    private final HttpResponseCode responseCode;

    public HttpResponseEvent(String responseBody, HttpResponseCode responseCode) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpResponseCode getResponseCode() {
        return responseCode;
    }

    public static HttpResponseEvent sessionExpiredEvent() {
        return new HttpResponseEvent(SESSION_EXPIRED_RESPONSE_BODY, HttpResponseCode._403_FORBIDDEN);
    }

    public static HttpResponseEvent badRequestEvent() {
        return new HttpResponseEvent(BAD_REQUEST_RESPONSE_BODY, HttpResponseCode._404_NOT_FOUND);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpResponseEvent that = (HttpResponseEvent) o;

        if (responseBody != null ? !responseBody.equals(that.responseBody) : that.responseBody != null) return false;
        if (responseCode != that.responseCode) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = responseBody != null ? responseBody.hashCode() : 0;
        result = 31 * result + (responseCode != null ? responseCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HttpResponseEvent{" +
                "responseBody='" + responseBody + '\'' +
                ", responseCode=" + responseCode +
                '}';
    }
}
