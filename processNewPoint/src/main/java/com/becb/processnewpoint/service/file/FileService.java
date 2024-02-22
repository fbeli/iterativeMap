package com.becb.processnewpoint.service.file;

import com.becb.processnewpoint.domain.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class FileService {



    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${file.directory}")
    String fileDir;
    @Value("${app.endpoint}")
    String appEndpoint;
    @Value("${service.endpoint}")
    String serviceEndpoint;

    @Autowired
    AmazonS3Service amazonS3Service;

    public void createFileToMap(List<Point> points, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeadJson());
        logger.info("received {} points to create json file. File will be uploaded", points.size());
        int x = 1;

        for (Point point: points) {
            sb.append(getBodyJson(point));
            if( x < points.size() )
                sb.append(",");
            x++;
        }

        sb.append(bottonJson());

        File file = createFile(fileName, sb);
        if(file != null)
            logger.info("Map file created : "+file.getAbsolutePath());
    }
    public void createNotApprovedFile(List<Point> points, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeadHtml());
        logger.info("received {} points to be reviewd", points.size());
        for (Point point: points) {
            sb.append(getBodyHtml(point));
        }
        sb.append(bottonHtml());



        File file = createFile(fileName, sb);
        if(file != null)
            logger.info("File to approve created : "+file.getAbsolutePath());

    }

    private File createFile(String fileName, StringBuilder sb){


        File file = null;
        String env = System.getenv("ENVIRONMENT");
        /*if ( env.equals("docker") || env.equals("dev") ) {
           file = createLocalFile(fileName, sb);
           return file;
        }else {*/

            amazonS3Service.saveAdminFile( fileName, createTempFile(fileName, sb));
        //}
        return file;
    }


    private InputStream createTempFile(String fileName, StringBuilder sb)
    {

            InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
            return inputStream;

    }

        private File createLocalFile(String fileName, StringBuilder sb){

        String filePath = fileDir + File.separator + fileName;
        File file = new File(filePath);

        logger.info("Creating file: "+filePath);
        try {
            if (file.createNewFile()) {
                logger.info("File created successfully.");

                // Write content to the file
                FileWriter writer = new FileWriter(file);
                writer.write(sb.toString());
                writer.close();

                logger.info("Content written to the file.");
            } else {
                logger.info("File already exists.");
            }
        } catch (IOException e) {
            logger.info("An error occurred while creating the file: {}", e.getMessage());
        }

        return file;
    }

    public String getHeadHtml(){
        return "<!DOCTYPE html>\n" +
                "<html lang=\"pt-br\"> <html>\n" +

                "    <title>GuideMapper</title> " +
                "<meta charset=\"utf-8\"> \n <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "<script src=\"../javascript/point.js\">   </script> \n" +
                "</head> " +
                "<table>    <tr><th>aprove</th><th>block</th><th>Id</th><th>Titulo</th><th>Descrição</th><th>Latitude</th><th" +
                ">Longitude</th><th>audio</th><th" +
                ">Usuário</th><th>Email</th></tr>";
    }
    public String getBodyHtml(Point point){
        return "\n<tr><td><a href='#' onclick=\"aprovarPoint('"+ serviceEndpoint +"aprovar/"+point.getPointId()+"/"+point.getUser().getUserEmail()+"')" +
                "\">aprove" +
                "</a></td><td><a href='#' onclick=\"aprovarPoint('"+ serviceEndpoint +"bloquear/"+point.getPointId()+
                "/"+point.getUser().getUserEmail()+"')" +
                "\">block</a></td><td>"+point.getPointId()+"</td><td>"+point.getTitle()+"</td><td>"+point.getDescription()+"</td><td>"+point.getLatitude()+"</td><td>"+point.getLongitude()+"</td><td><audio controls><source src=\""+appEndpoint+"/"+point.getAudio()+"\"type=\"audio/mpeg\"></audio></td><td>"+point.getUser().getUserName()+"</td><td>"+point.getUser().getUserEmail()+"</td></tr>";

    }
    public String bottonHtml(){
        return "</table></body></html>";
    }
    private String bottonJson(){
        return  "  ],\n" +
                "\"type\": \"FeatureCollection\"\n" +
                "}";
    }

    private String getHeadJson(){
        return "{\n" +
                "  \"features\": [\n";
    }
    private String getBodyJson(Point point){
        return "\n{\n" +
                "    \"type\": \"Feature\",\n" +
                "    \"properties\": {\n" +
                "      \"title\": \""+point.getTitle()+"\",\n" +
                "      \"shortDescription\": \""+point.getShortDescription()+"\",\n" +
                "      \"description\": \""+point.getDescription()+"\",\n" +
                "      \"pointId\": \""+point.getPointId()+"\"\n" +
                "      \"audio\": \""+appEndpoint+point.getAudio()+"\"\n" +
                "    },\n" +
                "    \"geometry\": {\n" +
                "      \"type\": \"Point\",\n" +
                "      \"coordinates\": ["+point.getLongitude().trim()+","+point.getLatitude().trim()+"]\n" +
                "    }\n" +
                "  }\n";
    }

}
