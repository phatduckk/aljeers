package com.phatduckk.aljeers.example;

import com.phatduckk.aljeers.handler.BaseHandler;
import com.phatduckk.aljeers.handler.annotations.GET;
import com.phatduckk.aljeers.handler.annotations.HEAD;
import com.phatduckk.aljeers.handler.annotations.POST;
import com.phatduckk.aljeers.handler.annotations.PUT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExampleHandler extends BaseHandler {
    @GET
    public Object sayHello(HttpServletRequest req, HttpServletResponse resp) {
        return "Hello there";
    }

    @GET
    public Object sayHelloTo(HttpServletRequest req, HttpServletResponse resp) {
        return "Hello there " + req.getParameter("name");
    }

    @GET
    public Object throwAnError(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        throw new Exception("balls");
    }

    @POST
    public Object postAUser(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = null;
        try {
            user = (User) getObjectFromRequest(req, User.class);
        } catch (IOException e) {
            throw new Exception("Include a JSON representation of a user in the POST body. Ex: {\"name\": \"John Doe\"}");
        }

        user.setEmail("arin@example.com");
        user.setBio("bla bla bla\nline 2 here");

        return user;
    }

    @HEAD
    public void headRequest(HttpServletRequest req, HttpServletResponse resp) {
        // noop
    }

    @PUT
    public String putRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String s = getRequestBody(req);
        if (s == null || s.equals("")) {
            throw new Exception("Include a body in the PUT request...");
        }

        return "you sent me: " + s;
    }

    @GET
    public Map returnAMap(HttpServletRequest req, HttpServletResponse resp) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("bla", "balls");
        map.put("hey", "you");

        return map;
    }
}
