package com.otto616.onTrack.bootstrap;

import com.otto616.onTrack.models.Client;
import com.otto616.onTrack.models.DocumentType;
import com.otto616.onTrack.models.Enums;
import com.otto616.onTrack.repositories.ChecklistDocumentRepository;
import com.otto616.onTrack.repositories.ClientRepository;
import com.otto616.onTrack.repositories.DocumentTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ChecklistDocumentRepository checklistDocumentRepository;

    public DataLoader(ClientRepository clientRepository, DocumentTypeRepository documentTypeRepository, ChecklistDocumentRepository checklistDocumentRepository) {
        this.clientRepository = clientRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.checklistDocumentRepository = checklistDocumentRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (documentTypeRepository.count() == 0) {
            createDocumentType("Rebut Assegurança Responsabilitat Civil", Enums.DocumentCategory.COMPANY, Enums.ClientType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Certificat Negatiu deutes Seguretat Social", Enums.DocumentCategory.COMPANY, Enums.ClientType.FREELANCE_NO_WORKERS, false);
            createDocumentType("Inscripció mútua", Enums.DocumentCategory.COMPANY, Enums.ClientType.FREELANCE_NO_WORKERS, false);
            createDocumentType("TC1 i TC2 del mes anterior", Enums.DocumentCategory.WORKER, Enums.ClientType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Certificat aptitud mèdica", Enums.DocumentCategory.WORKER, Enums.ClientType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("ITV Maquinària", Enums.DocumentCategory.MACHINERY, Enums.ClientType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("REA", Enums.DocumentCategory.COMPANY, Enums.ClientType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Manual Màquina", Enums.DocumentCategory.MACHINERY, Enums.ClientType.SUBCONTRACTOR_INDUSTRIAL, false);
        }

        if (clientRepository.count() == 0) {
            createClient("Construccions Falses SL", "B12345678", Enums.ClientType.SUBCONTRACTOR_INDUSTRIAL);
            createClient("Instal·lacions Elèctriques Pérez", "B98765432", Enums.ClientType.FREELANCE_WITH_WORKERS);
            createClient("Joan Garcia - Llauner", "47851236M", Enums.ClientType.FREELANCE_NO_WORKERS);
            createClient("Excavacions i Moviments de Terra SA", "A55555555", Enums.ClientType.SUBCONTRACTOR_INDUSTRIAL);
            createClient("Climatització Costa", "B11112222", Enums.ClientType.FREELANCE_WITH_WORKERS);
            createClient("Marta Roig - Arquitecta", "39998887X", Enums.ClientType.FREELANCE_NO_WORKERS);
        }
    }

    private void createDocumentType(String name, Enums.DocumentCategory category, Enums.ClientType clientType, boolean expires) {
        DocumentType doc = new DocumentType();
        doc.setName(name);
        doc.setCategory(category);
        doc.setClientType(clientType);
        doc.setExpires(expires);
        documentTypeRepository.save(doc);
    }

    private void createClient(String name, String taxId, Enums.ClientType clientType) {
        Client client = new Client();
        client.setName(name);
        client.setTaxId(taxId);
        client.setClientType(clientType);
        clientRepository.save(client);
    }
}