package king.game.score.system.domain;

public class UnrecoverableException extends RuntimeException {
    public UnrecoverableException(String msg) {
        super(msg);
    }
}