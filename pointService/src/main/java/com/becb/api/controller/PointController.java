package com.becb.api.controller;

import com.becb.api.dto.ArquivoDto;
import com.becb.api.dto.PointDto;
import com.becb.api.dto.PointResponse;
import com.becb.api.service.sqs.SqsService;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;


import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.logging.Logger;

@Controller
@RequestMapping( produces = "application/json;charset=UTF-8")
public class PointController {

    private static final Logger logger = Logger.getLogger(PointController.class.getName());

    @Autowired
    SqsService  sqsService;

    @Value("${sqs.queue.aprovar_point}")
    String aproveQueueName;

    @Value("${sqs.queue.bloquear_point}")
    String bloquearQueueName;

    @Value("${sqs.queue.new_point}")
    String newPointQueueName;

    @Value("${sqs.queue.new_file_to_map}")
    String newFileToMapQueueName;

    @Value("${sqs.queue.not_approved}")
    String notApprovedQueueName;

    @Value("${file.endpoint}")
    String fileEndpoint;

    @GetMapping("/aprovar/{id}/{user_mail}")
    //@PreAuthorize("isAuthenticated()")
    @ResponseBody
    public PointResponse aprovar(@PathVariable String id, @PathVariable String user_mail) {

        String formatedPoint = this.aprovePoint(id, user_mail);

        try{
            sqsService.sendMessage(formatedPoint, aproveQueueName);
        }catch (Exception e){
            logger.severe("Error to add point: "+e.getMessage());
            return new PointResponse("500", "Error to add point");
        }

        return new PointResponse("Point added successfully");

    }

    @GetMapping("/bloquear/{id}/{user_mail}")
    @ResponseBody
    public PointResponse bloquear(@PathVariable String id, @PathVariable String user_mail) {

        String formatedPoint = this.aprovePoint(id, user_mail);

        try{
            sqsService.sendMessage(formatedPoint, bloquearQueueName);
        }catch (Exception e){
            logger.severe("Error to add point: "+e.getMessage());
            return new PointResponse("500", "Error to add point");
        }

        return new PointResponse("Point added successfully");

    }

    @GetMapping("/gerarArquivoAprovacao")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public PointResponse gerarArquivoAprovacao(HttpServletRequest request){

        String fileName = getFileName("pointNaoCadastrado");
        String formatedPoint = this.configArquivo(new ArquivoDto(), request, fileName);

        try{
            sqsService.sendMessage(formatedPoint, this.notApprovedQueueName);
        }catch (Exception e){
            logger.severe("Erro para gerar Arquivo de aprovacao: "+e.getMessage());
            return new PointResponse("500", "Error to add point");
        }
        return new PointResponse("Arquivo solicitado com sucesso: "+fileEndpoint+fileName);
    }

    @GetMapping("/gerarArquivoParaMapa")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public PointResponse gerarArquivoParaMapa(HttpServletRequest request){
        String fileName = getFileName("mapFile");
        String formatedPoint = this.configArquivo(new ArquivoDto(), request, fileName);

        try{
            sqsService.sendMessage(formatedPoint, newFileToMapQueueName);
        }catch (Exception e){
            logger.severe("Erro para gerar Arquivo para mapa: "+e.getMessage());
            return new PointResponse("500", "Error to add point");
        }
        return new PointResponse("Arquivo solicitado com sucesso: "+fileEndpoint+fileName);

    }

    /**
     * Cadastrar um ponto
     */
    @PostMapping("/point")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public PointResponse cadastro(@RequestBody PointDto pointDto, HttpServletRequest request) {

        String formatedPoint = configPoint(pointDto,  request);
        logger.info("Point to Add: "+formatedPoint);
        try{
            //SqsService  sqsService = new SqsService();
            sqsService.sendMessage(formatedPoint);
        }catch (Exception e){
            logger.severe("Error to add point: "+e.getMessage());
            return new PointResponse("500", "Error to add point");
        }

        return new PointResponse("Point added successfully");
    }
    private String configPoint(PointDto pointDto, HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject = new JSONObject(payload);

       pointDto.setUser_id(jsonObject.getString("usuario_id"));
       pointDto.setUser_email(jsonObject.getString("user_name"));
       pointDto.setUser_name(jsonObject.getString("nome_completo"));

       return pointDto.toString();
    }

    private String aprovePoint(String id, String user_mail) {

        PointDto pointDto = new PointDto();
        pointDto.setPointId(id);
        pointDto.setUser_email(user_mail);

        return pointDto.toString();
    }

    private String configArquivo(ArquivoDto arquivoDto, HttpServletRequest request, String fileName) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject = new JSONObject(payload);

        arquivoDto.setUser_id(jsonObject.getString("usuario_id"));
        arquivoDto.setUser_email(jsonObject.getString("user_name"));
        arquivoDto.setUser_name(jsonObject.getString("nome_completo"));
        arquivoDto.setFile_name(fileName);
        return arquivoDto.toString();
    }
    private String getFileName(String name){
        return name+"_"+ LocalDateTime.now() +".html";
    }
}
