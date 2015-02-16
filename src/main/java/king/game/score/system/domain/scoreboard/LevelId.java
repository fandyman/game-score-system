package king.game.score.system.domain.scoreboard;

import king.game.score.system.domain.Validator;
import king.game.score.system.domain.InvalidArgumentException;

public class LevelId {

    public static final String LEVEL_ID_MUST_BE_POSITIVE_VALUE = "Level id must be positive value";
    private final Integer value;

    public LevelId(Integer value) throws InvalidArgumentException {
        Validator.notNull(value, "Level id value mustn't be null");
        Validator.check(value > 0, LEVEL_ID_MUST_BE_POSITIVE_VALUE);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LevelId levelId = (LevelId) o;

        if (value != null ? !value.equals(levelId.value) : levelId.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
