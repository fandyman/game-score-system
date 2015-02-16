package king.game.score.system.domain.scoreboard;

import king.game.score.system.domain.Validator;
import king.game.score.system.domain.InvalidArgumentException;

public class Score {

    public static final String SCORE_MUST_BE_POSITIVE_OR_ZERO = "Score must be positive value or zero";
    private final Integer value;

    public Score(Integer value) throws InvalidArgumentException {
        Validator.notNull(value, "Score value mustn't be null");
        Validator.check(value >= 0, SCORE_MUST_BE_POSITIVE_OR_ZERO);
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Score score1 = (Score) o;

        if (value != null ? !value.equals(score1.value) : score1.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Score{" +
                "value=" + value +
                '}';
    }

}
