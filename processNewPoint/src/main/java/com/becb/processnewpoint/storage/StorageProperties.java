package com.becb.processnewpoint.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("becb.storage.s3")
@Getter
@Setter
public class StorageProperties {

    private String bucket;
    private String accessKey;
    private String secretKey;
    private String region;
    private String directoryFile;

}
