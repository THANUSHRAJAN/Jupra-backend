package com.jupra.backend.service;

import com.jupra.backend.domain.Enquiry;
import com.jupra.backend.dto.EnquiryDetailDto;
import com.jupra.backend.dto.EnquiryListItemDto;
import com.jupra.backend.exception.ApiException;
import com.jupra.backend.repository.EnquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class EnquiryService {

    private static final int PREVIEW_LENGTH = 140;

    private final EnquiryRepository enquiryRepository;

    public EnquiryService(EnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }

    public Long save(String name, String email, String phone, String organization,
                      String requirementType, String message, String source,
                      MultipartFile attachment) throws IOException {

        if (name == null || name.isBlank()) {
            throw new ApiException(400, "Please enter your name.");
        }
        if (email == null || email.isBlank()) {
            throw new ApiException(400, "Please enter a valid email address.");
        }

        Enquiry enquiry = new Enquiry();
        enquiry.setName(name.trim());
        enquiry.setEmail(email.trim());
        enquiry.setPhone(phone);
        enquiry.setOrganization(organization);
        enquiry.setRequirementType(requirementType);
        enquiry.setMessage(message);
        enquiry.setSource(source);

        if (attachment != null && !attachment.isEmpty()) {
            enquiry.setAttachmentName(attachment.getOriginalFilename());
            enquiry.setAttachmentContentType(attachment.getContentType());
            enquiry.setAttachmentData(attachment.getBytes());
        }

        Enquiry saved = enquiryRepository.save(enquiry);
        return saved.getId();
    }

    public List<EnquiryListItemDto> listAll() {
        return enquiryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toListItem)
                .toList();
    }

    public EnquiryDetailDto getDetail(Long id) {
        Enquiry e = find(id);
        EnquiryDetailDto dto = new EnquiryDetailDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setEmail(e.getEmail());
        dto.setPhone(e.getPhone());
        dto.setOrganization(e.getOrganization());
        dto.setRequirementType(e.getRequirementType());
        dto.setMessage(e.getMessage());
        dto.setSource(e.getSource());
        dto.setHasAttachment(hasAttachment(e));
        dto.setAttachmentName(e.getAttachmentName());
        dto.setAttachmentContentType(e.getAttachmentContentType());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }

    public Enquiry getForDocument(Long id) {
        Enquiry e = find(id);
        if (!hasAttachment(e)) {
            throw new ApiException(404, "No document is attached to this enquiry.");
        }
        return e;
    }

    private Enquiry find(Long id) {
        return enquiryRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Enquiry not found."));
    }

    private boolean hasAttachment(Enquiry e) {
        return e.getAttachmentData() != null && e.getAttachmentData().length > 0;
    }

    private EnquiryListItemDto toListItem(Enquiry e) {
        String preview = e.getMessage();
        if (preview != null && preview.length() > PREVIEW_LENGTH) {
            preview = preview.substring(0, PREVIEW_LENGTH).trim() + "…";
        }
        EnquiryListItemDto dto = new EnquiryListItemDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setEmail(e.getEmail());
        dto.setPhone(e.getPhone());
        dto.setOrganization(e.getOrganization());
        dto.setRequirementType(e.getRequirementType());
        dto.setMessagePreview(preview);
        dto.setSource(e.getSource());
        dto.setHasAttachment(hasAttachment(e));
        dto.setAttachmentName(e.getAttachmentName());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }
}
