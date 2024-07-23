package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.domain.RouterPoint;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.dto.RoteiroDto;
import com.becb.processnewpoint.repository.RoteiroRepository;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@Setter
public class RoteiroService {

    @Autowired
    private RoteiroRepository roteiroRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PointService pointService;



    public Roteiro saveRoteiro(Roteiro roteiro){

        roteiro.getPoints().forEach(point -> {point.setRoteiro(roteiro); });
        roteiroRepository.save(roteiro);

        return roteiro;
    }

    public Roteiro createRoteiro(String roteiroId, List<RouterPoint> routerPoints, User user, String city, String title, String description, String language) {

        Roteiro roteiro;

        if(roteiroId == null || roteiroId.isEmpty()) {
            roteiroId = UUID.randomUUID().toString();
            roteiro = new Roteiro(roteiroId);
        }else{
            roteiro = roteiroRepository.findById(roteiroId).orElse(new Roteiro(roteiroId));
        }

        roteiro.setUserOwner(user);
        organizeRoutes(roteiro, routerPoints);
        roteiro.setDescription(description);
        roteiro.setLanguage(SuportService.getLanguage(language).getValue());

        if((city==null || city.isEmpty() ) && roteiro.getCity() == null){
            Optional<RouterPoint> opt = routerPoints.stream().filter(
                    routerPoint -> routerPoint.getPoint().getCity() != null
            ).findFirst();
            opt.ifPresent( value -> roteiro.setCity(value.getPoint().getCity()));
        }else{
            roteiro.setCity(city);
        }
        if(title != null && !title.isEmpty()){
            roteiro.setTitle(title);
        }
        else{
            roteiro.setTitle("Router to " + roteiro.getCity());
        }
        return saveRoteiro(roteiro);
    }

    public void organizeRoutes(Roteiro route, List<RouterPoint> routerPoints) {

        final AtomicInteger last = new AtomicInteger(route.getPoints()
                                                          .stream()
                                                          .mapToInt(RouterPoint::getPositionInRoute)
                                                          .max()
                                                          .orElse(0));


        List<String> pointsInserted = route.getPoints().stream().map(RouterPoint::getPoint).map(Point::getPointId).collect(Collectors.toList());

        List<RouterPoint> routerPointsFiltered = routerPoints.stream()
                                                             .filter(routerPoint -> !pointsInserted.contains(routerPoint.getPoint().getPointId()))
                                                             .collect(Collectors.toList());
        routerPointsFiltered.forEach(routerPoint -> routerPoint.setPositionInRoute(last.incrementAndGet()));
        routerPointsFiltered.forEach(routePoint -> {
            route.getPoints().add(routePoint);
        });

    }

    public Roteiro getRoteiroById(String id) {
        return roteiroRepository.findById(id).orElse(null);
    }

    public Roteiro addNewRoute(String message) {

        JSONObject jsonObject;

        jsonObject = new JSONObject(message);
        User user = userService.getUserByUserId(jsonObject.optString("userId"));
        if(user == null)
            user = userService.getUserByInstagramId("@guidemapper");
        String city = jsonObject.optString("city");
        JSONArray jsonArray = jsonObject.optJSONArray("routePoints");
        List<RouterPoint> routerPoints = new ArrayList<>();

        if(jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Point point = pointService.getPointById(jsonObject1.optString("pointId"));
                if (point != null)
                    routerPoints.add(new RouterPoint(jsonObject1.optInt("positionInRoute"), point));
            }
        } else {
            if(!jsonObject.optString("pointId").isEmpty()){
                Point point = pointService.getPointById(jsonObject.optString("pointId"));
                if(point != null){
                    routerPoints.add(new RouterPoint(1, point));
                }
            }
        }
        String roteiroId = jsonObject.optString("roteiroId");

        String title = jsonObject.optString("title");
        String description = jsonObject.optString("description");
        String language =jsonObject.optString("language");
        return createRoteiro(roteiroId,routerPoints, user, city, title, description, language);
    }

    public List<RoteiroDto> getRoteiros(Pageable pageable, String city, String title, String userId) {

        if(title != null && !title.isEmpty()) {
            title ="%"+title+"%";
        }
        if(city != null && !city.isEmpty()) {
            city = "%"+city+"%";
        }
        Page<Roteiro> roteiros;
        if(city != null && !city.isEmpty() && title != null && !title.isEmpty() && userId != null && !userId.isEmpty()){
            roteiros = roteiroRepository.findAllByCityAndTitleAndUserOwner(pageable, city, title, userId);
        }else if(city != null && !city.isEmpty() && title != null && !title.isEmpty()){
             roteiros = roteiroRepository.findAllByCityAndTitle(pageable, city, title);
        }else if(city != null && !city.isEmpty() && userId != null && !userId.isEmpty()){
             roteiros = roteiroRepository.findAllByCityAndUserOwner(pageable, city, userId);
        }else if(city != null && !city.isEmpty()){
             roteiros = roteiroRepository.findAllByCity(pageable, city);
        }else if(title != null && !title.isEmpty() && userId != null && !userId.isEmpty()){
            roteiros = roteiroRepository.findAllByTitleAndUserOwner(pageable, title, userId);
        }else if(title != null && !title.isEmpty()){
            roteiros = roteiroRepository.findAllByTitleAndDescription(pageable, title);
        }else if(userId != null && !userId.isEmpty()){
             roteiros = roteiroRepository.findAllByUserOwner(pageable, userId);
        }else{
             roteiros = roteiroRepository.findAll(pageable);
        }

        List<RoteiroDto> roteirosList = roteiros.stream().map(RoteiroDto::new).collect(Collectors.toList());

        return roteirosList;
    }

    public Page<Roteiro> getRoteirosByUser(Pageable pageable, User user) {
        return roteiroRepository.findAllByUserOwner(pageable, user.getUserId());
    }



}
