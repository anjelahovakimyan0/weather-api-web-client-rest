package am.itspace.photoshootprojectmanagementweb.fileComponent;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileComponent {

    @Value("${project.picture.upload.directory}")
    private String uploadDirectory;

    public String uploadPicture(MultipartFile multipartFile) {
        String picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        File file = new File(uploadDirectory, picName);

        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return picName;
    }

    public byte[] getPicture(String picName) {
        File file = new File(uploadDirectory, picName);

        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                return IOUtils.toByteArray(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public void deletePicture(String picName) {
        Path filePath = Paths.get(uploadDirectory, picName);

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
