package king.game.score.system.domain;

public class InvalidArgumentException extends UnrecoverableException {
    public InvalidArgumentException(String msg) {
        super(msg);
    }
}