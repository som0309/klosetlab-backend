package com.example.kloset_lab.comment.repository;

import com.example.kloset_lab.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}
