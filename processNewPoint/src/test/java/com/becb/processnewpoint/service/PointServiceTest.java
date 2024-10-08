package com.becb.processnewpoint.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.repository.PointRepository;
import com.becb.processnewpoint.service.audio.AudioService;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.file.FileService;
import com.becb.processnewpoint.service.translate.TranslateService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.becb.processnewpoint.domain.Point;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = { "key=value" })

class PointServiceTest {

    @InjectMocks
    PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    DynamoDbClient dynamodbClient;

    @Mock
    MapService mapService;

    @Mock
    FileService fileService;

    @Mock
    AudioService audioService;

    @Mock
    TranslateService translateService;

    @Test
    void gerarArquivoParaMapa() {
        ArrayList<Point> lista = new ArrayList();
        for(int i =0; i < 3; i++) {
            Point point = new Point();
            point.setTitle("Title "+i);
            point.setLongitude("-9"+i+".15007687024712");
            point.setLatitude("3"+i+".72524959265044");
            lista.add(point);
        }
        when(pointRepository.findAllByAproved(any())).thenReturn(lista);
        when(mapService.setPlace(any())).thenCallRealMethod();
        when(fileService.createFileToMap(any(), any())).thenReturn(List.of("file1","file2","file3"));

        pointService.gerarArquivoParaMapa("{" +
                "  \"user_id\": \"01HNG094DXF7A4HQPD8QKWHCBW\"," +
                "  \"user_name\": \"Fred\"," +
                "  \"user_email\": \"fred.belisario@gmail.com\","+
                "  \"file_name\": \"arquivo_"+ LocalDateTime.now() +".html\"} ");
        assertEquals(3, lista.size());
    }

    @Test
    void updatePointObject() throws IllegalAccessException {
        String title = "new Title Here!";
        Point pointOld = new Point();
        pointOld.setPointId("01HRVRVS9X5NC1T2JAPT9EVYEC");
        pointOld.setPhotos(List.of("photo1","photo2"));

        Point pointNew  = new Point();
        pointNew.setPointId("01HRVRVS9X5NC1T2JAPT9EVYEC");
        pointNew.setTitle("new Title Here!");

        pointService.updatePointObject(pointNew, pointOld );
        Assert.assertEquals(title, pointOld.getTitle());

    }

    @Test
    void testSavePoint() {
    }

    @Test
    void aprovePoint() {
    }

    @Test
    void testGerarArquivoParaMapa() {
    }

    @Test
    void testGerarArquivoParaAprovacao() {
    }

    @Test
    void convertItemsToPoints() {
    }

    @Test
    void convertItemToPoint() {
    }

    private String getMessageFromStack(){

    return "{"+
        "\"description\": \"Marquês de Pombal era muito bom\","+
        "\"latitude\": \"Latitude: 38.72524959265044\","+
        "\"lngLat\": null,"+
        "\"longitude\": \"Longitude: -9.15007687024712\","+
        "\"pointId\": null,"+
        "\"s3Voice\": null,"+
        "\"title\": \"Marqu\u00E9s de Pombal\","+
        "\"user_email\": \"frederico@gmail.com\","+
        "\"user_id\": \"335254e4-73e6-4c9d-a271-904cb3bf320a\","+
        "\"user_name\": \"Frederico\""+
        "}" ;
    }

    private String getMessageFromStackWithAudio(){

        return "{"+
                "\"description\": \"Marquês de Pombal era muito bom\","+
                "\"latitude\": \"Latitude: 38.72524959265044\","+
                "\"lngLat\": null,"+
                "\"longitude\": \"Longitude: -9.15007687024712\","+
                "\"pointId\": XXXXXX,"+
                "\"s3Voice\": null,"+
                "\"title\": \"Marqu\u00E9s de Pombal\","+
                "\"user_email\": \"frederico@gmail.com\","+
                "\"user_id\": \"335254e4-73e6-4c9d-a271-904cb3bf320a\","+
                "\"user_name\": \"Frederico\","+
                "\"audio\": \"AUDIOHER\""+
                "}" ;
    }
    private String getMessageFromStackAudioEmpty(){

        return "{"+
                "\"description\": \"Marquês de Pombal era muito bom\","+
                "\"latitude\": \"Latitude: 38.72524959265044\","+
                "\"lngLat\": null,"+
                "\"longitude\": \"Longitude: -9.15007687024712\","+
                "\"pointId\": null,"+
                "\"s3Voice\": null,"+
                "\"title\": \"Marqus de Pombal\","+
                "\"user_email\": \"frederico@gmail.com\","+
                "\"user_id\": \"335254e4-73e6-4c9d-a271-904cb3bf320a\","+
                "\"user_name\": \"Frederico\","+
                "\"audio\": \"\""+
                "}" ;
    }

