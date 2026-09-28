package com.jupra.backend.dto;

import java.time.Instant;

/** Full enquiry detail shown when the founder clicks "view" on the dashboard. */
public class EnquiryDetailDto {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String organization;

    private String requirementType;

    private String message;

    private String source;

    private boolean hasAttachment;

    private String attachmentName;

    private String attachmentContentType;

    private Instant createdAt;

    public EnquiryDetailDto() {
    }

    public EnquiryDetailDto(Long id, String name, String email, String phone, String organization, String requirementType, String message, String source, boolean hasAttachment, String attachmentName, String attachmentContentType, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.organization = organization;
        this.requirementType = requirementType;
        this.message = message;
        this.source = source;
        this.hasAttachment = hasAttachment;
        this.attachmentName = attachmentName;
        this.attachmentContentType = attachmentContentType;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
