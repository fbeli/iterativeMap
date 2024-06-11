package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.domain.RouterPoint;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.repository.RoteiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RoteiroService {

    @Autowired
    RoteiroRepository roteiroRepository;

    public List<Roteiro> getRoteirosByUser(User user) {
        return roteiroRepository.findAllByUserOwner(user.getUserId());
    }


    public List<Roteiro> getRoteirosByCity(String city) {
        return roteiroRepository.findAllByCity(city);
    }

    public Roteiro saveRoteiro(List<RouterPoint> routerPoints, User user) {
        Roteiro roteiro = new Roteiro();
        roteiro.setUserOwner(user);
        roteiro.setPoints(routerPoints);
        Optional<RouterPoint> opt = routerPoints.stream().filter(
                routerPoint -> routerPoint.getPoint().getCity() != null
        ).findFirst();
        opt.ifPresent( value -> roteiro.setCity(value.getPoint().getCity()));

        return roteiroRepository.save(roteiro);
    }

    public Roteiro saveRoteiro(List<RouterPoint> routerPoints, User user, String city) {
        Roteiro roteiro = new Roteiro();
        roteiro.setUserOwner(user);
        roteiro.setPoints(routerPoints);
        roteiro.setCity(city);
        return roteiroRepository.save(roteiro);
    }

}
