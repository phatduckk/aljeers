package com.phatduckk.aljeers;

import com.phatduckk.aljeers.handler.BaseHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

public class Aljeers {
    protected static final String CONF_HANDLERS = "conf.handlers";
    protected static final String CONF_PORT = "conf.port";
    protected static final int DEFAULT_PORT = 8080;

    protected Server server;
    protected ServletContextHandler context;

    public Aljeers() throws Exception {
        setupServer();
        setupHandlers(context);
    }

    public static void main(String[] args) throws Exception {
        Aljeers aljeers = new Aljeers();
        aljeers.startServer();
    }

    protected void startServer() throws Exception {
        server.start();
        server.join();
    }

    protected void setupServer() {
        server = new Server(getPort());
        context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
    }

    protected void setupHandlers(ServletContextHandler context) throws Exception {
        Properties handlers = (Properties) getHandlers();
        
        for (Object s : handlers.keySet()) {
            String uri = (String) s;
            String className = (String) handlers.get(uri);

            BaseHandler handler = (BaseHandler) Class.forName(className).newInstance();
            context.addServlet(new ServletHolder(handler), uri);
            context.addServlet(new ServletHolder(handler), uri + "/*");
        }
    }

    public static int getPort() {
        String port = System.getProperty(CONF_PORT);
        return (port == null) ? DEFAULT_PORT : Integer.parseInt(port);
    }

    public static Map getHandlers() throws Exception {
        String handlersConf = System.getProperty(CONF_HANDLERS);
        if (handlersConf == null) {
            throw new Exception("You need to specify -D" + CONF_HANDLERS);
        }

        File file = new File(handlersConf);
        if (! file.exists()) {
            throw new Exception("Conf file does not exist: " + handlersConf);
        }

        Properties props = new Properties();
        props.load(new FileInputStream(file));
        return props;
    }
}
