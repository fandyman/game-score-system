package king.game.score.system.app.client.event;

public class ErrorEvent implements Event{
    private final String responseBody;

    public ErrorEvent(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
