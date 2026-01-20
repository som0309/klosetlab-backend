package com.example.kloset_lab.comment.repository;

import com.example.kloset_lab.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {}
