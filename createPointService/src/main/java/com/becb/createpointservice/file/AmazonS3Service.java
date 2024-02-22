package com.becb.createpointservice.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.ByteBuffer;

@Service
public class AmazonS3Service {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${becb.storage.s3.bucket}")
    private String bucket;

    @Value("${becb.storage.s3.directory-files}")
    private String directoryFile;

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

           ByteBuffer bb = ByteBuffer.allocate(1024 * 1024 );
           var objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("text/html");
          //      objectMetadata.set ContentLength(bb.limit());


           amazonS3.putObject(bucket, filePath, inputStream, objectMetadata);
       } catch (Exception e) {
           logger.error("não foi possível salvar arquivo no S3: " + e.getMessage());
       }
    }

    private String getFilePath(String fileName, String directory) {
        return String.format("%s/%s",directory, fileName);
    }

}
