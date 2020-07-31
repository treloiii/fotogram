package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.PhotoRepository;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import com.trelloiii.fotogram.service.facade.PhotoServiceFacade;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PhotoServiceFacade photoServiceFacade;
    private final PasswordEncoder encoder;
    @Value("${domain}")
    private String domain;

    public PhotoService(PhotoRepository photoRepository, PhotoServiceFacade photoServiceFacade, @Qualifier("crcEncoder") PasswordEncoder encoder) {
        this.photoRepository = photoRepository;
        this.photoServiceFacade = photoServiceFacade;
        this.encoder = encoder;
    }

    public Mono<EntityContainer<Photo>> getPhotoById(Long id){
        return photoRepository.findFullPhotoById(id);
    }
    public Mono<Page<Photo>> getUserPhotos(String username, Pageable pageable){
        return photoRepository.findByOwnerUsername(username,pageable);
    }

    @SneakyThrows
    private Path createTempAndTransfer(FilePart file,String originalFileName){
        Path temp = new File("./static/tmp/"+originalFileName).toPath();
        file.transferTo(temp);
        return temp;
    }
    private String extractResultFileName(File tempFile,String originalFileName,long userId){
        String fileExtension = FilenameUtils.getExtension(tempFile.getAbsolutePath());
        String hashedFileName = encoder.encode(originalFileName);
        long timestamp = System.currentTimeMillis();
        String hashedId = encoder.encode(String.valueOf(userId));
        return String.format("%s/%d_%s.%s",hashedId,timestamp,hashedFileName,fileExtension);
    }
    @SneakyThrows
    public Mono<Photo> uploadAndGet(FilePart file, String caption, long userId){
        /*
            1. hash(file name)
            2. get current timestamp
            3. hash(user id)
            4. merge this values: (3)/(2)_(1).extension
         */
        final String originalFileName = file.filename();
        return Mono.just(createTempAndTransfer(file,originalFileName))
                .flatMap(temp-> {
                    String resultFileName = extractResultFileName(temp.toFile(),originalFileName,userId);
                    return Mono.just(Pair.of(temp,resultFileName));
                })
                .doOnSuccess(pair->{
                    String resultFileName = pair.getSecond();
                    File f =new File("./static/"+resultFileName);
                    try {
                        FileUtils.copyFile(pair.getFirst().toFile(), f);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                })
                .flatMap(pair -> {
                    String resultFileName = pair.getSecond();
                    return photoServiceFacade.savePhotoAndGet(userId,caption,resultFileName);
                });
    }

    public void deletePhoto(Long id) {
        photoRepository.findById(id)
                .subscribe(photo -> {
                    String fileName = extractFileName(photo.getUrl());
                    try {
                        FileUtils.forceDelete(new File(fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoRepository.deleteById(id).subscribe();
                });
    }
    private String extractFileName(String url){
        return "./static/"+url.substring((domain+"img/").length());
    }
}