    private String getMessageFromStackAudioNull(){

        return "{"+
                "\"description\": \"Marquês de Pombal era muito bom\","+
                "\"latitude\": \"Latitude: 38.72524959265044\","+
                "\"lngLat\": null,"+
                "\"longitude\": \"Longitude: -9.15007687024712\","+
                "\"pointId\": null,"+
                "\"s3Voice\": null,"+
                "\"title\": \"Marqus de Pombal\","+
                "\"user_email\": \"frederico@gmail.com\","+
                "\"user_id\": \"335254e4-73e6-4c9d-a271-904cb3bf320a\","+
                "\"user_name\": \"Frederico\","+
                "\"audio\": null"+
                "}" ;
    }
    @Test
    void testConvertItemToPoint() {
        Item item = new Item()
                .withString("user_email","lisboasecreta@mail.com")
                .withString("user_name","Lisboa Secreta")
                .withString("language","PT")
                .withString("point_latitude","38.74380760290884")
                .withString("instagram","@lisboasecreta")
                .withString("point_longitude","-9.10122436858540")
                .withString("point_short_description","É restaurante, é livraria, é sala")
                .withString("point_description","É restaurante, é livraria, é sala de concertos")
                .withString("pointId","01HRVRVS9X5NC1T2JAPT9EVYEC")
                .withString("point_title","Fábrica Braço de Prata")
                .withString("user_id","3871ca56-4c29-4813-88f1-ff6e012a698b")
                .withString("usuario_aprovador","lisboasecreta@mail")
                .withBoolean("aprovado",true);

        Point point = pointService.convertItemToPoint(item);
        Assert.assertEquals("01HRVRVS9X5NC1T2JAPT9EVYEC", point.getPointId());


    }

    @Test
    void testConvertItemToPointWithEmptyField() {
        Item item = new Item()
                .withString("user_email","lisboasecreta@mail.com")
                .withString("user_name","Lisboa Secreta")
                .withString("language","PT")
                .withString("point_latitude","38.74380760290884")
                .withString("point_longitude","-9.10122436858540")
                .withString("point_short_description","É restaurante, é livraria, é sala")
                .withString("point_description","É restaurante, é livraria, é sala de concertos")
                .withString("pointId","01HRVRVS9X5NC1T2JAPT9EVYEC")
                .withString("point_title","Fábrica Braço de Prata")
                .withString("user_id","3871ca56-4c29-4813-88f1-ff6e012a698b")
                .withString("usuario_aprovador","lisboasecreta@mail")
                .withBoolean("aprovado",true);

        Point point = pointService.convertItemToPoint(item);
        Assert.assertEquals("01HRVRVS9X5NC1T2JAPT9EVYEC", point.getPointId());
    }

    @Test
    void addPhotos() {
        Point point = new Point();
        String photos = "{photo 1,photo2,photo3,photo4}";
        List<String> listaDePhotos = pointService.addPhotos(photos);
        Assert.assertEquals(4, listaDePhotos.size());

        point.setPhotos(listaDePhotos);
        Assert.assertTrue(photos.equals(point.getPhotosAsString()));


    }

    @Test
    void createPointsFromParent() {
        pointService.gerarArquivoParaMapa("{" +
                "  \"pointId\": \"01HNG094DXF7A4HQPD8QKWHCBW\" } ");
    }

    @Test
    void createAudioToParent() throws Exception {
        String pointId = "AAAAAA";
        Point point = new Point(pointId);
        point.setDescription("blá");
        when(audioService.saveAudio(any(), any(), any())).thenReturn("link");
        when(audioService.needCreateAudio(any())).thenReturn(true);
        Point pointRetorno = pointService.createAudioToParent(point);
        Assert.assertTrue(pointRetorno.getAudio().equals("link"));

    }
    @Test
    void notCreateAudioToParent() throws Exception {
        String pointId = "AAAAAA";
        Point point = new Point(pointId);
        point.setAudio("bla");

        when(pointRepository.findPointByPointId(pointId)).thenReturn(Optional.of(point));
        when(audioService.saveAudio(any(), any(), any())).thenReturn("link");
        Point pointRetorno = pointService.createAudioToParent(pointId);
        Assert.assertTrue(pointRetorno.getAudio().equals("bla"));

    }
    @Test
    void notCreateAudioToEmptyDescription() throws Exception {
        String pointId = "AAAAAA";
        Point point = new Point(pointId);
        point.setDescription("");
        point.setLanguage(LanguageEnum.PT);

        when(translateService.canChildForThatLanguage(any(), any())).thenReturn(true);
        when(translateService.canChildForThatLanguage(point, LanguageEnum.PT.getValue())).thenReturn(false);
        when(translateService.translate(any(), any(), any())).thenCallRealMethod();
        when(translateService.translateText(any(), any())).thenReturn("translated");

        when(pointRepository.findPointByPointId(pointId)).thenReturn(Optional.of(point));
        when(audioService.saveAudio(any(), any(), any())).thenCallRealMethod();
        List<Point> points = pointService.createPointsFromParent(point);
        Assert.assertTrue(points.get(0).getAudio()==null);

    }
}