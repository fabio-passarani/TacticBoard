package com.tacticboard.core.service;

import com.tacticboard.core.model.entity.tactic.Animation;
import com.tacticboard.core.model.entity.tactic.Tactic;

import java.util.List;
import java.util.Optional;

public interface TacticService {
    
    Tactic createTactic(Tactic tactic);
    
    Tactic updateTactic(Tactic tactic);
    
    Optional<Tactic> getTacticById(Long id);
    
    List<Tactic> getAllTacticsByTeamId(Long teamId);
    
    List<Tactic> getPublicTactics();
    
    void deleteTactic(Long id);
    
    Animation createAnimation(Animation animation, Long tacticId);
    
    Animation updateAnimation(Animation animation);
    
    Optional<Animation> getAnimationById(Long id);
    
    List<Animation> getAllAnimationsByTacticId(Long tacticId);
    
    void deleteAnimation(Long id);
}