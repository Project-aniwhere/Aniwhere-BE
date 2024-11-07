package com.example.aniwhere.application.voiceactor.repository;

import com.example.aniwhere.domain.voiceactor.VoiceActor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoiceactorRepository extends JpaRepository<VoiceActor, Long> {
}
