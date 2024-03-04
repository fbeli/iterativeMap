package com.becb.api.service;

import com.becb.api.dto.ArquivoDto;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class ArquivoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String configArquivo(ArquivoDto arquivoDto, HttpServletRequest request, String fileName) {

        if(request.getHeader("Authorization") != null ) {
            String token = request.getHeader("Authorization").replace("Bearer ", "");

            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            JSONObject jsonObject = new JSONObject(payload);
            arquivoDto.setUser_id(jsonObject.getString("usuario_id"));
            arquivoDto.setUser_email(jsonObject.getString("user_name"));
            arquivoDto.setUser_name(jsonObject.getString("nome_completo"));
        } else {
            logger.error("Authorization header is missing");
            arquivoDto.setUser_id("empty");
            arquivoDto.setUser_email("fred.belisario@gmail.com");
            arquivoDto.setUser_name("Frederico Belisario");
        }

        arquivoDto.setFile_name(fileName);
        return arquivoDto.toString();
    }

    public String getFileName(String name){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        if(name.equals("mapFile"))
            return name+"_.geojson";

        return name+"_"+ formatter.format(LocalDateTime.now()) +".html";
    }
}
