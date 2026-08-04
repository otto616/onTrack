package com.otto616.onTrack.models;

public class Enums {

    public enum ProviderType {
        FREELANCE_NO_WORKERS("Freelance without workers"),
        FREELANCE_WITH_WORKERS("Company and Freelance with workers"),
        SUBCONTRACTOR_INDUSTRIAL("Subcontractor / Industrial");

        private final String description;

        ProviderType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum DocumentCategory {
        COMPANY("Company"),
        WORKER("Workers"),
        MACHINERY("Machinery");

        private final String description;

        DocumentCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}