package com.becb.processnewpoint.service.file;

import com.becb.processnewpoint.domain.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class FileService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${file.directory}")
    String fileDir;

    @Value("${app.endpoint}")
    String appEndpoint;

    @Value("${service.endpoint}")
    String serviceEndpoint;

    public void createFileToMap(ArrayList<Point> points, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeadJson());
        for (Point point: points) {
            sb.append(getBodyJson(point));
        }
        sb.append(bottonJson());

        File file = createFile(fileName, sb);

        logger.info("Map file created : "+file.getAbsolutePath());

    }
    public void createNotApprovedFile(ArrayList<Point> points, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeadHtml());
        for (Point point: points) {
            sb.append(getBodyHtml(point));
        }
        sb.append(bottonHtml());

        File file = createFile(fileName, sb);
        logger.info("File to import in map created : "+file.getAbsolutePath());

    }

    private File createFile(String fileName, StringBuilder sb){

        String filePath = fileDir + File.separator + fileName;
        File file = new File(filePath);

        try {
            if (file.createNewFile()) {
                System.out.println("File created successfully.");

                // Write content to the file
                FileWriter writer = new FileWriter(file);
                writer.write(sb.toString());
                writer.close();

                System.out.println("Content written to the file.");
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
        }

        return file;
    }

    private String getDateTime(){
        return  new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss").format(new Date());
    }
    public String getHeadHtml(){
        return "<head>  <script src=\""+ appEndpoint +"javascript/point.js\">   </script> </head> " +
                "<style>tr{border:1}</style><body><div style='width=100%'><table>"+
                "<tr><th>aprove</th><th>block</th><th>Id</th><th>Titulo</th><th>Descrição</th><th>Latitude</th><th" +
                ">Longitude</th><th>audio</th><th" +
                ">Usuário</th><th>Email</th></tr>";
    }
    public String getBodyHtml(Point point){
        return "<tr><td><a href='#' onclick=\"aprovarPoint('"+ serviceEndpoint +"aprovar/"+point.getPointId()+"/"+point.getUser().getUserEmail()+"')" +
                "\">aprove" +
                "</a></td><td><a href='#' onclick=\"aprovarPoint('"+ serviceEndpoint +"bloquear/"+point.getPointId()+
                "/"+point.getUser().getUserEmail()+"')" +
                "\">block</a></td><td>"+point.getPointId()+"</td><td>"+point.getTitle()+"</td><td>"+point.getDescription()+"</td><td>"+point.getLatitude()+"</td><td>"+point.getLongitude()+"</td><td>"+point.getAudio()+"</td><td>"+point.getUser().getUserName()+"</td><td>"+point.getUser().getUserEmail()+"</td></tr>";

    }
    public String bottonHtml(){
        return "</table></div></body>";
    }
    private String bottonJson(){
        return  "  ]\n" +
                "\"type\": \"FeatureCollection\"\n" +
                "}";
    }

    private String getHeadJson(){
        return "{\n" +
                "  \"features\": [\n";
    }
    private String getBodyJson(Point point){
        return "{\n" +
                "    \"type\": \"Feature\",\n" +
                "    \"properties\": {\n" +
                "      \"title\": \""+point.getTitle()+"\",\n" +
                "      \"shortDescription\": \""+point.getShortDescription()+"\",\n" +
                "       \"description\": \""+point.getDescription()+"\",\n" +
                "    },\n" +
                "    \"geometry\": {\n" +
                "      \"type\": \"Point\",\n" +
                "      \"coordinates\": ["+point.getLatitude().trim()+","+point.getLongitude().trim()+"]\n" +
                "    }\n" +
                "  },\n";
    }



}
