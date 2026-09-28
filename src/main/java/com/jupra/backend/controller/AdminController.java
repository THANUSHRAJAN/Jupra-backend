package com.jupra.backend.controller;

import com.jupra.backend.domain.Enquiry;
import com.jupra.backend.dto.EnquiryDetailDto;
import com.jupra.backend.dto.EnquiryListItemDto;
import com.jupra.backend.service.EnquiryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Everything here is protected by {@link com.jupra.backend.config.JwtAuthFilter} because its
 * path starts with "/api/admin" — only a signed-in founder (valid JWT) can reach these routes.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final EnquiryService enquiryService;

    public AdminController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    /** Lets the frontend confirm a stored token is still valid after a page reload. */
    @GetMapping("/me")
    public Map<String, String> me(HttpServletRequest request) {
        return Map.of("email", String.valueOf(request.getAttribute("adminEmail")));
    }

    @GetMapping("/enquiries")
    public List<EnquiryListItemDto> listEnquiries() {
        return enquiryService.listAll();
    }

    @GetMapping("/enquiries/{id}")
    public EnquiryDetailDto getEnquiry(@PathVariable Long id) {
        return enquiryService.getDetail(id);
    }

    /** Streams the attached document inline so it opens in the browser tab rather than downloading. */
    @GetMapping("/enquiries/{id}/document")
    public ResponseEntity<byte[]> getDocument(@PathVariable Long id) {
        Enquiry enquiry = enquiryService.getForDocument(id);

        MediaType contentType;
        try {
            contentType = enquiry.getAttachmentContentType() != null
                    ? MediaType.parseMediaType(enquiry.getAttachmentContentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        String filename = enquiry.getAttachmentName() != null ? enquiry.getAttachmentName() : "document";

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(filename).build().toString())
                .body(enquiry.getAttachmentData());
    }
}
