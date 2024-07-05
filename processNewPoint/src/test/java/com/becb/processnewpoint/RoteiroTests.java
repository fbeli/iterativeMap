package com.becb.processnewpoint;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.domain.RouterPoint;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.repository.RoteiroRepository;
import com.becb.processnewpoint.service.RoteiroService;
import com.becb.processnewpoint.service.UserService;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class RoteiroTests {


    RoteiroService roteiroService;

    public void setServices(UserService userService, RoteiroService roteiroService, RoteiroRepository roteiroRepository) {
        setServices(userService, roteiroService);
        roteiroService.setRoteiroRepository(roteiroRepository);

    }

    public void setServices(UserService userService, RoteiroService roteiroService) {
        this.userService = userService;
        this.roteiroService = roteiroService;

    }

    UserService userService;
    protected List<User> usuarioList = new ArrayList<>();
    protected List<Roteiro> roteiroList = new ArrayList<>();

    protected void saveRoteiro(String city, String userId) {
        User user = createUser(userId);
        user.setInstagram("@"+userId);
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

        Roteiro roteiro = roteiroService.createRoteiro(null,routerPoints, user, null, "Roteiro "+city, "Essa é a descrição para um roteiro na cidade de "+city);
        Assertions.assertEquals(3, roteiro.getPoints().size());
        Assertions.assertEquals(city, roteiro.getCity());
        roteiroList.add(roteiro);
    }

    protected User createUser(String userId) {
        User usuario = new User();
        usuario.setUserId(userId);
        usuario.setInstagram("@"+userId);
        usuarioList.add(usuario);
        userService.saveUser(usuario);
        return usuario;
    }

    protected Point createPoint(String city) {
        Point point = new Point();
        point.setCity(city);
        point.setPointId(UUID.randomUUID().toString());

        return point;

    }
}
