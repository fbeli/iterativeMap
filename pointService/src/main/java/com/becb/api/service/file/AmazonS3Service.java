package com.becb.api.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AmazonS3Service {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AmazonS3 amazonS3;


    /**
     * @param bucket
     * @param directory path do arquivo no s3
     * @param file
     */
    public String saveFile(String bucket, String directory, InputStream inputStream, String fileName) {

        String filePath = getFilePath(fileName, directory);

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

    private String getFilePath(String fileName, String directory) {
        return String.format("%s/%s.%s", directory, fileName, "mp3");
    }


}
