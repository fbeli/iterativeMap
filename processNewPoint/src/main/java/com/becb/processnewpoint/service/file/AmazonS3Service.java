package com.becb.processnewpoint.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
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

    @Value("${becb.storage.s3.users_directory_template}")
    private String usersTemplate;

    @Value("${becb.storage.s3.users_directory}")
    private String usersDirectory;

    public PutObjectResult saveAdminFile(String fileName, InputStream file) {
        return saveFile(bucket, directoryFile, file, fileName);
    }
    public PutObjectResult saveConstFile(String fileName, InputStream file) {
        return saveFile(bucket, "", file, fileName);
    }




    /**
     * @param bucket
     * @param directory path do arquivo no s3
     * @param inputStream
     * @param fileName
     */
    public PutObjectResult saveFile(String bucket, String directory, InputStream inputStream, String fileName) {

        try {
            String filePath = getFilePath(fileName, directory);

            var objectMetadata = new ObjectMetadata();
            if (fileName.contains("map")){
                objectMetadata.setContentType("application/json");
                cloudFrontService.invalidateCache();
                objectMetadata.setContentEncoding("UTF-8");
            } else
                if(fileName.contains("mp3")){
                    objectMetadata.setContentType("audio/mpeg3");
                }
            else {
                objectMetadata.setContentType("text/html");
                objectMetadata.setContentEncoding("UTF-8");
            }

            logger.info("Sending file {} to bucker {} in S3 region:{} ", filePath, bucket, amazonS3.getRegion());
            return amazonS3.putObject(bucket, filePath, inputStream, objectMetadata);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            logger.error("não foi possível salvar arquivo no S3: " + e.getMessage()+ "\n" + sw.toString());
        }
        return null;
    }



    private String getFilePath(String fileName, String directory) {
        if(directory.length()>1)
            return String.format("%s/%s", directory, fileName);
        else return fileName;
    }

    public void copyUsersFile(String directory){
        CopyObjectResult result = amazonS3.copyObject(bucket, usersTemplate,
                bucket, usersDirectory+"/"+directory.replace("@","")+"/index.html");
    }
}
