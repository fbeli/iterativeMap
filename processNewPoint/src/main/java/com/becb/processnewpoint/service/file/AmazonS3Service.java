package com.becb.processnewpoint.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class AmazonS3Service {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private CloudFrontService cloudFrontService;

    @Value("${becb.storage.s3.bucket}")
    private String bucket;

    @Value("${becb.storage.s3.directory-files}")
    private String directoryFile;

    public void saveAdminFile(String fileName, InputStream file) {
        saveFile(bucket, directoryFile, file, fileName);
    }

    /**
     * @param bucket
     * @param directory path do arquivo no s3
     * @param inputStream
     * @param fileName
     */
    public void saveFile(String bucket, String directory, InputStream inputStream, String fileName) {

        logger.info("Sending file {} to S3 in {}: ", fileName, amazonS3.getRegion());

        try {
            String filePath = getFilePath(fileName, directory);

            var objectMetadata = new ObjectMetadata();
            if (fileName.contains("map")){
                objectMetadata.setContentType("application/json");
                cloudFrontService.invalidateCache();
            } else {
                objectMetadata.setContentType("text/html");
            }
            objectMetadata.setContentEncoding("UTF-8");

            amazonS3.putObject(bucket, filePath, inputStream, objectMetadata);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            logger.error("não foi possível salvar arquivo no S3: " + e.getMessage()+ "\n" + sw.toString());
        }
    }

    private String getFilePath(String fileName, String directory) {
        return String.format("%s/%s", directory, fileName);
    }

}
