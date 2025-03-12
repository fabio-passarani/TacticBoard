package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.tactic.Tactic;
import com.tacticboard.core.model.entity.tactic.TacticElement;
import com.tacticboard.core.model.entity.tactic.TacticVersion;
import com.tacticboard.core.service.TacticService;
import com.tacticboard.persistence.repository.TacticElementRepository;
import com.tacticboard.persistence.repository.TacticRepository;
import com.tacticboard.persistence.repository.TacticVersionRepository;
import com.tacticboard.persistence.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TacticServiceImpl implements TacticService {

    private final TacticRepository tacticRepository;
    private final TacticVersionRepository tacticVersionRepository;
    private final TacticElementRepository tacticElementRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public TacticServiceImpl(
            TacticRepository tacticRepository,
            TacticVersionRepository tacticVersionRepository,
            TacticElementRepository tacticElementRepository,
            TeamRepository teamRepository) {
        this.tacticRepository = tacticRepository;
        this.tacticVersionRepository = tacticVersionRepository;
        this.tacticElementRepository = tacticElementRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public Tactic createTactic(Tactic tactic) {
        if (!teamRepository.existsById(tactic.getTeam().getId())) {
            throw new ResourceNotFoundException("Team", "id", tactic.getTeam().getId());
        }
        
        tactic.setCreatedAt(LocalDateTime.now());
        tactic.setUpdatedAt(LocalDateTime.now());
        
        return tacticRepository.save(tactic);
    }

    @Override
    public Tactic updateTactic(Tactic tactic) {
        if (!tacticRepository.existsById(tactic.getId())) {
            throw new ResourceNotFoundException("Tactic", "id", tactic.getId());
        }
        
        tactic.setUpdatedAt(LocalDateTime.now());
        return tacticRepository.save(tactic);
    }

    @Override
    public Optional<Tactic> getTacticById(Long id) {
        return tacticRepository.findById(id);
    }

    @Override
    public List<Tactic> getTacticsByTeamId(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }
        
        return tacticRepository.findByTeamId(teamId);
    }

    @Override
    public void deleteTactic(Long id) {
        if (!tacticRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tactic", "id", id);
        }
        
        // Delete all versions and elements first
        List<TacticVersion> versions = tacticVersionRepository.findByTacticId(id);
        for (TacticVersion version : versions) {
            tacticElementRepository.deleteByTacticVersionId(version.getId());
        }
        
        tacticVersionRepository.deleteByTacticId(id);
        tacticRepository.deleteById(id);
    }

    @Override
    public TacticVersion createTacticVersion(TacticVersion version, Long tacticId) {
        Tactic tactic = tacticRepository.findById(tacticId)
                .orElseThrow(() -> new ResourceNotFoundException("Tactic", "id", tacticId));
        
        version.setTactic(tactic);
        version.setCreatedAt(LocalDateTime.now());
        
        // Update the tactic's updated timestamp
        tactic.setUpdatedAt(LocalDateTime.now());
        tacticRepository.save(tactic);
        
        return tacticVersionRepository.save(version);
    }

    @Override
    public Optional<TacticVersion> getTacticVersionById(Long id) {
        return tacticVersionRepository.findById(id);
    }

    @Override
    public List<TacticVersion> getTacticVersionsByTacticId(Long tacticId) {
        if (!tacticRepository.existsById(tacticId)) {
            throw new ResourceNotFoundException("Tactic", "id", tacticId);
        }
        
        return tacticVersionRepository.findByTacticId(tacticId);
    }

    @Override
    public void deleteTacticVersion(Long id) {
        if (!tacticVersionRepository.existsById(id)) {
            throw new ResourceNotFoundException("TacticVersion", "id", id);
        }
        
        // Delete all elements first
        tacticElementRepository.deleteByTacticVersionId(id);
        tacticVersionRepository.deleteById(id);
    }

    @Override
    public TacticElement createTacticElement(TacticElement element, Long versionId) {
        TacticVersion version = tacticVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("TacticVersion", "id", versionId));
        
        element.setTacticVersion(version);
        return tacticElementRepository.save(element);
    }

    @Override
    public TacticElement updateTacticElement(TacticElement element) {
        if (!tacticElementRepository.existsById(element.getId())) {
            throw new ResourceNotFoundException("TacticElement", "id", element.getId());
        }
        
        return tacticElementRepository.save(element);
    }

    @Override
    public Optional<TacticElement> getTacticElementById(Long id) {
        return tacticElementRepository.findById(id);
    }

    @Override
    public List<TacticElement> getTacticElementsByVersionId(Long versionId) {
        if (!tacticVersionRepository.existsById(versionId)) {
            throw new ResourceNotFoundException("TacticVersion", "id", versionId);
        }
        
        return tacticElementRepository.findByTacticVersionId(versionId);
    }

    @Override
    public void deleteTacticElement(Long id) {
        if (!tacticElementRepository.existsById(id)) {
            throw new ResourceNotFoundException("TacticElement", "id", id);
        }
        
        tacticElementRepository.deleteById(id);
    }

    @Override
    public List<Tactic> searchTactics(String query, Long teamId) {
        if (teamId != null) {
            if (!teamRepository.existsById(teamId)) {
                throw new ResourceNotFoundException("Team", "id", teamId);
            }
            return tacticRepository.findByNameContainingAndTeamId(query, teamId);
        } else {
            return tacticRepository.findByNameContaining(query);
        }
    }

    @Override
    public TacticVersion duplicateTacticVersion(Long versionId, String newName) {
        TacticVersion originalVersion = tacticVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("TacticVersion", "id", versionId));
        
        // Create a new version
        TacticVersion newVersion = new TacticVersion();
        newVersion.setName(newName);
        newVersion.setDescription(originalVersion.getDescription());
        newVersion.setTactic(originalVersion.getTactic());
        newVersion.setCreatedAt(LocalDateTime.now());
        
        TacticVersion savedVersion = tacticVersionRepository.save(newVersion);
        
        // Copy all elements
        List<TacticElement> elements = tacticElementRepository.findByTacticVersionId(versionId);
        for (TacticElement element : elements) {
            TacticElement newElement = new TacticElement();
            newElement.setType(element.getType());
            newElement.setPositionX(element.getPositionX());
            newElement.setPositionY(element.getPositionY());
            newElement.setProperties(element.getProperties());
            newElement.setTacticVersion(savedVersion);
            
            tacticElementRepository.save(newElement);
        }
        
        return savedVersion;
    }
}