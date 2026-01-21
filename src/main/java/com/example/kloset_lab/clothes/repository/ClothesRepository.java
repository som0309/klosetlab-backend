package com.example.kloset_lab.clothes.repository;

import com.example.kloset_lab.clothes.entity.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothesRepository extends JpaRepository<Clothes, Long> {}
