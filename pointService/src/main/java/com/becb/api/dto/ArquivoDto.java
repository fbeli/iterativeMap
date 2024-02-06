package com.becb.api.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@NoArgsConstructor

public class ArquivoDto {
    private String file_name;
    private String user_id;
    private String user_name;
    private String user_email;


    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }


}
