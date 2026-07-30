package com.otto616.onTrack.dto;

import com.otto616.onTrack.models.ChecklistDocument;
import java.util.List;

public class ChecklistForm {

    private List<ChecklistDocument> documents;

    public ChecklistForm() {}

    public List<ChecklistDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ChecklistDocument> documents) {
        this.documents = documents;
    }
}