package com.example.backend.model.entity.user;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_subscriptions")
@Data
public class UserSubscription {
    @EmbeddedId
    private UserSubscriptionId id;

    @MapsId("channelId")
    @ManyToOne
    private User channel;

    @MapsId("subscriberId")
    @ManyToOne
    private User subscriber;

    private boolean active;

    public UserSubscription() {
    }

    public UserSubscription(User channel, User subscriber) {
        this.channel = channel;
        this.subscriber = subscriber;
        this.id = new UserSubscriptionId(channel.getId(), subscriber.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSubscription that)) return false;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
