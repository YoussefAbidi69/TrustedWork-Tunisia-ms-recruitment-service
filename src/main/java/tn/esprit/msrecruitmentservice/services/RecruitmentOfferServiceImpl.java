package tn.esprit.msrecruitmentservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.msrecruitmentservice.dto.RecruitmentOfferDTO;
import tn.esprit.msrecruitmentservice.entities.*;
import tn.esprit.msrecruitmentservice.repositories.IRecruitmentApplicationRepository;
import tn.esprit.msrecruitmentservice.repositories.IRecruitmentOfferRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruitmentOfferServiceImpl implements IRecruitmentOfferService {

    @Autowired
    private IRecruitmentOfferRepository offerRepository;

    @Autowired
    private IRecruitmentApplicationRepository applicationRepository;

    private RecruitmentOfferDTO toDTO(RecruitmentOffer entity) {
        RecruitmentOfferDTO dto = new RecruitmentOfferDTO();
        dto.setId(entity.getId());
        dto.setApplicationId(entity.getApplication().getId());
        dto.setSalairePropose(entity.getSalairePropose());
        dto.setPosteExact(entity.getPosteExact());
        dto.setDateDebutSouhaitee(entity.getDateDebutSouhaitee());
        dto.setPeriodeEssaiMois(entity.getPeriodeEssaiMois());
        dto.setAvantages(entity.getAvantages());
        dto.setDeadlineReponse(entity.getDeadlineReponse());
        dto.setContreOffreFreelancer(entity.getContreOffreFreelancer());
        dto.setStatus(entity.getStatus());
        dto.setDateEnvoi(entity.getDateEnvoi());
        return dto;
    }

    private RecruitmentOffer toEntity(RecruitmentOfferDTO dto) {
        RecruitmentOffer entity = new RecruitmentOffer();

        RecruitmentApplication application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application non trouvee : " + dto.getApplicationId()));
        entity.setApplication(application);

        entity.setSalairePropose(dto.getSalairePropose());
        entity.setPosteExact(dto.getPosteExact());
        entity.setDateDebutSouhaitee(dto.getDateDebutSouhaitee());
        entity.setPeriodeEssaiMois(dto.getPeriodeEssaiMois());
        entity.setAvantages(dto.getAvantages());
        entity.setDeadlineReponse(dto.getDeadlineReponse());
        entity.setStatus(OfferStatus.SENT);
        return entity;
    }

    @Override
    public RecruitmentOfferDTO create(RecruitmentOfferDTO dto) {
        RecruitmentOffer saved = offerRepository.save(toEntity(dto));
        // Mettre a jour le status de la candidature
        RecruitmentApplication app = saved.getApplication();
        app.setStatus(ApplicationStatus.OFFERED);
        applicationRepository.save(app);
        return toDTO(saved);
    }

    @Override
    public RecruitmentOfferDTO update(Long id, RecruitmentOfferDTO dto) {
        RecruitmentOffer existing = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre non trouvee : " + id));
        existing.setSalairePropose(dto.getSalairePropose());
        existing.setPosteExact(dto.getPosteExact());
        existing.setDateDebutSouhaitee(dto.getDateDebutSouhaitee());
        existing.setPeriodeEssaiMois(dto.getPeriodeEssaiMois());
        existing.setAvantages(dto.getAvantages());
        existing.setDeadlineReponse(dto.getDeadlineReponse());
        existing.setStatus(dto.getStatus());
        return toDTO(offerRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        offerRepository.deleteById(id);
    }

    @Override
    public RecruitmentOfferDTO getById(Long id) {
        return toDTO(offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre non trouvee : " + id)));
    }

    @Override
    public RecruitmentOfferDTO getByApplication(Long applicationId) {
        return toDTO(offerRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("Aucune offre pour cette candidature")));
    }

    @Override
    public List<RecruitmentOfferDTO> getAll() {
        return offerRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<RecruitmentOfferDTO> getByEntreprise(Long entrepriseId) {
        return offerRepository.findByEntrepriseId(entrepriseId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public RecruitmentOfferDTO updateStatus(Long id, OfferStatus status) {
        offerRepository.updateStatus(id, status);
        // Si acceptee → mettre a jour la candidature
        if (status == OfferStatus.ACCEPTED) {
            RecruitmentOffer offer = offerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Offre non trouvee : " + id));
            RecruitmentApplication app = offer.getApplication();
            app.setStatus(ApplicationStatus.HIRED);
            applicationRepository.save(app);
        }
        return getById(id);
    }

    @Override
    public RecruitmentOfferDTO addContreOffre(Long id, String contreOffre) {
        offerRepository.addContreOffre(id, contreOffre);
        return getById(id);
    }
}