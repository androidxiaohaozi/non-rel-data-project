package com.example.nonreldataproject.controller;

import com.example.nonreldataproject.dto.OpcDataDTO;
import com.example.nonreldataproject.utils.Opcutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    public Opcutil opcutil;

    /**
     * read
     * @return
     */
    @GetMapping("/read")
    public OpcDataDTO read(Integer namaspaceIndex, String tagName) {
        try {
            return opcutil.read(namaspaceIndex,tagName);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
