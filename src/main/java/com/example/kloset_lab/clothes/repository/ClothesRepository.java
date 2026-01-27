package com.example.kloset_lab.clothes.repository;

import com.example.kloset_lab.clothes.entity.Clothes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClothesRepository extends JpaRepository<Clothes, Long> {
    @Query(
            """
        SELECT c
        FROM Clothes c
        JOIN FETCH c.user u
        WHERE (u.id = :userId)
          AND (:cursor IS NULL OR c.id < :cursor)
        ORDER BY c.id DESC
    """)
    Slice<Clothes> findByCursor(@Param("userId") Long userId, @Param("cursor") Long cursor, Pageable pageable);
}
