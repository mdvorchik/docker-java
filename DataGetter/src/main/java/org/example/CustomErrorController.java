package org.example;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        return String.format("<html><body><h2>Custom Error Page</h2><div>Status code: <b>%s</b></div>"
                        + "<div>Custom Error Controller: <b>%s</b></div><body></html>",
                statusCode, "Sorry, we have only / and /health paths");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}