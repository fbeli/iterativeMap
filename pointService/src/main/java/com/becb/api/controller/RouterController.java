package com.becb.api.controller;

import com.becb.api.dto.RouteDto;
import com.becb.api.service.PointSupport;
import com.becb.api.service.RouteService;
import com.becb.api.service.sqs.SqsService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping//( produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE)
public class RouterController {

    @Autowired
    public RouterController( RouteService routeService, SqsService sqsService,   @Value("${sqs.queue.add_route}") String addRouteQueue) {
        this.routeService = routeService;
        this.sqsService = sqsService;
        this.addRouteQueue = addRouteQueue;
    }



    RouteService routeService;

    SqsService sqsService;

    String addRouteQueue;

    @GetMapping(value = "/routes")
    public ResponseEntity<Object> getRoutes(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @RequestParam(value ="instagram" ,  defaultValue = "") String instagram,
                                                @RequestParam(value ="title",  defaultValue = "") String title,
                                                @RequestParam(value ="city",  defaultValue = "") String city) throws Exception {

        return new ResponseEntity<>(routeService.getRoute(page, size, instagram, title, city), HttpStatus.valueOf(200));

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/routes")
    public void addRoute(@RequestBody RouteDto routeDto, HttpServletRequest request) {

        String message = configPoint(routeDto, request);
        sqsService.sendMessage(message, addRouteQueue);

    }

    private JSONObject getToken(HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        return new JSONObject(payload);
    }
    private String configPoint(RouteDto routeDto, HttpServletRequest request) {

        JSONObject jsonObject = getToken(request);

        routeDto.setUser_id(jsonObject.getString("usuario_id"));

        return PointSupport.unicodeEscapeToUtf8(routeDto.toString());
    }

}