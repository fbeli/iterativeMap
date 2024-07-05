package com.becb.api.service;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteServiceTest {

    RouteService routeService = new RouteService();

    @Test
    void getQuery() throws Exception {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("page", "page"));
        params.add(new BasicNameValuePair("size", "size"));
        params.add(new BasicNameValuePair("instagram", "instagram"));
        params.add(new BasicNameValuePair("title", "title"));
        params.add(new BasicNameValuePair("city", "city"));

        System.out.println(routeService.getQuery(params));
    }
}