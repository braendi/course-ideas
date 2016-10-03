package com.teamtreehouse.courses;

import com.teamtreehouse.courses.model.CourseIdea;
import com.teamtreehouse.courses.model.CourseIdeaDAO;
import com.teamtreehouse.courses.model.NotFoundException;
import com.teamtreehouse.courses.model.SimpleCourseIdeaDAO;
import spark.ModelAndView;
import spark.Request;
import spark.Spark;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    public static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {

        Spark.staticFileLocation("/public");

        CourseIdeaDAO dao = new SimpleCourseIdeaDAO();

        // Authentication filters
        before((request, response) -> {
            String username = request.cookie("username");
            if (username != null) {
                request.attribute("username", username);
            }
        });

        before("/ideas", (request, response) -> {
            boolean authenticated = request.attribute("username") != null;
            if (!authenticated) {
                setFlashMessage(request, "You have to first log in to view ideas");
                response.redirect("/");
                halt();
            }
        });

        // Exception Handling
        exception(NotFoundException.class, (exception, request, response) -> {
            response.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(new ModelAndView(null, "not-found.hbs"));
            response.body(html);
        });

        // Requests
        get("/", (request, response) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", request.attribute("username"));
            model.put("flashMessage", captureFlashMessage(request));
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        post("/sign-in", (request, response) -> {
            Map<String, String> model = new HashMap<>();
            String username = request.queryParams("username");
            response.cookie("username", username);
            model.put("username", username);
            response.redirect("/");
            return null;
        });

        get("/ideas", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("ideas", dao.findAll());
            model.put("flashMessage", captureFlashMessage(request));
            return new ModelAndView(model, "ideas.hbs");
        }, new HandlebarsTemplateEngine());

        post("/ideas", (request, response) -> {
            String title = request.queryParams("title");
            String username = request.attribute("username");
            CourseIdea idea = new CourseIdea(title, username);
            dao.add(idea);
            response.redirect("/ideas");
            return null;
        });

        post("/ideas/:slug/vote", (request, response) -> {
            CourseIdea idea = dao.findBySlug(request.params("slug"));
            boolean voted = idea.addVoter(request.attribute("username"));
            if (voted) {
                setFlashMessage(request, "Thank you for voting!");
            } else {
                setFlashMessage(request, "You already voted!");
            }
            response.redirect("/ideas");
            return null;
        });

        get("/ideas/:slug", (request, response) -> {
            CourseIdea idea = dao.findBySlug(request.params("slug"));
            Map<String, Object> model = new HashMap<>();
            model.put("idea", idea);
            return new ModelAndView(model, "idea.hbs");
        },  new HandlebarsTemplateEngine());
    }

    private static void setFlashMessage(Request request, String message) {
        request.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request request) {
        if (request.session(false) == null) {
            return null;
        }
        if (!request.session().attributes().contains (FLASH_MESSAGE_KEY)) {
            return null;
        }
        return request.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request request) {
        String message = getFlashMessage(request);
        if (message != null) {
            request.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }
}
