package com.becb.api.core;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("sqs.queue")
public class SQSProperties {

    public String new_point;
    public String aprovar_point;
    public String bloquear_point;
    public String new_file_to_map;
    public String not_approved;
    public String add_photo_point;
    public String add_audio_point;
    public String reset_password;
    public String point_vote;
    public String update_point;

}
