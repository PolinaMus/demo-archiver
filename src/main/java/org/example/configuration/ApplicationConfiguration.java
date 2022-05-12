package org.example.configuration;

import com.google.gson.Gson;
import org.example.controller.TaskController;
import org.example.controller.UserController;
import org.example.handler.Handler;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public DataSource dataSource() throws NamingException {
        final InitialContext ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:/comp/env/jdbc/ds");
    }

    @Bean
    public Jdbi jdbi(final DataSource dataSource) {
        return Jdbi.create(dataSource);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Bean
    public Map<String, Handler> routes(
            final UserController userController,
            final TaskController taskController
    ) {
        final Map<String, Handler> routes = new HashMap<>();
        routes.put("/users.me", userController::getMe);
        routes.put("/users.getAll", userController::getAll);
        routes.put("/users.register", userController::register);
        routes.put("/users.login", userController::login);
        routes.put("/users.create", userController::create);
        routes.put("/users.changeRoles", userController::changeRoles);
        routes.put("/multipart.create", taskController::saveFile);
        routes.put("/tasks.getById", taskController::getById);
        routes.put("/tasks.getAllByUserId", taskController::getAllByUserId);
        routes.put("/tasks.getResultById", taskController::getResultById);
        routes.put("/tasks.getAll", taskController::getAll);
        routes.put("/tasks.getTaskByAdmin", taskController::getTaskByAdmin);
        routes.put("/tasks.getFileById", taskController::getFileById);
        return Collections.unmodifiableMap(routes);
    }
}
