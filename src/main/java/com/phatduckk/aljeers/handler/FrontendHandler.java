package com.phatduckk.aljeers.handler;

import com.phatduckk.aljeers.Aljeers;
import com.phatduckk.aljeers.example.ExampleHandler;
import com.phatduckk.aljeers.handler.annotations.*;
import com.phatduckk.aljeers.http.AljeersRequest;
import com.phatduckk.aljeers.http.AljeersResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class FrontendHandler extends BaseHandler {
    private static boolean isVelocityInit = false;
    private static VelocityEngine ve;
    private static Map<String, String> handlers = getHandlers();

    static {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    }

    @GET
    public AljeersResponse index(AljeersRequest req) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("handlers", getHandlers());

        return render("templates/index.html", context);
    }

    @POST
    public AljeersResponse getMethods(AljeersRequest req) throws Exception {
        VelocityContext context = new VelocityContext();
        String handler = req.getParameter("handler");

        Class<?> aClass = Class.forName(handler);
        Method[] methods = aClass.getMethods();
        Map<String, String> validMethods = new HashMap<String, String>();

        for (Method method : methods) {
            if (! Modifier.isPublic(method.getModifiers())) {
                continue;
            }

            Annotation[] annotations = method.getAnnotations();
            if (annotations.length == 0) {
                continue;
            }

            for (Annotation annotation : annotations) {
                if (annotation instanceof GET
                        || annotation instanceof PUT
                        || annotation instanceof POST
                        || annotation instanceof DELETE
                        || annotation instanceof HEAD) {

                    String simpleName = annotation.annotationType().getSimpleName();
                    validMethods.put(method.getName(), simpleName);
                }
            }
        }

        if (ExampleHandler.class.getCanonicalName().equals(handler)) {
            validMethods.put("-invalidMethod", "GET");
        }

        context.put("methods", validMethods);
        return render("templates/methodList.html", context);
    }

    public AljeersResponse render(String templateFile, VelocityContext context) throws IOException {
        StringWriter writer = new StringWriter();

        try {
            Template template = getVelocityEngine().getTemplate(templateFile, "UTF-8");
            template.merge(context, writer);
        } catch (Exception e) {
            writer.write(e.getMessage());
        } finally {
            writer.flush();
            writer.close();
        }

        AljeersResponse resp = new AljeersResponse(writer.toString());
        resp.addHeader("Content-Type", "text/html");
        resp.setRespondRawString(true);
        return resp;
    }

    private static VelocityEngine getVelocityEngine() throws Exception {
        if (!isVelocityInit) {
            ve.init();
            isVelocityInit = true;
        }

        return ve;
    }

    protected static Map<String, String> getHandlers() {
        Map<String, String> rtn = new HashMap<String, String>();

        try {
            Map<String, String> map = Aljeers.getHandlers();

            for (String uriPrefix : map.keySet()) {
                String className = map.get(uriPrefix);
                if (! className.equals(FrontendHandler.class.getCanonicalName())) {
                    rtn.put(uriPrefix, className);
                }
            }

            return rtn;
        } catch (Exception e) {
            return new HashMap();
        }
    }
}
