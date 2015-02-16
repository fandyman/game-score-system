package king.game.score.system.app.client.event;

public class ResponseEvent implements Event {
    public static final String EMPTY_BODY = "";
    private final String responseBody;

    public ResponseEvent(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public static ResponseEvent emptyBody() {
        return new ResponseEvent(EMPTY_BODY);
    }
}
