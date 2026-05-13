package com.warehouse.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int code = Integer.parseInt(status.toString());
            if (code == 404) {
                String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
                if (path != null && !path.startsWith("/user") && !path.startsWith("/goods")
                        && !path.startsWith("/storage") && !path.startsWith("/record")
                        && !path.startsWith("/alert") && !path.startsWith("/excel")) {
                    return "forward:/index.html";
                }
            }
        }
        return null;
    }
}
