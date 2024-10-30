package com.example.aniwhere.domain.voiceactor;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@Table(name = "voiceactors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceActor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voice_actor_id")
    private Long voiceActorId;

    @Column(name = "name", length = 255, nullable = false)
    private String name;
}
