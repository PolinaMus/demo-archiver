package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.entity.UserEntity;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final Jdbi jdbi;

    public List<UserEntity> getAll() {
        return jdbi.withHandle(handle -> handle.createQuery(
                        // language=PostgreSQL
                        "SELECT id, login, roles FROM users ORDER BY id"
                )
                .mapToBean(UserEntity.class)
                .list());
    }

    public Optional<UserEntity> findByLogin(String login) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                "SELECT id, login, password, roles FROM users WHERE login = :login"
                        )
                        .bind("login", login)
                        .mapToBean(UserEntity.class)
                        .findOne()
        );
    }

    public UserEntity save(UserEntity entity) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                INSERT INTO users(login, password, roles) VALUES (:login, :password, :roles)
                                RETURNING id, login, password, roles
                                """
                        )
                        .bind("login", entity.getLogin())
                        .bind("password", entity.getPassword())
                        .bind("roles", entity.getRoles())
                        .mapToBean(UserEntity.class)
                        .one()
        );
    }

    public UserEntity setRolesByLogin(String login, String[] roles) {
        return jdbi.withHandle(handle -> handle.createQuery(
                // language=PostgreSQL
                """
                UPDATE users SET roles = :roles WHERE login = :login
                RETURNING id, login, roles
                """
            )
                .bind("roles", roles)
                .bind("login", login)
                .mapToBean(UserEntity.class)
                .one()
        );
    }
}
