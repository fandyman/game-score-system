package king.game.score.system.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RequestHandler implements HttpHandler {

    public static final String LOGIN_PATH_REGEX = "^/[0-9]+/login$";
    public static final String HIGH_SCORE_LIST_PATH_REGEX = "^/[0-9]+/highscorelist$";
    public static final String SESSION_KEY_QUERY_REGEX = "^sessionkey=[a-zA-Z0-9_]+$";
    public static final String ADD_SCORE_PATH_REGEX = "^/[0-9]+/score$";
    public static final String SCORE_REGEX = "^[0-9]+$";

    @Resource(name = "textResponseWriter")
    private ResponseWriter responseWriter;
    @Resource
    private RequestProcessor requestProcessor;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();
        String query = httpExchange.getRequestURI().getQuery();
        String sessionKey = null;
        if (query != null) {
            sessionKey = queryToMap(query).get("sessionkey");
        }
        String body = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody())).readLine();

        HttpResponseEvent responseEvent = HttpResponseEvent.badRequestEvent();

        if (matchesURIPathAndQuery(LOGIN_PATH_REGEX,null, path, null)) {
            String userId = path.split("/")[1];
            responseEvent = handleLogIn(userId);
        } else{
            String levelId = path.split("/")[1];
            if (matchesURIPathAndQuery(HIGH_SCORE_LIST_PATH_REGEX, SESSION_KEY_QUERY_REGEX, path, query)) {
                responseEvent = handleGetHighestScoreList(sessionKey, levelId);
            } else if (matchesURIPathAndQuery(ADD_SCORE_PATH_REGEX, SESSION_KEY_QUERY_REGEX, path, query)
                    && matchesBody(SCORE_REGEX, body)) {
                responseEvent = handleAddScore(sessionKey, body, levelId);
            }
        }
        responseWriter.write(httpExchange, responseEvent.getResponseCode(), responseEvent.getResponseBody());
    }

    private HttpResponseEvent handleLogIn(String userId) {
        return requestProcessor.logIn(userId);
    }

    private HttpResponseEvent handleAddScore(String sessionKey, String body, String levelId) {
        HttpResponseEvent responseEvent;
        if (requestProcessor.hasSessionExpired(sessionKey)) {
            responseEvent = HttpResponseEvent.sessionExpiredEvent();
        } else {
            responseEvent = requestProcessor.addScore(levelId, body, sessionKey);
        }
        return responseEvent;
    }

    private HttpResponseEvent handleGetHighestScoreList(String sessionKey, String levelId) {
        HttpResponseEvent responseEvent;
        if (requestProcessor.hasSessionExpired(sessionKey)) {
            responseEvent = HttpResponseEvent.sessionExpiredEvent();
        }
        else {
            responseEvent = requestProcessor.getHighestScores(levelId);
        }
        return responseEvent;
    }

    private boolean matchesBody(String regex, String body) throws IOException {
        boolean outcome = false;
        if (body != null) {
            Matcher pm = getMatcher(regex, body);
            outcome = pm.find();
        }
        return outcome;
    }

    private boolean matchesURIPathAndQuery(String pathRegex, String queryRegex, String path, String query) {
        Matcher pm = getMatcher(pathRegex, path);
        boolean queryMatched = true;
        if (query != null) {
            Matcher qm = getMatcher(queryRegex, query);
            queryMatched = qm.find();
        }
        return pm.find() && queryMatched;
    }

    private Matcher getMatcher(String pathPattern, String path) {
        Pattern pp = Pattern.compile(pathPattern);
        return pp.matcher(path);
    }

    private Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

}