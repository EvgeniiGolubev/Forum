package com.example.backend.repository;

import com.example.backend.model.entity.user.User;
import com.example.backend.model.entity.user.UserSubscription;
import com.example.backend.model.entity.user.UserSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UserSubscriptionId> {
    List<UserSubscription> findBySubscriber(User user);
    List<UserSubscription> findByChannel(User channel);
    UserSubscription findByChannelAndSubscriber(User channel, User subscriber);
}
