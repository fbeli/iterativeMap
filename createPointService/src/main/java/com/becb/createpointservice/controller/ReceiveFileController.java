package com.becb.createpointservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ReceiveFileController {

    @PostMapping("/audio_upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("point_id") String pointId,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {



        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

}
