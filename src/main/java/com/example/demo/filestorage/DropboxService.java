package com.example.demo.filestorage;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DropboxService {
    private DbxClientV2 client;

    @Value("${dropbox.app.key}")
    private String appKey;

    @Value("${dropbox.app.secret}")
    private String appSecret;

    @Value("${dropbox.app.ACCESS_TOKEN}")
    private String ACCESS_TOKEN;

    @PostConstruct
    private void initializeDropboxClient() throws DbxException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        this.client = new DbxClientV2(config, ACCESS_TOKEN);
        FullAccount account = this.client.users().getCurrentAccount();
    }

    // Method to upload a file to Dropbox
    public FileMetadata uploadFile(MultipartFile multipartFile) throws DbxException, IOException {
        // Convert MultipartFile to java.io.File
        File file = new File(multipartFile.getOriginalFilename());
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }
        // Upload to Dropbox
        try (InputStream in = new FileInputStream(file)) {
            FileMetadata metadata = this.client.files().uploadBuilder("/" + file.getPath()).uploadAndFinish(in);

            return metadata;
        } finally {
            // Delete the temporary file
            if (file.exists()) {
                file.delete();
            }
        }
    }

    // Method to download a file from Dropbox
    public void downloadFile(String dropboxPath, OutputStream outputStream) throws DbxException, IOException {
        client.files().downloadBuilder(dropboxPath).download(outputStream);
    }

    public List<String> listFiles(String folderPath) throws DbxException {
        List<String> fileNames = new ArrayList<>();
        ListFolderResult result = client.files().listFolder(folderPath);
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                if (metadata instanceof FileMetadata) {
                    fileNames.add(metadata.getName());
                }
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }
        return fileNames;
    }

    // Add more methods for file management as needed
}
