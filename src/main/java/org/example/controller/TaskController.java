package org.example.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.example.attribute.RequestAttributes;
import org.example.dto.*;
import org.example.mime.ContentTypes;
import org.example.security.Authentication;
import org.example.service.TaskService;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class TaskController {
    private final TaskService service;
    private final Gson gson;

    public void saveFile(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
        final Authentication auth = (Authentication) req.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final Part part = req.getPart("file");
        final TaskCreateResponseDTO responseData = service.createTask(auth, part);
        resp.getWriter().write(gson.toJson(responseData));
    }

    public void getById(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Authentication auth = (Authentication) req.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final String id = req.getParameter("id");
        final TaskGetByIdResponseDTO responseData = service.getById(auth, id);
        resp.setContentType(ContentTypes.APPLICATION_JSON);
        resp.getWriter().write(gson.toJson(responseData));
    }

    public void getAllByUserId(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Authentication auth = (Authentication) req.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final List<TaskGetAllByUserIdResponseDTO> responseData = service.getAllByUserId(auth);
        resp.setContentType(ContentTypes.APPLICATION_JSON);
        resp.getWriter().write(gson.toJson(responseData));
    }

    public void getResultById(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Authentication auth = (Authentication) req.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final String id = req.getParameter("id");
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment;filename=" + id + ".zip");
        service.getResultById(auth, id, resp.getOutputStream());
    }

    public void getAll(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Authentication auth = (Authentication) req.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final List<TaskGetAllByAdminDTO> responseData = service.getAll(auth);
        resp.setContentType(ContentTypes.APPLICATION_JSON);
        resp.getWriter().write(gson.toJson(responseData));
    }

    public void getTaskByAdmin(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Authentication auth = (Authentication) req.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final long id = Long.parseLong(req.getParameter("id"));
        final List<TaskGetByAdminDTO> responseData = service.getTaskByAdmin(auth, id);
        resp.setContentType(ContentTypes.APPLICATION_JSON);
        resp.getWriter().write(gson.toJson(responseData));
    }

    public void getFileById(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Authentication auth = (Authentication) req.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final String id = req.getParameter("id");
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment;filename=" + id + ".zip");
        service.getFileById(auth, id, resp.getOutputStream());
    }
}
