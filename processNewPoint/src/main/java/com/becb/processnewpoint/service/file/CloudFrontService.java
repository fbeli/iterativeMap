package com.becb.processnewpoint.service.file;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.ion.Timestamp;

import java.util.Collection;
import java.util.HashSet;

@Service
public class CloudFrontService {


    @Autowired
    private AWSCredentials credentials;

    private AmazonCloudFrontClient getCloudWatchClient(){
        return new AmazonCloudFrontClient( new AWSStaticCredentialsProvider(credentials));

    }
    public void invalidateCache()  {

        Paths invalidationPaths = new Paths();
        Collection<String> col = new HashSet<>();
        col.add("/file/*");
        invalidationPaths.setItems(col);
        invalidationPaths.setQuantity(1);


        InvalidationBatch invalidationBatch = new InvalidationBatch(invalidationPaths, Timestamp.now().toString());

        CreateInvalidationRequest createInvalidationRequest = new CreateInvalidationRequest();
        createInvalidationRequest.setDistributionId("EEH1AZFSOYVMF");
        createInvalidationRequest.setInvalidationBatch(invalidationBatch);


        getCloudWatchClient().createInvalidation(createInvalidationRequest);


    }
}
