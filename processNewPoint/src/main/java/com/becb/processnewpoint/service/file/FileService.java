package com.becb.processnewpoint.service.file;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.becb.processnewpoint.domain.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
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

    @Value("${becb.storage.s3.users_directory}")
    private String usersDirectory;


    @Autowired
    AmazonS3Service amazonS3Service;

    public List<String> createFileToMap(List<Point> points, String fileName) {

        List<String> filesCreated = new ArrayList<>();

            StringBuilder sb = createInputToFile(points);
            String sufix = points.get(0).getLanguage().getValue().toLowerCase();
            String version = createFile(configFilename(fileName, sufix), sb);

            if (version != null) {
                logger.info("Map file created : {}", configFilename(fileName, sufix));
                filesCreated.add(configFilename(fileName, sufix) + " - " + points.size());
            }

        return filesCreated;
    }

    public boolean createFileToUserMap(List<Point> points, String userInstagram) {
        StringBuilder sb = createInputToFile(points);
        String filename = userInstagram.replace("@", "") + "_map_.geojson";
        String version = createFile(filename, sb);

        if (version != null) {
            logger.info("Map file created : {}", filename);
            return true;
        }
        return false;
    }

    public void copyUserFiles(String userInstagram) {
        amazonS3Service.copyUsersFile(userInstagram);
    }

    public void createConstFile(String instagram, String latitude, String longitude) {
        StringBuilder sb = new StringBuilder();
        instagram = instagram.replace("@", "");
        sb.append("const config = {\n" +
                "    map_file:\"https://www.guidemapper.com/file/" + instagram + "_map_.geojson\",\n" +
                "    latitude:\"" + latitude + "\",\n" +
                "    longitude:\"" + longitude + "\"\n" +
                "}");
        String fileName = usersDirectory + "/" + instagram.replace("@", "") + "/const.js";
        PutObjectResult result = amazonS3Service.saveConstFile(fileName, createTempFile(sb));
        result.getVersionId();
    }

    public String configFilename(String fileName, String sufix) {
        String filePrefix = fileName.substring(0, fileName.lastIndexOf("."));
        String fileSuffix = fileName.substring(filePrefix.length());
        return filePrefix + sufix + "_" + fileSuffix;
    }

    private StringBuilder createInputToFile(List<Point> points) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeadJson());
        logger.info("received {} points to create json file. File will be uploaded", points.size());
        int x = 1;
        for (Point point : points) {
            sb.append(getBodyJson(point));
            if (x < points.size())
                sb.append(",");
            x++;
        }
        sb.append(bottonJson());
        return sb;
    }

    public void createNotApprovedFile(List<Point> points, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeadHtml());
        logger.info("received {} points to be reviewd", points.size());
        for (Point point : points) {
            sb.append(getBodyHtml(point));
        }
        sb.append(bottonHtml());


        String version = createFile(fileName, sb);
        if (version != null)
            logger.info("File to approve created : " + fileName);

    }

    private String createFile(String fileName, StringBuilder sb) {
        PutObjectResult result = amazonS3Service.saveAdminFile(fileName, createTempFile(sb));
        return result.getVersionId();
    }


    private InputStream createTempFile(StringBuilder sb) {
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    private File createLocalFile(String fileName, StringBuilder sb) {

        String filePath = fileDir + File.separator + fileName;
        File file = new File(filePath);

        logger.info("Creating file: " + filePath);
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

    public String getHeadHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"pt-br\"> <html>\n" +

                "<title>GuideMapper</title> " +
                "<meta charset=\"utf-8\"> \n <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "<script src=\"../javascript/point.js\">   </script> \n" +
                "<link href=\"../css/button.css\" rel=\"stylesheet\">" +
                "</head> " +
                "<table>    <tr><th>aprove / block</th><th>Id</th><th>Titulo</th><th>Descrição</th>" +
                "<th>Lat / Long</th><th>audio</th><th" +
                ">Usuário</th></tr>";
    }

    public String getBodyHtml(Point point) {

        String audio = "";
        String audioBlock = "";
        String audioEndpoint = "";
        if (point.getAudio() != null) {
            if (appEndpoint.startsWith("https")) {
                audioEndpoint = appEndpoint.trim() + "/" + point.getAudio();
            } else {
                audioEndpoint = "https://" + appEndpoint.trim() + "/" + point.getAudio();
            }
        }
        if (point.getAudio() != null && !point.getAudio().isBlank()) {
            audio = appEndpoint + "/" + point.getAudio();
            audioBlock = "<audio controls><source src=\"" + audio + "\" type=\"audio/mpeg\" /></audio>";
        }

        return "\n<tr><td>" +
                "<button type=\"button\" class=\"button-53\"  onclick=\"aprovarPoint('" + serviceEndpoint + "aprovar/" + point.getPointId() + "/" + point.getUser().getUserEmail() + "')\" id=\"btn-login-get-token\" >Aprovar</button>" +


                "<button type=\"button\" class=\"button-53\" style=\"background-color:red\" onclick=\"aprovarPoint('" + serviceEndpoint + "bloquear/" + point.getPointId() + "/" + point.getUser().getUserEmail() + "')\" id=\"btn-login-get-token\" >Bloquear</button> \n" +
                "</td><td style=\"font-size:10px\">" + point.getPointId() + "</td><td>" + point.getTitle() + "</td><td style:\"font-weight: bold;\" >" + point.getDescription() + "</td>" +
                "<td><p>" + point.getLatitude() + "</p><p>" + point.getLongitude() + "</p></td>" +
                "<td>" + audioBlock + "</td>" +
                "<td>" + point.getUser().getUserName() + "</td></tr>";

    }

    public String bottonHtml() {
        return "</table></body></html>";
    }

    private String bottonJson() {
        return "  ],\n" +
                "\"type\": \"FeatureCollection\"\n" +
                "}";
    }

    private String getHeadJson() {
        return "{\n" +
                "  \"features\": [\n";
    }

    public String getBodyJson(Point point) {
        String audioEndpoint = "";
        String photoEndpoint = "";
        if (point.getAudio() != null && !point.getAudio().isBlank()) {
           audioEndpoint = "https://" + appEndpoint.replace("https://", "").trim() + "/" + point.getAudio();
        }
        if (point.getPhoto() != null) {
                photoEndpoint = "https://" + appEndpoint.replace("https://", "").trim()  + "/" + point.getPhoto();
        }


        return "\n{\n" +
                "    \"type\": \"Feature\",\n" +
                "    \"properties\": {\n" +
                "      \"title\": \"" + point.getTitle() + "\",\n" +
                "      \"shortDescription\": \"" + point.getShortDescription() + "\",\n" +
                "      \"description\": \"" + point.getDescription() + "\",\n" +
                "      \"pointId\": \"" + point.getPointId() + "\",\n" +
                "      \"user_id\": \"" + point.getUser().getUserId() + "\",\n" +
                "      \"user_name\": \"" + point.getUser().getUserName() + "\",\n" +
                "      \"user_share\": \"" + point.getUser().getShare() + "\",\n" +
                "      \"audio\": \"" + audioEndpoint + "\",\n" +
                "      \"user_instagram\": \"" + point.getUser().getInstagram() + "\",\n" +
                "      \"user_guide\": \"" + point.getUser().getGuide() + "\",\n" +
                "      \"photo\": \"" + photoEndpoint + "\"\n" +

                "    },\n" +
                "    \"geometry\": {\n" +
                "      \"type\": \"Point\",\n" +
                "      \"coordinates\": [" + point.getLongitude().trim() + "," + point.getLatitude().trim() + "]\n" +
                "    }\n" +
                "  }\n";
    }

}
