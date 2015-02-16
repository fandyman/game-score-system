package king.game.score.system.domain;

public final class Validator {

    private Validator() {}

    public static void notNull(Object arg, String name) {
        if (arg == null) {
            throw new InvalidArgumentException(name + " is null! ");
        }
    }
    public static void check(boolean test, String message) {
        if (!test) {
            throw new InvalidArgumentException(message);
        }
    }

}