package com.trelloiii.fotogram.service;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class UploadService {
    private final PasswordEncoder encoder;
    @Value("${domain}")
    private String domain;

    public UploadService(@Qualifier("crcEncoder") PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    private String extractFileName(String url) {
        return "./static/" + url.substring((domain + "img/").length());
    }

    @SneakyThrows
    private Path createTempAndTransfer(FilePart file, String originalFileName) {
        Path temp = new File("./static/tmp/" + originalFileName).toPath();
        file.transferTo(temp);
        return temp;
    }

    private String extractResultFileName(File tempFile, String originalFileName, long userId) {
        String fileExtension = FilenameUtils.getExtension(tempFile.getAbsolutePath());
        String hashedFileName = encoder.encode(originalFileName);
        long timestamp = System.currentTimeMillis();
        String hashedId = encoder.encode(String.valueOf(userId));
        return String.format("%s/%d_%s.%s", hashedId, timestamp, hashedFileName, fileExtension);
    }

    public Mono<Pair<Path, String>> uploadFile(FilePart file, long userId) {
        /*
            1. hash(file name)
            2. get current timestamp
            3. hash(user id)
            4. merge this values: (3)/(2)_(1).extension
         */
        final String originalFileName = file.filename();
        return Mono.just(createTempAndTransfer(file, originalFileName))
                .flatMap(temp -> {
                    String resultFileName = extractResultFileName(temp.toFile(), originalFileName, userId);
                    return Mono.just(Pair.of(temp, resultFileName));
                })
                .doOnSuccess(pair -> {
                    String resultFileName = pair.getSecond();
                    Path temp = pair.getFirst();
                    File f = new File("./static/" + resultFileName);
                    try {
                        FileUtils.copyFile(temp.toFile(), f);
                        FileUtils.forceDelete(temp.toFile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
    public void deleteFile(String url){
        String fileName = extractFileName(url);
        try {
            FileUtils.forceDelete(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
