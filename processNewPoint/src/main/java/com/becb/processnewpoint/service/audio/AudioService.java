package com.becb.processnewpoint.service.audio;

import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.service.file.AmazonS3Service;
import com.voicerss.tts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class AudioService {

    @Autowired
    public AudioService(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    @Value("${becb.storage.s3.bucket}")
    private String bucket;

    private final AmazonS3Service amazonS3Service;

    @Value("${vss.key}")
    private String vssKey;


    public String saveAudio(String point_id, String text, LanguageEnum languageEnum) {

        VoiceProvider tts = new VoiceProvider(vssKey);

        VoiceParameters params = new VoiceParameters(text, getLanguage(languageEnum));
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);

        try {
            byte[] voice;

            voice = tts.speech(params);

            InputStream inputStream = new ByteArrayInputStream(voice);

            String namefile = point_id + "_ia.mp3";

            amazonS3Service.saveFile(bucket, "audio", inputStream, namefile);
            return "audio/" + namefile;
        } catch (Exception e) {
            return "";
        }
    }

    private String getLanguage(LanguageEnum language) {

        switch (language.getValue()) {
            case "PT":
                return Languages.Portuguese_Brazil;
            case "DE":
                return Languages.German_Germany;
            case "FR":
                return Languages.French_France;
            case "ES":
                return Languages.Spanish_Spain;
            case "EN":
                return Languages.English_GreatBritain;
            default:
                return Languages.English_UnitedStates;
        }

    }
}
