package com.becb.processnewpoint.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.becb.processnewpoint.storage.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AmazonS3Service {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${becb.storage.s3.bucket}")
    private String bucket;

    @Value("${becb.storage.s3.directory-files}")
    private String directoryFile;

    @Autowired
    private StorageProperties storageProperties;

    public void saveAdminFile(String fileName, InputStream file)  {
        saveFile(bucket, directoryFile, file, fileName);
    }
    /**
     *
     * @param bucket
     * @param directory path do arquivo no s3
     * @param file
     */
    public void saveFile(String bucket, String directory, InputStream inputStream, String fileName)  {

       try {
           String filePath = getFilePath(fileName, directory);

           var objectMetadata = new ObjectMetadata();

           amazonS3.putObject(bucket, filePath, inputStream, objectMetadata);
       } catch (Exception e) {
           logger.error("não foi possível salvar arquivo no S3: " + e.getMessage());
       }
    }

    private String getFilePath(String fileName, String directory) {
        return String.format("%s/%s",directory, fileName);
    }

}
