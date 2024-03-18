package com.becb.api.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class AmazonS3Service {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${becb.storage.s3.bucket}")
    private String bucket;


    @Autowired
    private AmazonS3 amazonS3;

    public  String saveFile(InputStream inputStream,  String directory, String fileName, String contentType) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);

        return saveFile(objectMetadata, inputStream, getFilePath(fileName,directory,contentType));

    }

    private String saveFile( ObjectMetadata objectMetadata, InputStream inputStream, String filePath) {


        logger.info("Sending file {} to S3 in {}: ", filePath, amazonS3.getRegion());

        try {

            amazonS3.putObject(bucket, filePath, inputStream, objectMetadata);
            return filePath;
        } catch (Exception e) {
            logger.error("não foi possível salvar arquivo no S3: {}", e.getMessage());
        }

        return null;
    }



    public String saveFile(String bucket, String directory, InputStream inputStream, String fileName) {

        String filePath = getFilePath(fileName, directory, null);

        logger.info("Sending file {} to S3 in {}: ", filePath, amazonS3.getRegion());

        try {

            var objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("audio/mpeg3");


            amazonS3.putObject(bucket, filePath, inputStream, objectMetadata);
            return filePath;
        } catch (Exception e) {
            logger.error("não foi possível salvar arquivo no S3: {}", e.getMessage());
        }

        return null;
    }


    private String getFilePath(String fileName, String directory, String contentType) {
        String extension;
        if(contentType != null && contentType.equals("image/jpeg")){
            extension = "jpg";
        }else
            extension = "mp3";
        return String.format("%s/%s.%s", directory, fileName, extension);
    }


}
