package com.example.aniwhere.domain.casting;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.voiceactor.VoiceActor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "casting")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Casting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long castingId;

    @ManyToOne(fetch = LAZY)
    @JsonBackReference
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "voice_actor_id", nullable = false)
    private VoiceActor voiceActor;

    @Column(name = "character_name", nullable = false)
    private String characterName;

    @Column(name = "character_description", columnDefinition = "TEXT")
    private String characterDescription;
}
