package com.jupra.backend.dto;

import java.time.Instant;

/** Lightweight row for the founder dashboard table (no file bytes, message truncated). */
public class EnquiryListItemDto {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String organization;

    private String requirementType;

    private String messagePreview;

    private String source;

    private boolean hasAttachment;

    private String attachmentName;

    private Instant createdAt;

    public EnquiryListItemDto() {
    }

    public EnquiryListItemDto(Long id, String name, String email, String phone, String organization, String requirementType, String messagePreview, String source, boolean hasAttachment, String attachmentName, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.organization = organization;
        this.requirementType = requirementType;
        this.messagePreview = messagePreview;
        this.source = source;
        this.hasAttachment = hasAttachment;
        this.attachmentName = attachmentName;
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

    public String getMessagePreview() {
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
