package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.domain.RouterPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = { "key=value" })
class RoteiroServiceTest {

    @Autowired
    private RoteiroService roteiroService;


    @Test
    void organizeRoutesAddRoute() {

        List<RouterPoint > routerPoints =  Arrays.asList(new RouterPoint(1, new Point()));
        routerPoints.get(0).getPoint().setPointId("2222");
        Roteiro route = getRoteiro();
        roteiroService.organizeRoutes(route, routerPoints);
        assertEquals(3, route.getPoints().size());

    }

    @Test
    void organizeRoutesAdd2Routes() {

        List<RouterPoint > routerPoints =  Arrays.asList(new RouterPoint(1, new Point()), new RouterPoint(2, new Point()));
        routerPoints.get(0).getPoint().setPointId("2222");
        routerPoints.get(1).getPoint().setPointId("3333");
        Roteiro route = getRoteiro();
        roteiroService.organizeRoutes(route, routerPoints);
        assertEquals(4, route.getPoints().size());

    }
    @Test
    void organizeRoutesFilterRoutes() {

        List<RouterPoint > routerPoints =  Arrays.asList(new RouterPoint(1, new Point()));
        routerPoints.get(0).getPoint().setPointId("1111");
        Roteiro route = getRoteiro();
        roteiroService.organizeRoutes(route, routerPoints);
        assertEquals(2, route.getPoints().size());

    }

    @Test
    void organizeRoutesFilterAndAddRoutes() {

        List<RouterPoint > routerPoints =  Arrays.asList(new RouterPoint(1, new Point()), new RouterPoint(2, new Point()));
        routerPoints.get(0).getPoint().setPointId("2222");
        routerPoints.get(1).getPoint().setPointId("1111");
        Roteiro route = getRoteiro();
        roteiroService.organizeRoutes(route, routerPoints);
        assertEquals(3, route.getPoints().size());

    }

    private Roteiro getRoteiro(){
        Roteiro route = new Roteiro("1234");
        route.setPoints(new ArrayList<RouterPoint>(Arrays.asList(new RouterPoint(1, new Point()), new RouterPoint(2, new Point()))));

        route.getPoints().get(0).getPoint().setPointId("0000");
        route.getPoints().get(1).getPoint().setPointId("1111");

        return route;
    }
}