package com.becb.api.controller;

import com.becb.api.dto.PointDto;
import com.becb.api.dto.PointResponse;
import com.becb.api.service.sqs.SqsService;
import org.json.JSONObject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.logging.Logger;

@Controller
public class PointController {



    private static final Logger logger = Logger.getLogger(PointController.class.getName());

    /**
     * Cadastrar um ponto
     */
    @PostMapping("/point")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public PointResponse cadastro(@RequestBody PointDto pointDto, HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");

        String formatedPoint = configPoint(pointDto, token);
        logger.info("Point to Add: "+formatedPoint);
        try{
            SqsService  sqsService = new SqsService();

            sqsService.sendMessage(formatedPoint);

        }catch (Exception e){
            logger.severe("Error to add point: "+e.getMessage());
            return new PointResponse("500", "Error to add point");
        }

        return new PointResponse("Point added successfully");
    }
    private String configPoint(PointDto pointDto, String token) {

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject = new JSONObject(payload);

       pointDto.setUser_id(jsonObject.getString("usuario_id"));
       pointDto.setEmail(jsonObject.getString("user_name"));
       pointDto.setUser_name(jsonObject.getString("nome_completo"));

        return pointDto.toString();
    }
}
