package king.game.score.system.domain;

public class UserId {

    public static final String USER_ID_MUST_BE_POSITIVE_VALUE = "User id must be positive value";
    private final Long value;

    public UserId(Long value) throws InvalidArgumentException {
        Validator.notNull(value, "User id value mustn't be null");
        Validator.check(value > 0, USER_ID_MUST_BE_POSITIVE_VALUE);
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserId userId1 = (UserId) o;

        if (value != null ? !value.equals(userId1.value) : userId1.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserId{" +
                "value=" + value +
                '}';
    }
}
