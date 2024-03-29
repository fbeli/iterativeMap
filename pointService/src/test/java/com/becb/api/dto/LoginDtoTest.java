package com.becb.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginDtoTest {

    @Test
    void setupFromString() {
        String allContent = "{\"id\":\"7fa3d5a4-20be-496e-a90e-d4336297dcc3\",\"phone\":\"+351966960358\",\"name\":\"Guide Mapper Team\",\"email\":\"guidemapper@gmail.com\",\"password\":null,\"telefone\":\"+351966960358\",\"country\":\"Portugal\",\"guide\":true,\"born_date\":\"1980-07-19\",\"instagram\":\"@guidemapper\",\"share\":true,\"description\":\"The team that make you happier\",\"grupoId\":null}";

        LoginDto dto = new LoginDto();
        dto.setupFromString(allContent);

        assertEquals("Guide Mapper Team", dto.getName());
        assertEquals("guidemapper@gmail.com", dto.getEmail());
        assertEquals("+351966960358", dto.getPhone());
        assertEquals("1980-07-19", dto.getBorn_date());
        assertEquals("true", dto.getShare());
        assertEquals("7fa3d5a4-20be-496e-a90e-d4336297dcc3", dto.getUserId());


    }

    @Test
    void setupFromStringWithEmpty() {
        String allContent = "{\"id\":\"7fa3d5a4-20be-496e-a90e-d4336297dcc3\",\"name\":\"Guide Mapper Team\",\"email\":\"guidemapper@gmail.com\",\"password\":null,\"country\":\"Portugal\",\"guide\":true,\"born_date\":\"1980-07-19\",\"instagram\":\"@guidemapper\",\"share\":true,\"description\":\"The team that make you happier\",\"grupoId\":null}";

        LoginDto dto = new LoginDto();
        dto.setupFromString(allContent);

        assertEquals("Guide Mapper Team", dto.getName());
        assertEquals("guidemapper@gmail.com", dto.getEmail());
        assertEquals("", dto.getPhone());
        assertEquals("1980-07-19", dto.getBorn_date());
        assertEquals("true", dto.getShare());


    }
}