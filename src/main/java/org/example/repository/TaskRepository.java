package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.entity.TaskEntity;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TaskRepository {
    private final Jdbi jdbi;

    public TaskEntity saveFile(final TaskEntity entity) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        WITH saved AS (
                                        INSERT INTO tasks(id, user_id, status) VALUES (:id, :userId, :status)
                                        RETURNING id, user_id, status
                                        ) SELECT s.id, s.user_id, s.status FROM saved s 
                                        JOIN users u ON u.id = s.user_id
                                        """
                        )
                        .bind("id", entity.getId())
                        .bind("userId", entity.getUserId())
                        .bind("status", entity.isStatus())
                        .mapToBean(TaskEntity.class)
                        .one()
        );
    }

    public void updateStatus(final TaskEntity entity) {
        jdbi.withHandle(handle -> handle.createUpdate(
                                // language=PostgreSQL
                                """
                                        UPDATE tasks SET status = :status
                                        WHERE id = :id
                                        """
                        )
                        .bind("id", entity.getId())
                        .bind("status", entity.isStatus())
                        .execute()
        );
    }

    public Optional<TaskEntity> getById(final String id, final long userId) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        SELECT t.id, t.user_id, t.status 
                                        FROM tasks t
                                        JOIN users u ON u.id = t.user_id
                                        WHERE t.id = :id AND t.user_id = :user_id
                                        """
                        )
                        .bind("id", id)
                        .bind("user_id", userId)
                        .mapToBean(TaskEntity.class)
                        .findOne()
        );
    }

    public List<TaskEntity> getAllByUserId(long userId) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        SELECT t.id, t.user_id, u.login AS user_login, t.status 
                                        FROM tasks t 
                                        JOIN users u ON u.id = t.user_id
                                        WHERE t.user_id = :user_id
                                        """
                        )
                        .bind("user_id", userId)
                        .mapToBean(TaskEntity.class)
                        .list()
        );
    }

    public Optional<TaskEntity> getResultById(final String id, final long userId) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        SELECT t.id, t.user_id, t.status 
                                        FROM tasks t
                                        JOIN users u ON u.id = t.user_id
                                        WHERE t.id =:id AND t.user_id = :user_id AND t.status = TRUE
                                        """
                        )
                        .bind("id", id)
                        .bind("user_id", userId)
                        .mapToBean(TaskEntity.class)
                        .findOne()
        );
    }

    public List<TaskEntity> getAll() {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        SELECT t.id, t.user_id, u.login AS user_login, t.status 
                                        FROM tasks t 
                                        JOIN users u ON u.id = t.user_id
                                        """
                        )
                        .mapToBean(TaskEntity.class)
                        .list()
        );
    }

    public List<TaskEntity> getTaskByAdmin(final long userId) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        SELECT t.id, t.user_id, u.login AS user_login, t.status 
                                        FROM tasks t
                                        JOIN users u on u.id = t.user_id
                                        WHERE t.user_id = :user_id AND t.status = TRUE
                                        """
                        )
                        .bind("user_id", userId)
                        .mapToBean(TaskEntity.class)
                        .list()
        );
    }

    public Optional<TaskEntity> getFileById(final String id) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        SELECT t.id, t.user_id, t.status 
                                        FROM tasks t
                                        JOIN users u on u.id = t.user_id
                                        WHERE t.id =:id AND t.status = TRUE
                                        """
                        )
                        .bind("id", id)
                        .mapToBean(TaskEntity.class)
                        .findOne()
        );
    }
}
