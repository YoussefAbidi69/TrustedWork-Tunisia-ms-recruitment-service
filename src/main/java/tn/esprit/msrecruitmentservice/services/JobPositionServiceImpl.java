package tn.esprit.msrecruitmentservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.msrecruitmentservice.dto.JobPositionDTO;
import tn.esprit.msrecruitmentservice.entities.JobPosition;
import tn.esprit.msrecruitmentservice.entities.JobStatus;
import tn.esprit.msrecruitmentservice.repositories.IJobPositionRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobPositionServiceImpl implements IJobPositionService {

    @Autowired
    private IJobPositionRepository jobPositionRepository;

    // ── Mapping Entity → DTO ──────────────────────────────────────────────
    private JobPositionDTO toDTO(JobPosition entity) {
        JobPositionDTO dto = new JobPositionDTO();
        dto.setId(entity.getId());
        dto.setEntrepriseId(entity.getEntrepriseId());
        dto.setTitre(entity.getTitre());
        dto.setDescription(entity.getDescription());
        dto.setTypeContrat(entity.getTypeContrat());
        dto.setSalaireMin(entity.getSalaireMin());
        dto.setSalaireMax(entity.getSalaireMax());
        dto.setLocalisation(entity.getLocalisation());
        dto.setRemote(entity.getRemote());
        dto.setExperienceRequiseAns(entity.getExperienceRequiseAns());
        dto.setSkillsRequis(entity.getSkillsRequis());
        dto.setDeadline(entity.getDeadline());
        dto.setStatus(entity.getStatus());
        dto.setNombreCandidatures(entity.getNombreCandidatures());
        return dto;
    }

    // ── Mapping DTO → Entity ──────────────────────────────────────────────
    private JobPosition toEntity(JobPositionDTO dto) {
        JobPosition entity = new JobPosition();
        entity.setEntrepriseId(dto.getEntrepriseId());
        entity.setTitre(dto.getTitre());
        entity.setDescription(dto.getDescription());
        entity.setTypeContrat(dto.getTypeContrat());
        entity.setSalaireMin(dto.getSalaireMin());
        entity.setSalaireMax(dto.getSalaireMax());
        entity.setLocalisation(dto.getLocalisation());
        entity.setRemote(dto.getRemote());
        entity.setExperienceRequiseAns(dto.getExperienceRequiseAns());
        entity.setSkillsRequis(dto.getSkillsRequis());
        entity.setDeadline(dto.getDeadline());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : JobStatus.DRAFT);
        return entity;
    }

    @Override
    public JobPositionDTO createJobPosition(JobPositionDTO dto) {
        JobPosition entity = toEntity(dto);
        return toDTO(jobPositionRepository.save(entity));
    }

    @Override
    public JobPositionDTO updateJobPosition(Long id, JobPositionDTO dto) {
        JobPosition existing = jobPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobPosition non trouvee : " + id));
        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setTypeContrat(dto.getTypeContrat());
        existing.setSalaireMin(dto.getSalaireMin());
        existing.setSalaireMax(dto.getSalaireMax());
        existing.setLocalisation(dto.getLocalisation());
        existing.setRemote(dto.getRemote());
        existing.setExperienceRequiseAns(dto.getExperienceRequiseAns());
        existing.setSkillsRequis(dto.getSkillsRequis());
        existing.setDeadline(dto.getDeadline());
        existing.setStatus(dto.getStatus());
        return toDTO(jobPositionRepository.save(existing));
    }

    @Override
    public void deleteJobPosition(Long id) {
        jobPositionRepository.deleteById(id);
    }

    @Override
    public JobPositionDTO getById(Long id) {
        return toDTO(jobPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobPosition non trouvee : " + id)));
    }

    @Override
    public List<JobPositionDTO> getAll() {
        return jobPositionRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<JobPositionDTO> getByEntreprise(Long entrepriseId) {
        return jobPositionRepository.findByEntrepriseId(entrepriseId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<JobPositionDTO> getPublished() {
        return jobPositionRepository.findByStatus(JobStatus.PUBLISHED)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
}