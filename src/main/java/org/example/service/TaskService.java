package org.example.service;

import jakarta.servlet.http.Part;
import org.example.dto.*;
import org.example.entity.TaskEntity;
import org.example.repository.TaskRepository;
import org.example.security.Authentication;
import org.example.security.ForbiddenException;
import org.example.security.NotFoundException;
import org.example.security.Roles;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class TaskService {
    private final TaskRepository repository;
    private final Path media = Paths.get("media");
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(64, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskCreateResponseDTO createTask(final Authentication auth, final Part part) throws IOException {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        final String id = UUID.randomUUID().toString();
        final TaskEntity saved = repository.saveFile(new TaskEntity(
                id,
                auth.getId(),
                "",
                false
        ));
        executor.execute(() -> {
            try (
                    final InputStream inputStream = part.getInputStream();
                    final ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(media.resolve(id + ".zip").toAbsolutePath().toString()));
            ) {
                final ZipEntry zipEntry = new ZipEntry(part.getSubmittedFileName());
                zipOutputStream.putNextEntry(zipEntry);
                inputStream.transferTo(zipOutputStream);
                zipOutputStream.closeEntry();
                final TaskEntity update = new TaskEntity(saved.getId(), saved.getUserId(), saved.getUserLogin(), true);
                repository.updateStatus(update);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return new TaskCreateResponseDTO(id, saved.isStatus());
    }

    public TaskGetByIdResponseDTO getById(final Authentication auth, final String id) {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        return repository.getById(id, auth.getId())
                .map(o -> new TaskGetByIdResponseDTO(
                        o.getId(),
                        o.isStatus()
                ))
                .orElseThrow(NotFoundException::new)
                ;
    }

    public List<TaskGetAllByUserIdResponseDTO> getAllByUserId(final Authentication auth) {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        return repository.getAllByUserId(auth.getId())
                .stream()
                .map(o -> new TaskGetAllByUserIdResponseDTO(
                        o.getId(),
                        o.getUserLogin(),
                        o.isStatus()
                ))
                .collect(Collectors.toList())
                ;
    }

    public void getResultById(final Authentication auth, final String id, final OutputStream stream) throws IOException {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        final TaskEntity saved = repository.getResultById(id, auth.getId())
                .orElseThrow(NotFoundException::new);

        final Path zipPath = media.resolve(saved.getId() + ".zip");
        Files.copy(zipPath, stream);
    }

    public List<TaskGetAllByAdminDTO> getAll(final Authentication auth) {
        if (!auth.hasRole(Roles.TASKS_VIEW_ALL)) {
            throw new ForbiddenException();
        }
        return repository.getAll()
                .stream()
                .map(o -> new TaskGetAllByAdminDTO(
                        o.getId(),
                        o.getUserId(),
                        o.getUserLogin(),
                        o.isStatus()
                ))
                .collect(Collectors.toList())
                ;
    }

    public List<TaskGetByAdminDTO> getTaskByAdmin(final Authentication auth, final long userId) {
        if (!auth.hasRole(Roles.TASKS_VIEW_ALL)) {
            throw new ForbiddenException();
        }
        return repository.getTaskByAdmin(userId).stream()
                .map(o -> new TaskGetByAdminDTO(
                        o.getId(),
                        o.getUserLogin(),
                        o.isStatus()
                ))
                .collect(Collectors.toList())
                ;
    }

    public void getFileById(final Authentication auth, final String id, final OutputStream stream) throws IOException {
        if (!auth.hasRole(Roles.TASKS_DOWNLOAD_ALL)) {
                throw new ForbiddenException();
            }
        final TaskEntity saved = repository.getFileById(id)
                .orElseThrow(NotFoundException::new);

        final Path zipPath = media.resolve(saved.getId() + ".zip");
        Files.copy(zipPath, stream);
    }
}
