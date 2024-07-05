package com.becb.processnewpoint.service;

import com.becb.processnewpoint.RoteiroTests;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.dto.RoteiroDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})
class RoteiroServiceDataTest extends RoteiroTests {


    @Autowired
    RoteiroService roteiroService;
    @Autowired
    UserService userService;
    @Mock
    PointService pointService;

    @Test
    @Transactional
    void getRoteirosByUser() {

        setServices(userService, roteiroService);
        saveRoteiro("Lisbon", "userId_1");
        saveRoteiro("Lisbon", "userId_2");
        saveRoteiro("Porto", "userId_1");


        Pageable pageable = PageRequest.of(0, 10);
        Page<Roteiro> roteirosPage = roteiroService.getRoteirosByUser(pageable, usuarioList.get(0));
        List<Roteiro> roteiros = roteirosPage.getContent();

        Assertions.assertEquals(2, roteiros.size());
        Assertions.assertEquals(3, roteiros.get(0).getPoints().size());
        Assertions.assertTrue(roteiros.get(0).getPoints().stream().sorted((o1, o2) -> o1.getPositionInRoute()).collect(Collectors.toList())
                .get(0).getPoint().getTitle().contains("ponto0"));

    }

    private void setRoteiros(){
        setServices(userService, roteiroService);
        saveRoteiro("Lisbon", "userId_1");
        saveRoteiro("Lisbon", "userId_2");
        saveRoteiro("Porto", "userId_1");
        saveRoteiro("Braga", "userId_1");
        saveRoteiro("Rio", "userId_1");
    }

    @Test
    @Transactional
    void getRoteirosBySearch() {

        setRoteiros();

        Pageable pageable = PageRequest.of(0, 10);

        List<RoteiroDto> roteirosByTitle = roteiroService.getRoteiros(pageable, "", "Roteiro", null);
        assertEquals(1, roteirosByTitle.size());


    }


    @Test
    @Transactional
    void getRoteiros() {
        setRoteiros();

        Pageable pageable = PageRequest.of(0, 10);

        List<RoteiroDto> roteiros =roteiroService.getRoteiros(pageable, "Lisbon", null, null);

        Assertions.assertEquals(2, roteiros.size());
        Assertions.assertEquals(3, roteiros.get(0).getPoints().size());
        Assertions.assertTrue(roteiros.get(0).getPoints().stream().sorted((o1, o2) -> o1.getPositionInRoute()).collect(Collectors.toList())
                .get(0).getTitle().contains("ponto0"));


        roteiros = roteiroService.getRoteiros(PageRequest.of(0, 10), "PORTO", null, null);
        Assertions.assertEquals(1, roteiros.size());
        Assertions.assertEquals(3, roteiros.get(0).getPoints().size());
        Assertions.assertTrue(roteiros.get(0).getPoints().stream().sorted((o1, o2) -> o1.getPositionInRoute()).collect(Collectors.toList())
                .get(0).getTitle().contains("ponto0"));

        //if pageable is null
        roteiros = roteiroService.getRoteiros(null, "PORTO", null, null);
        Assertions.assertEquals(1, roteiros.size());

        roteiros =roteiroService.getRoteiros(pageable, "Lisbon", "roteiro", null);
        Assertions.assertEquals(2, roteiros.size());

        roteiros =roteiroService.getRoteiros(pageable, null, "roteiro", null);
        Assertions.assertEquals(5, roteiros.size());

        roteiros =roteiroService.getRoteiros(pageable, null, "braga", "userId_1");
        Assertions.assertEquals(1, roteiros.size());

        roteiros =roteiroService.getRoteiros(pageable, null, null, "userId_1");
        Assertions.assertEquals(4, roteiros.size());

        roteiros =roteiroService.getRoteiros(pageable, "Iguaba", null, "userId_1");
        Assertions.assertEquals(0, roteiros.size());

        roteiros =roteiroService.getRoteiros(pageable, "Lisbon", "roteiro", "userId_1");
        Assertions.assertEquals(1, roteiros.size());

    }

    @Test
    @Transactional
    void getRoteiroById() {
        setServices(userService, roteiroService);
        saveRoteiro("Lisbon", "userId_1");
        saveRoteiro("Lisbon", "userId_2");
        saveRoteiro("Porto", "userId_1");

        Pageable pageable = PageRequest.of(0, 10);
        Roteiro roteiro = roteiroService.getRoteiroById(roteiroList.get(0).getId());

        Assertions.assertEquals(3, roteiro.getPoints().size());
        roteiro = roteiroService.getRoteiroById("userId_12");
        Assertions.assertNull(roteiro);

    }

    @Test
    @Transactional
    void addRoteiro() throws Exception {
        createPoints();
        roteiroService.setPointService(pointService);
        when(pointService.getPointById("01HSXA2ZXT1YA6DFT2XK7DK9N1")).thenReturn(pointList.get(0));
        when(pointService.getPointById("01HSX9ZZF0G0AGKVCEZ25F1D88")).thenReturn(pointList.get(1));
        when(pointService.getPointById("01HSX9Q11T50WAPZ50ZPY2HQ36")).thenReturn(pointList.get(2));
        when(pointService.getPointById("01HSY0X5X91GP1DH2F00BX576B")).thenReturn(pointList.get(3));

        setServices(userService, roteiroService);
        createUser("userId_1");
        Roteiro roteiro = roteiroService.addNewRoute(jsonRoteiro());

        Assertions.assertEquals(4, roteiro.getPoints().size());
        Assertions.assertNotNull(roteiro.getId());

    }

    List<Point> pointList = new ArrayList<>();
    private void createPoints(){
        pointList.add(new Point("01HSXA2ZXT1YA6DFT2XK7DK9N1"));
        pointList.add(new Point("01HSX9ZZF0G0AGKVCEZ25F1D88"));
        pointList.add(new Point("01HSX9Q11T50WAPZ50ZPY2HQ36"));
        pointList.add(new Point("01HSY0X5X91GP1DH2F00BX576B"));
        pointList.get(0).setCity("Lisbon");
    }
    private String jsonRoteiro() {
        String mensagem =
                "{" +
                    "'city': 'Lisbon'," +
                    "'roteiroId': '01HSX9Q2HQ36'," +
                    "'userId': 'userId_1'," +
                        "'routePoints': [" +
                            "{" +
                                "'pointId': '01HSXA2ZXT1YA6DFT2XK7DK9N1'," +
                                "'positionInRoute': '1'" +
                            "}," +
                        "{" +
                        "'pointId': '01HSX9ZZF0G0AGKVCEZ25F1D88'," +
                        "'positionInRoute': '4'" +
                        "}," +
                        "{" +
                        "'pointId': '01HSX9Q11T50WAPZ50ZPY2HQ36'," +
                        "'positionInRoute': '2'" +
                        "}," +
                        "{" +
                        "'pointId': '01HSY0X5X91GP1DH2F00BX576B'," +
                        "'positionInRoute': '3'" +
                        "}" +

                        "]" +
                "}";

        return mensagem;
    }

}