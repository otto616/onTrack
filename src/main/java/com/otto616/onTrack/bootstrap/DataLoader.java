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

            createDocumentType("Certificat Negatiu deutes Seguretat Social", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, true);
            createDocumentType("Certificat Positiu d'Inexistència de Deutes", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, true);
            createDocumentType("Certificat Hisenda Subcontractista", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, true);
            createDocumentType("Fotocòpia de la pòlissa de Responsabilitat Civil", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, false);
            createDocumentType("Rebut en vigor assegurança de Responsabilitat Civil", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, true);
            createDocumentType("Rebut d'autònom actualitzat", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, true);
            createDocumentType("Inscripció mútua", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, false);
            createDocumentType("Acta d'Adhesió al Pla de Seguretat i Salut (DOC PRL 1)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_NO_WORKERS, false);

            createDocumentType("Certificat Negatiu deutes Seguretat Social", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Certificat Positiu d'Inexistència de Deutes (AEAT)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Certificat Hisenda Subcontractista", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Fotocòpia de la pòlissa de Responsabilitat Civil", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, false);
            createDocumentType("Rebut en vigor assegurança de Responsabilitat Civil", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Fotocòpia de la pòlissa d'Accidents de treball", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, false);
            createDocumentType("Rebut en vigor de l'assegurança d'accidents", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("TC1 i TC2 (RLC i RNT) i rebut bancari", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("REA (Certificat d'inscripció)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Acta d'Adhesió al Pla de Seguretat i Salut (DOC PRL1 i PRL2)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, false);
            createDocumentType("Adreça i telèfon de la Mútua de Treball", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, false);
            createDocumentType("Avaluació dels riscos laborals i procediments", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, false);
            createDocumentType("Certificat del Servei de Prevenció", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);
            createDocumentType("Rebut del contracte del Servei de Prevenció", Enums.DocumentCategory.COMPANY, Enums.ProviderType.FREELANCE_WITH_WORKERS, true);

            createDocumentType("Certificat d'hisenda segons art. 43,1 f) LGT-modelo 01C", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Certificat corrent pagament Tesoreria art.42 E.T.", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Assegurança d'accidents (pòlissa)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("Rebut en vigor assegurança accidents", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Assegurança responsabilitat civil (pòlissa)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("Rebut en vigor assegurança responsabilitat civil", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Acreditació existència servei de prevenció", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("Rebut contracte de Prevenció", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Nòmines signades / rebuts bancaris", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("ITA", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("TC1 TC2 i rebut bancari", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Inscripció REA", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Fotocòpia IAE i pagament", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, true);
            createDocumentType("Avaluació riscos empresa", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("Mútua accidents (dades i centre assistencial)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("Relació de treballadors a l'obra", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("Adhesió pla de seguretat i salut", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);
            createDocumentType("Designació responsable (recurs preventiu)", Enums.DocumentCategory.COMPANY, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL, false);

            Enums.ProviderType[] withWorkers = {Enums.ProviderType.FREELANCE_WITH_WORKERS, Enums.ProviderType.SUBCONTRACTOR_INDUSTRIAL};
            for (Enums.ProviderType type : withWorkers) {
                createDocumentType("DNI / NIE / Carnet de conduir", Enums.DocumentCategory.WORKER, type, true);
                createDocumentType("Alta a la Seguretat Social (TA-2)", Enums.DocumentCategory.WORKER, type, false);
                createDocumentType("Certificat individual d'entrega d'EPIs", Enums.DocumentCategory.WORKER, type, false);
                createDocumentType("Certificat formació PRL (20h / 60h+6h)", Enums.DocumentCategory.WORKER, type, false);
                createDocumentType("Informació lloc de treball art. 18 i 19", Enums.DocumentCategory.WORKER, type, false);
                createDocumentType("Còpia bàsica del contracte de treball", Enums.DocumentCategory.WORKER, type, false);
                createDocumentType("Certificat d'aptitud mèdica", Enums.DocumentCategory.WORKER, type, true);
                createDocumentType("Autoritzacions ús maquinària", Enums.DocumentCategory.WORKER, type, false);
            }

            for (Enums.ProviderType type : Enums.ProviderType.values()) {
                createDocumentType("Rebut d'assegurança", Enums.DocumentCategory.MACHINERY, type, true);
                createDocumentType("Fitxa tècnica", Enums.DocumentCategory.MACHINERY, type, false);
                createDocumentType("Permís de circulació", Enums.DocumentCategory.MACHINERY, type, false);
                createDocumentType("ITV", Enums.DocumentCategory.MACHINERY, type, true);
                createDocumentType("Certificat CEE", Enums.DocumentCategory.MACHINERY, type, false);
                createDocumentType("Manual d'instruccions", Enums.DocumentCategory.MACHINERY, type, false);
                createDocumentType("Manteniment / Gestor de Residus", Enums.DocumentCategory.MACHINERY, type, true);
            }
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