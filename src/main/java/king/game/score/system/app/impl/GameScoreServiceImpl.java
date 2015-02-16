package king.game.score.system.app.impl;

import java.util.Iterator;
import java.util.SortedSet;

import king.game.score.system.app.client.GameScoreService;
import king.game.score.system.app.client.event.ErrorEvent;
import king.game.score.system.app.client.event.ResponseEvent;
import king.game.score.system.domain.InvalidArgumentException;
import king.game.score.system.domain.UserId;
import king.game.score.system.domain.scoreboard.LevelId;
import king.game.score.system.domain.scoreboard.Score;
import king.game.score.system.domain.session.SessionManager;
import king.game.score.system.app.client.event.Event;
import king.game.score.system.domain.scoreboard.ScoreBoardManager;
import king.game.score.system.domain.scoreboard.UserScore;
import king.game.score.system.domain.session.SessionKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GameScoreServiceImpl implements GameScoreService {

    @Resource
    private ScoreBoardManager scoreBoardManager;
    @Resource
    private SessionManager sessionManager;

    @Override
    public Event addScore(Integer levelId, Integer score, String sessionKey) {
        UserId userId = sessionManager.getUserId(new SessionKey(sessionKey));
        Event responseEvent = ResponseEvent.emptyBody();
        try {
            scoreBoardManager.addScore(new LevelId(levelId), new UserScore(userId, new Score(score)));
        } catch (InvalidArgumentException e) {
            responseEvent = new ErrorEvent(e.getMessage());
        }
        return responseEvent;
    }

    @Override
    public Event getHighestScores(Integer levelId) {
        Event responseEvent = null;
        try {
            SortedSet<UserScore> highestScores = scoreBoardManager.getLevelScores(new LevelId(levelId));
            responseEvent = new ResponseEvent(formatCSV(highestScores));
        } catch (InvalidArgumentException e) {
            responseEvent = new ErrorEvent(e.getMessage());
        }
        return responseEvent;
    }

    private String formatCSV(SortedSet<UserScore> highestScores) {
        StringBuilder sb = new StringBuilder();
        Iterator<UserScore> iterator = highestScores.iterator();
        int entryCount = 0;
        while (iterator.hasNext()) {
            UserScore entry = iterator.next();
            sb.append(entry.getUserId());
            sb.append("=");
            sb.append(entry.getScore());
            entryCount++;
            if (entryCount!=highestScores.size()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
