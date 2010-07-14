package com.phatduckk.aljeers;

import com.phatduckk.aljeers.handler.BaseHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Aljeers {
    protected static final String CONF_HANDLERS = "conf.handlers";
    protected static final String CONF_PORT = "conf.port";

    protected static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server(getPort());
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        Map handlers = getHandlers();
        for (Object s : handlers.keySet()) {
            String uri = (String) s;
            String className = (String) handlers.get(uri);

            BaseHandler handler = (BaseHandler) Class.forName(className).newInstance();
            context.addServlet(new ServletHolder(handler), uri);
            context.addServlet(new ServletHolder(handler), uri + "/*");
        }

        server.start();
        server.join();
    }

    private static int getPort() {
        String port = System.getProperty(CONF_PORT);

        if (port == null) {
            return DEFAULT_PORT;
        }

        return Integer.parseInt(port);
    }

    public static Map getHandlers() throws Exception {
        String handlersConf = System.getProperty(CONF_HANDLERS);

        if (handlersConf == null) {
            throw new Exception("You need to specify -D" + CONF_HANDLERS);
        }

        Properties props = new Properties();
        InputStream stream = new FileInputStream(new File(handlersConf));

        if (stream == null) {
            throw new Exception(handlersConf + " could not be found in the classpath");
        }

        props.load(stream);
        return props;
    }
}
