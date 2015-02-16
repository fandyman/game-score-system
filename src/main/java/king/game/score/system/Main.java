package king.game.score.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.net.httpserver.HttpServerImpl;

import java.io.IOException;

public class Main {

    final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        LOG.info("Initializing Spring context.");

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/game-score-system-appContext.xml");
        HttpServerImpl httpServer = (HttpServerImpl) applicationContext.getBean("httpServer");
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();

        LOG.info("Spring context initialized and http server started");

    }

}
