package com.otto616.onTrack.bootstrap;

import com.otto616.onTrack.models.Provider;
import com.otto616.onTrack.models.DocumentType;
import com.otto616.onTrack.models.Enums;
import com.otto616.onTrack.repositories.ChecklistDocumentRepository;
import com.otto616.onTrack.repositories.ProviderRepository;
import com.otto616.onTrack.repositories.DocumentTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProviderRepository providerRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ChecklistDocumentRepository checklistDocumentRepository;

    public DataLoader(ProviderRepository providerRepository, DocumentTypeRepository documentTypeRepository, ChecklistDocumentRepository checklistDocumentRepository) {
        this.providerRepository = providerRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.checklistDocumentRepository = checklistDocumentRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (documentTypeRepository.count() == 0) {
            createDocumentType("Rebut Assegurança Responsabilitat Civil", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Certificat Negatiu deutes Seguretat Social", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, false);
            createDocumentType("Inscripció mútua", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, false);
            createDocumentType("TC1 i TC2 del mes anterior", Enums.DocumentCategory.WORKER, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Certificat aptitud mèdica", Enums.DocumentCategory.WORKER, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("ITV Maquinària", Enums.DocumentCategory.MACHINERY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("REA", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Manual Màquina", Enums.DocumentCategory.MACHINERY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
        }

        if (providerRepository.count() == 0) {
            createProvider("Construccions Falses SL", "B12345678", Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL);
            createProvider("Instal·lacions Elèctriques Pérez", "B98765432", Enums.ProviderType.FREELANCE_WITH_WORKERS);
            createProvider("Joan Garcia - Llauner", "47851236M", Enums.ProviderType.FREELANCE_NO_WORKERS);
            createProvider("Excavacions i Moviments de Terra SA", "A55555555", Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL);
            createProvider("Climatització Costa", "B11112222", Enums.ProviderType.FREELANCE_WITH_WORKERS);
            createProvider("Marta Roig - Arquitecta", "39998887X", Enums.ProviderType.FREELANCE_NO_WORKERS);
        }
    }

    private void createDocumentType(String name, Enums.DocumentCategory category, Enums.ProviderType providerType, boolean expires) {
        DocumentType doc = new DocumentType();
        doc.setName(name);
        doc.setCategory(category);
        doc.setProviderType(providerType);
        doc.setExpires(expires);
        doc.setVerificationUrl("");
        documentTypeRepository.save(doc);
    }

    private void createProvider(String name, String taxId, Enums.ProviderType providerType) {
        Provider provider = new Provider();
        provider.setName(name);
        provider.setTaxId(taxId);
        provider.setProviderType(providerType);
        providerRepository.save(provider);
    }
}