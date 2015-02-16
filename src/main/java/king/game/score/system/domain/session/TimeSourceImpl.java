package king.game.score.system.domain.session;

import org.springframework.stereotype.Component;

@Component
public class TimeSourceImpl implements TimeSource {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
