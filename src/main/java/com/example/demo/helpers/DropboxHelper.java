package com.example.demo.helpers;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

public class DropboxHelper {

    private DbxClientV2 client;

    public DropboxHelper(DbxClientV2 client) {
        this.client = client;
    }

    public boolean folderExists(String path) {
        try {
            ListFolderResult result = client.files().listFolder(path);
            for (Metadata metadata : result.getEntries()) {
                if (metadata instanceof FolderMetadata) {
                    return true;
                }
            }
        } catch (ListFolderErrorException e) {
            if (e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public FolderMetadata createFolder(String path) throws Exception {
        if (!folderExists(path)) {
            return client.files().createFolderV2(path).getMetadata();
        } else {
            throw new Exception("Папка уже существует");
        }
    }
}

