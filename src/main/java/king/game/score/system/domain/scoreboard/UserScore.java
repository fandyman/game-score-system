package king.game.score.system.domain.scoreboard;

import king.game.score.system.domain.UserId;
import king.game.score.system.domain.Validator;

public class UserScore implements Comparable<UserScore> {

    private final UserId userId;
    private final Score score;

    public UserScore(UserId userId, Score score) {
        Validator.notNull(userId, "User id mustn't be null");
        Validator.notNull(score, "Score mustn't be null");
        this.userId = userId;
        this.score = score;
    }

    public Long getUserId() {
        return userId.getValue();
    }

    public Integer getScore() {
        return score.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserScore userScore = (UserScore) o;

        if (score != null ? !score.equals(userScore.score) : userScore.score != null) return false;
        if (userId != null ? !userId.equals(userScore.userId) : userScore.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(UserScore userScore) {
        int v;

        v = userScore.getScore().compareTo(this.score.getValue());
        if (v != 0) return v;

        v = userScore.getUserId().compareTo(this.userId.getValue());
        if (v != 0) return v;

        return 0;
    }
}
