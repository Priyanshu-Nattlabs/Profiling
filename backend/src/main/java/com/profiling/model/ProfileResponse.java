package com.profiling.model;

public class ProfileResponse {
    
    private Profile profile;
    private String templateText;

    public ProfileResponse() {
    }

    public ProfileResponse(Profile profile, String templateText) {
        this.profile = profile;
        this.templateText = templateText;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }
}

