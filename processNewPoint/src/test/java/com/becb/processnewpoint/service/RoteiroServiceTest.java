package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.domain.RouterPoint;
import com.becb.processnewpoint.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})
class RoteiroServiceTest {

    @Autowired
    RoteiroService roteiroService;

    @Autowired
    UserService userService;

    private List<User> usuarioList = new ArrayList<>();
    @Test
    @Transactional
    void getRoteirosByUser() {

        saveRoteiro("Lisbon", "userId_1");
        saveRoteiro("Lisbon", "userId_2");
        saveRoteiro("Porto", "userId_1");

        List<Roteiro> roteiros = roteiroService.getRoteirosByUser(usuarioList.get(0));
        Assertions.assertEquals(2, roteiros.size());
        Assertions.assertEquals(3, roteiros.get(0).getPoints().size());
        Assertions.assertTrue(roteiros.get(0).getPoints().stream().sorted((o1,o2)-> o1.getPositionInRoute() ).collect(Collectors.toList())
                .get(0).getPoint().getTitle().contains("ponto0"));

    }

    @Test
    @Transactional
    void getRoteirosByCity() {
        saveRoteiro("Lisbon", "userId_1");
        saveRoteiro("Lisbon", "userId_2");
        saveRoteiro("Porto", "userId_1");

        List<Roteiro> roteiros = roteiroService.getRoteirosByCity("Lisbon");
        Assertions.assertEquals(2, roteiros.size());
        Assertions.assertEquals(3, roteiros.get(0).getPoints().size());
        Assertions.assertTrue(roteiros.get(0).getPoints().stream().sorted((o1,o2)-> o1.getPositionInRoute() ).collect(Collectors.toList())
                .get(0).getPoint().getTitle().contains("ponto0"));


     roteiros = roteiroService.getRoteirosByCity("PORTO");
        Assertions.assertEquals(1, roteiros.size());
        Assertions.assertEquals(3, roteiros.get(0).getPoints().size());
        Assertions.assertTrue(roteiros.get(0).getPoints().stream().sorted((o1,o2)-> o1.getPositionInRoute() ).collect(Collectors.toList())
                .get(0).getPoint().getTitle().contains("ponto0"));
    }


    void saveRoteiro(String city, String userId) {
        User user = createUser(userId);
        Point point = createPoint(city);
        point.setTitle(userId + "_ponto0");
        Point point2 = createPoint(city);
        point2.setTitle(userId + "_ponto2");
        Point point3 = createPoint(city);
        point2.setTitle(userId + "_ponto3");

        RouterPoint routerPoint = new RouterPoint(1, point);
        RouterPoint routerPoint2 = new RouterPoint(2, point2);
        RouterPoint routerPoint3 = new RouterPoint(3, point3);

        List<RouterPoint> routerPoints = Arrays.asList(routerPoint, routerPoint2, routerPoint3);

        Roteiro roteiro = roteiroService.saveRoteiro(routerPoints, user);
        Assertions.assertEquals(3, roteiro.getPoints().size());
        Assertions.assertEquals(city, roteiro.getCity());

    }

    private User createUser(String userId){
        User usuario = new User();
        usuario.setUserId(userId);
        usuarioList.add(usuario);
        return userService.saveUser(usuario);

    }
    private Point createPoint(String city){
        Point point = new Point();
        point.setCity(city);
        point.setPointId(UUID.randomUUID().toString());

        return point;

    }
}