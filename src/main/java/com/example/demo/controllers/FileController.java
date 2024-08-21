package com.example.demo.controllers;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import com.example.demo.config.JwtService;
import com.example.demo.filestorage.DropboxService;
import com.example.demo.helpers.DropboxHelper;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.services.TaskService;
import com.example.demo.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@AllArgsConstructor
public class FileController {
    @Autowired
    private DbxClientV2 dbxClient;
    @Autowired
    private DropboxService dropboxService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private TaskService taskService;

    @PostMapping("/upload/{fileType}")
    public ResponseEntity<String> uploadFile(
            @PathVariable String fileType,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        String path;
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (fileType.equals("avatar")) {
            path = "/users/" + userId + "/avatar.jpg"; // Исправленный путь
        } else {
            path = "/tasks/" + fileType + "/" + file.getOriginalFilename(); // Исправленный путь
        }
        InputStream fileStream = file.getInputStream();
        dbxClient.files().uploadBuilder(path) // Используйте исправленный путь здесь
                .withMode(WriteMode.OVERWRITE) // Добавлено для перезаписи существующего файла
                .uploadAndFinish(fileStream);
        fileStream.close();

        if (fileType.equals("avatar")) {
            UUID userIdf = jwtService.getUserIdFromRequest(request);
            Optional<User> existingUser = userService.getUserById(userIdf);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setAvatar(path); // Предполагается, что у User есть поле avatarPath
                userService.updateUser(userId, user);
            }
        }
        // Сохранение файла в указанной директории
        // Здесь должен быть код для сохранения файла в файловой системе
        return ResponseEntity.ok("Файл успешно загружен.");
    }



    @GetMapping("/download/avatar")
    public ResponseEntity<String> getUserAvatar(HttpServletResponse response) {

        UUID userId = jwtService.getUserIdFromRequest(request);
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String avatarPath = user.getAvatar();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    dropboxService.downloadFile(avatarPath, baos);
                    byte[] imageData = baos.toByteArray();
                    String base64Image = Base64.getEncoder().encodeToString(imageData);
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(base64Image);
                } catch (DbxException | IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }



    @GetMapping("/download/task-images/{taskId}")
    public ResponseEntity<List<String>> downloadTaskImages(@PathVariable UUID taskId, HttpServletResponse response) {
        String taskFolderPath = "/" + taskId.toString(); // Путь к папке с изображениями задачи
        try {
            List<String> fileNames = dropboxService.listFiles(taskFolderPath);
            if (fileNames.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<String> downloadLinks = new ArrayList<>();
            for (String filename : fileNames) {
                String fileDownloadPath = taskFolderPath + "/" + filename;
                downloadLinks.add(fileDownloadPath); // Ссылки для скачивания файлов
            }
            return new ResponseEntity<>(downloadLinks, HttpStatus.OK);
        } catch (DbxException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
