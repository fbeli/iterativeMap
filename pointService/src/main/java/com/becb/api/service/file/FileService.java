package com.becb.api.service.file;

import com.amazonaws.util.Base64;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {

    private final Logger logger = LoggerFactory.getLogger(getClass());



    @Value("${becb.storage.s3.bucket}")
    private String bucket;

    @Value("${becb.storage.s3.directory}")
    private String directoryFile;

    @Value("${becb.storage.s3.directory.photo}")
    private String directoryPhotoFile;

    @Autowired
    AmazonS3Service amazonS3Service;

    public String saveAudio(String b64String, String fileName) throws JSONException, IOException {

        byte[] decodedBytes = Base64.decode(b64String);
        InputStream inputStream= new ByteArrayInputStream(decodedBytes);

        return saveFileMp3(inputStream, fileName);

    }
    public String saveFileMp3(InputStream inputStream, String fileName)  {

        return amazonS3Service.saveFile(bucket, directoryFile,  inputStream,  fileName);
    }

    public String saveFileJpg( InputStream inputStream, String fileName)  {

        return amazonS3Service.saveFile(  inputStream, directoryPhotoFile, fileName, "image/jpeg" );
    }

    public String savePointPhoto(InputStream inputStream, String pointId) throws IOException {
            return saveFileJpg( inputStream, pointId+"_");
    }

}
