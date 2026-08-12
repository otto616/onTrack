package com.otto616.onTrack.models;

public class Enums {

    public enum ProviderType {
        FREELANCE_NO_WORKERS("Autònom Sense Treballadors"),
        FREELANCE_WITH_WORKERS("Empresa o Autònom Amb Treballadors"),
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
        COMPANY("Empresa"),
        WORKER("Treballadors"),
        MACHINERY("Maquinària");

        private final String description;

        DocumentCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}