package com.becb.api.controller;

import com.becb.api.dto.ArquivoDto;
import com.becb.api.dto.PointDto;
import com.becb.api.dto.PointResponse;
import com.becb.api.service.ArquivoService;
import com.becb.api.service.sqs.SqsService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping//( produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE)
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SqsService sqsService;

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

    @Autowired
    ArquivoService arquivoService;

    @GetMapping(value = "/aprovar/{id}/{user_mail}")
    @ResponseBody
    public String aprovar(@PathVariable String id, @PathVariable String user_mail) {

        logger.info("Aprovando point {} by {}", id, user_mail);
        String formatedPoint = this.aprovePoint(id, user_mail);

        try{
            sqsService.sendMessage(formatedPoint, aproveQueueName);
        }catch (Exception e){
            logger.error("Erro ao aprovar ponto point: "+e.getMessage());
            return new PointResponse("500", "Erro para aprovar ponto"+e.getMessage()).toString();
        }

        return new PointResponse("Point added successfully").toString();

    }

    @GetMapping("/bloquear/{id}/{user_mail}")
    @ResponseBody
    public PointResponse bloquear(@PathVariable String id, @PathVariable String user_mail) {

        logger.info("bloqueando  point {} by {}", id, user_mail);
        String formatedPoint = this.aprovePoint(id, user_mail);

        try{
            sqsService.sendMessage(formatedPoint, bloquearQueueName);
        }catch (Exception e){
            logger.error("Erro  ao bloquear ponto: "+e.getMessage());
            return new PointResponse("500", "Error para bloquear ponto" + e.getMessage());
        }

        return new PointResponse("Point added successfully");

    }

    @GetMapping("/gerarArquivoAprovacao")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public PointResponse gerarArquivoAprovacao(HttpServletRequest request){

        String formatedPoint = arquivoService.configArquivo(new ArquivoDto(), request, arquivoService.getFileName("pointNaoCadastrado"));

        try{
            sqsService.sendMessage(formatedPoint, this.notApprovedQueueName);
        }catch (Exception e){
            logger.error("Erro para gerar Arquivo de aprovacao: "+e.getMessage());
            return new PointResponse("500", "Erro para gerar arquivo de aprovacao: " + e.getMessage());
        }
        return new PointResponse("Arquivo solicitado com sucesso: <a href=\""+fileEndpoint+arquivoService.getFileName("pointNaoCadastrado")+"\">" +fileEndpoint+arquivoService.getFileName("pointNaoCadastrado")+"</a>");
    }

    @GetMapping("/gerarArquivoParaMapa")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public PointResponse gerarArquivoParaMapa(HttpServletRequest request){

        String formatedPoint = arquivoService.configArquivo(new ArquivoDto(), request, arquivoService.getFileName("mapFile"));

        try{
            sqsService.sendMessage(formatedPoint, newFileToMapQueueName);
        }catch (Exception e){
            logger.error("Erro para gerar Arquivo para mapa: {}", e.getMessage());
            return new PointResponse("500", "Erro para gerar Arquivo para mapa : "+e.getMessage());
        }
        return new PointResponse("Arquivo solicitado com sucesso: <a href=\" "+fileEndpoint+arquivoService.getFileName("mapFile")+"\">" +fileEndpoint+arquivoService.getFileName("mapFile")+"</a>");

    }


    private String aprovePoint(String id, String user_mail) {

        PointDto pointDto = new PointDto();
        pointDto.setPointId(id);
        pointDto.setUser_email(user_mail);

        return pointDto.toString();
    }
}
