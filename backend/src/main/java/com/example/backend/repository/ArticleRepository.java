package com.example.backend.repository;

import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>  {
    Page<Article> findAll(Pageable pageable);

    List<Article> findAllByAuthor(User author);

    Page<Article> findByTitleContaining(String searchQuery, Pageable pageable);

    @Query("SELECT a FROM Article a " +
            "WHERE a.author.id IN (SELECT us.channel.id FROM UserSubscription us WHERE us.subscriber.id = :userId)")
    Page<Article> findArticlesBySubscribedUser(@Param("userId") Long id, Pageable pageable);

    @Query("SELECT a FROM Article a " +
            "WHERE a.author.id IN (SELECT us.channel.id FROM UserSubscription us WHERE us.subscriber.id = :userId) " +
            "AND a.title LIKE %:title%")
    Page<Article> findArticlesBySubscribedUserAndTitleContaining(
            @Param("userId") Long id,
            @Param("title") String stringSearch,
            Pageable pageable
    );
}
