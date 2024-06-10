package com.becb.processnewpoint.controller;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.dto.PointDto;
import com.becb.processnewpoint.service.PointService;
import com.becb.processnewpoint.service.translate.TranslateService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping//( produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE)
public class EditPointController {

    @Autowired
    PointService pointService;

    @Autowired
    TranslateService translateService;

    @GetMapping( value = "/point/users")
    @ResponseBody
    public List<PointDto> getByUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam("userId") String userId) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Point> points = pointService.getFatherPointsByUserId(pageable, userId);
        Page<PointDto> resultPage = pointService.convertPointToDto(points.toList());
        return resultPage.getContent();
    }

    @GetMapping( value = "/point/hi")
    @ResponseBody
    public String hi() {
        return "hi";
    }

    @GetMapping( value = "/point/")
    @ResponseBody
    public Point getByPointId(@RequestParam("pointId") String pointId) {
        return pointService.getPointById(pointId);
    }
    @GetMapping( value = "/point2/{pointId}")
    @ResponseBody
    public Point getByPointId2(@PathVariable("pointId") String pointId) {
        return  pointService.getPointById(pointId);
    }

    @GetMapping( value = "/point/reload_queue")
    @ResponseBody
    /**
     * Queues com problema, o chron está a chamar, esse endpoint adianta o chron para updates.
     */
    public ResponseEntity addChron() {
        pointService.adiantarChron();
        System.out.println("Adiantou chron");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping ( value = "/point/translate")
    @ResponseBody
    public ResponseEntity<PointDto> getByUser(@RequestParam String pointId, @RequestParam String language)  throws IOException {
        pointService.getPointById(pointId);
        PointDto  point = new PointDto(pointService.translate(pointService.getPointById(pointId), language));

        return new ResponseEntity<>(point, HttpStatus.CREATED);
    }

}
