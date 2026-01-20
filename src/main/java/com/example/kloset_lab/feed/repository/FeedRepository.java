package com.example.kloset_lab.feed.repository;

import com.example.kloset_lab.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {}
