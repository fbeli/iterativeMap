package com.becb.processnewpoint.controller;

import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.dto.RoteiroDto;
import com.becb.processnewpoint.dto.RoteiroResponseDto;
import com.becb.processnewpoint.service.RoteiroService;
import com.becb.processnewpoint.service.UserService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping//( produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE)
public class RoteiroController{

    public RoteiroController(@Autowired RoteiroService roteiroService,
                             @Autowired UserService userService) {
        this.roteiroService = roteiroService;
        this.userService = userService;
    }

    RoteiroService roteiroService;
    UserService userService;


    @GetMapping(value = "/rotas/instagram")
    @ResponseBody
    public List<Roteiro> getRoteirosByUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                           @RequestParam("instagram") String instagram){

        Pageable pageable = PageRequest.of(page, size);

        User user = userService.getUserByInstagramId(instagram);
        if (user != null) {
            return roteiroService.getRoteirosByUser(pageable, user).getContent();
        }
        return new ArrayList<>();
    }


    @GetMapping(value = "/routes")
    @ResponseBody
    public ResponseEntity<Object> getRoteiros(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestParam(value ="instagram" ,  defaultValue = "") String instagram,
                                              @RequestParam(value ="title",  defaultValue = "") String title,
                                              @RequestParam(value ="city",  defaultValue = "") String city) {

         Pageable pageable = PageRequest.of(page, size);
         List <RoteiroDto> roteiros = new ArrayList<>();


        String userId="";
        if(instagram != null && !instagram.isEmpty()){
            User user = userService.getUserByInstagramId(instagram);

            if (user != null) {
                userId = user.getUserId();
            }
        }
        roteiros = roteiroService.getRoteiros(pageable, city, title, userId);

        return new ResponseEntity<>(new RoteiroResponseDto(city, title, instagram, page, size, roteiros), HttpStatus.OK);

    }

}
