package king.game.score.system.domain.scoreboard;

import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ScoreBoardManager {

    public static final int MAX_NUMBER_OF_SCORES_RETURNED = 15;
    private final ConcurrentMap<LevelId, SortedSet<UserScore>> allLevelsScores = new ConcurrentHashMap<>();
    private int maxNumberOfScores = MAX_NUMBER_OF_SCORES_RETURNED;

    public void setMaxNumberOfScores(int maxNumberOfScores) {
        this.maxNumberOfScores = maxNumberOfScores;
    }

    public SortedSet<UserScore> getLevelScores(LevelId levelId) {
        SortedSet<UserScore> cappedUserScores = new TreeSet<UserScore>();
        if (allLevelsScores.containsKey(levelId)) {
            SortedSet<UserScore> userScores = allLevelsScores.get(levelId);
            int count = 0;
            synchronized (ScoreBoardManager.class) {
                Iterator<UserScore> iterator = userScores.iterator();
                while (iterator.hasNext() && count < maxNumberOfScores) {
                    cappedUserScores.add(iterator.next());
                    count++;
                }
            }
        }
        return cappedUserScores;
    }

    public void addScore(LevelId levelId, UserScore newUserScore) {
        TreeSet<UserScore> levelScores = (TreeSet<UserScore>) allLevelsScores.get(levelId);
        if (levelScores == null) {
            levelScores = new TreeSet<UserScore>();
            allLevelsScores.put(levelId, levelScores);
        }
        synchronized (ScoreBoardManager.class) {
            Iterator<UserScore> it = levelScores.iterator();
            while (it.hasNext()) {
                UserScore userScore = it.next();
                if (userScore.getUserId().equals(newUserScore.getUserId()) && userScore.getScore() < newUserScore.getScore()) {
                    it.remove();
                    break;
                }
            }
            levelScores.add(newUserScore);
        }
    }
}
