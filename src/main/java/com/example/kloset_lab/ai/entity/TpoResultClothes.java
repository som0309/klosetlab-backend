package com.example.kloset_lab.ai.entity;

import com.example.kloset_lab.global.entity.BaseTimeEntity;
import com.example.kloset_lab.media.entity.MediaFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tpo_result_clothes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TpoResultClothes extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpo_result_id", nullable = false)
    private TpoResult tpoResult;

    @Column(name = "vector_db_id", nullable = false, columnDefinition = "varchar(50)")
    private String vectorDbId;

    @Builder
    public TpoResultClothes(TpoResult tpoResult, MediaFile file, String vectorDbId) {
        this.tpoResult = tpoResult;
        this.vectorDbId = vectorDbId;
    }
}
