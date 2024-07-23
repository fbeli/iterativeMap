package com.becb.processnewpoint.service.translate;

import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.SuportService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})
class TranslateServiceInterfaceImplTest {

    @Spy
    @InjectMocks
    private TranslateService translateService;

    @Mock
    private TranslateServiceInterface translateServiceInterfaceMock;

    @Mock
    SuportService suporteService;

    @Test
    void testTranslateJson() throws IOException {
        String raw = retornoFromTranlationService();
        Assert.assertEquals("Ola",translateService.getTranslateFromJason(raw,"pt"));// Test the translate method
    }

    private String retornoFromTranlationService(){
        return "{ \"status\": 200, \"from\": \"en\",\"to\": \"pt\",\"original_text\": \"Hello, world!!\",\"translated_text\": {\"pt\": \"Ola\"},\"translated_characters\": 14}";
    }

    @Test
    void testTranslatePoint() throws IOException {
        Point oldPoint = getPoint();
        Point newPoint = getPoint();
        doReturn("Bláblá").when(translateService).translateText(anyString(), any());

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(suporteService.getConnection(any())).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpURLConnection.getInputStream()).thenReturn(new ByteArrayInputStream(retornoFromTranlationService().getBytes()));

        Point createdPoint  = translateService.translate(oldPoint, newPoint,"PT");
        Assert.assertEquals(oldPoint.getPointId(),createdPoint.getPointParent().getPointId());
    }

    @Test
    void testImpossibleToTranslateError() throws IOException {
        Point oldPoint = getPoint();
        Point newPoint = getPoint();
        doReturn("Bláblá").when(translateService).translateText(anyString(), any());

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(suporteService.getConnection(any())).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpURLConnection.getInputStream()).thenReturn(new ByteArrayInputStream(retornoFromTranlationService().getBytes()));

        Point createdPoint  = translateService.translate(oldPoint, newPoint,"PT");
        Assert.assertEquals(oldPoint.getPointId(),createdPoint.getPointParent().getPointId());
    }

    private Point getPoint(){

        Point oldPoint = new Point();
        int leftLimit = 97; // ASCII value for 'a'
        int rightLimit = 122; // ASCII value for 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        oldPoint.setPointId(generatedString);
        oldPoint.setLanguage(LanguageEnum.EN);
        oldPoint.setUser(getUser());
        oldPoint.setDescription("Hello, world!!");
        oldPoint.setTitle("Hello, world!!");
        return oldPoint;
    }
    private User getUser(){
        User user = new User();
        user.setUserId("XXXXXX");
        return user;
    }

    @Test
    void testChildInSameLanguage(){
        Point fatherPoint = getPoint();
        Point childEN = getPoint();
        Point childPT = getPoint();

        Point childSP = getPoint();
        Point childFR = getPoint();

        fatherPoint.setLanguage(LanguageEnum.EN);

        childPT.setLanguage(LanguageEnum.PT);
        childSP.setLanguage(LanguageEnum.ES);


        childEN.setLanguage(LanguageEnum.EN);
        Assert.assertTrue(translateService.canChildForThatLanguage(fatherPoint, "PT"));
        fatherPoint.addChildPoint(childSP);
        fatherPoint.addChildPoint(childPT);
        fatherPoint.addChildPoint(childFR);

        Assert.assertFalse(translateService.canChildForThatLanguage(fatherPoint, LanguageEnum.PT.toString()));
        Assert.assertFalse(translateService.canChildForThatLanguage(fatherPoint, LanguageEnum.EN.toString()));

    }
}