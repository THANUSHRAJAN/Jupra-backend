package com.jupra.backend.controller;

import com.jupra.backend.service.EnquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/** Public endpoint used by the website's Contact form and chatbot. */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final EnquiryService enquiryService;

    public ContactController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> submit(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) String requirementType,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) MultipartFile attachment
    ) throws IOException {
        Long id = enquiryService.save(name, email, phone, organization, requirementType, message, source, attachment);
        return Map.of("id", id, "message", "Enquiry received.");
    }
}
