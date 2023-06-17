package com.example.backend.model.entity.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class UserSubscriptionId implements Serializable {
    @Column(name = "channel_id")
    private Long channelId;
    @Column(name = "subscriber_id")
    private Long subscriberId;

    public UserSubscriptionId() {
    }

    public UserSubscriptionId(Long channelId, Long subscriberId) {
        this.channelId = channelId;
        this.subscriberId = subscriberId;
    }
}
