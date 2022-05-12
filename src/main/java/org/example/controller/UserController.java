package org.example.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.attribute.RequestAttributes;
import org.example.dto.*;
import org.example.mime.ContentTypes;
import org.example.security.Authentication;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService service;
    private final Gson gson;

    public void getMe(final HttpServletRequest request,final HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final UserMeResponseDto responseData = service.getMe(auth);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void getAll(final HttpServletRequest request,final HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final List<UserGetAllResponseDTO> responseData = service.getAll(auth);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void register(final HttpServletRequest request,final HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final UserRegisterRequestDTO requestData = gson.fromJson(
                request.getReader(),
                UserRegisterRequestDTO.class
        );
        final UserRegisterResponseDTO responseData = service.register(auth, requestData);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void login(final HttpServletRequest request,final HttpServletResponse response) throws IOException {
        final UserLoginRequestDTO requestData = gson.fromJson(
                request.getReader(),
                UserLoginRequestDTO.class
        );
        final UserLoginResponseDTO responseData = service.login(requestData);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void create(final HttpServletRequest request,final HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final UserCreateRequestDTO requestData = gson.fromJson(
                request.getReader(),
                UserCreateRequestDTO.class
        );
        final UserCreateResponseDTO responseData = service.create(auth, requestData);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void changeRoles(final HttpServletRequest request,final HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final UserChangeRolesRequestDTO requestData = gson.fromJson(
                request.getReader(),
                UserChangeRolesRequestDTO.class
        );
        final UserChangeRolesResponseDTO responseData = service.changeRoles(auth, requestData);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }
}
