package com.becb.processnewpoint.service.translate;


import com.becb.processnewpoint.domain.Point;

import java.io.IOException;


public interface TranslateServiceInterface {

    Point translate(String pointId, String language) throws IOException;

    String getTranslateFromJason(String str, String lang) throws IOException;

    String translateText(String text, String languageDestino) throws IOException;

}
