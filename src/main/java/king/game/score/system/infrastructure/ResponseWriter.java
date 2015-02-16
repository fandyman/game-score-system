package king.game.score.system.infrastructure;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface ResponseWriter {
    void write(HttpExchange httpExchange, HttpResponseCode httpResponseCode, String response) throws IOException;
}
