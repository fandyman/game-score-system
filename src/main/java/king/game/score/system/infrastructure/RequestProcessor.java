package king.game.score.system.infrastructure;

import king.game.score.system.app.client.GameScoreService;
import king.game.score.system.app.client.LoginService;
import king.game.score.system.app.client.event.ErrorEvent;
import king.game.score.system.app.client.event.Event;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RequestProcessor {

    @Resource
    private GameScoreService gameScoreService;
    @Resource
    private LoginService loginService;

    public HttpResponseEvent addScore(String levelIdString, String scoreString, String sessionKeyString) {
        Event responseEvent;
        Integer levelId = Integer.valueOf(levelIdString);
        Integer score = Integer.valueOf(scoreString);
        responseEvent = gameScoreService.addScore(levelId, score, sessionKeyString);
        return new HttpResponseEvent(responseEvent.getResponseBody(), resolveReturnCode(responseEvent));
    }

    public HttpResponseEvent getHighestScores(String levelIdString) {
        Event responseEvent;
        Integer levelId = Integer.valueOf(levelIdString);
        responseEvent = gameScoreService.getHighestScores(levelId);
        return new HttpResponseEvent(responseEvent.getResponseBody(), resolveReturnCode(responseEvent));
    }

    public boolean hasSessionExpired(String sessionKey) {
        Event responseEvent;
        responseEvent = loginService.hasSessionKeyExpired(sessionKey);
        return Boolean.valueOf(responseEvent.getResponseBody()).booleanValue();
    }

    public HttpResponseEvent logIn(String userIdString) {
        Event responseEvent;
        long userid = Long.valueOf(userIdString);
        responseEvent = loginService.getSessionKey(userid);
        return new HttpResponseEvent(responseEvent.getResponseBody(), resolveReturnCode(responseEvent));
    }

    private HttpResponseCode resolveReturnCode(Event responseEvent) {
        HttpResponseCode httpResponseCode = HttpResponseCode._200_OK;
        if (responseEvent instanceof ErrorEvent) {
            httpResponseCode = HttpResponseCode._404_NOT_FOUND;
        }
        return  httpResponseCode;
    }

}
