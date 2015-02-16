package king.game.score.system.app.client;

import king.game.score.system.app.client.event.Event;

public interface GameScoreService {

    Event addScore(Integer levelId, Integer score, String sessionKey);

    Event getHighestScores(Integer levelId);
}
