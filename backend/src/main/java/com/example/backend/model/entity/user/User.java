package com.example.backend.model.entity.user;

import com.example.backend.model.entity.article.Article;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="user_picture")
    private String userPicture;

    @Column(name="locale")
    private String locale;

    @Enumerated(EnumType.STRING)
    @Column(name="provider")
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "active")
    private boolean active;

    @Column(name = "activation_code")
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "subscriber", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<UserSubscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy = "channel", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<UserSubscription> subscribers = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Article> articles;

    public User() {}

    public User(
            String email,
            String password,
            String name,
            String userPicture,
            String locale,
            Provider provider,
            Status status,
            Set<Role> roles
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userPicture = userPicture;
        this.locale = locale;
        this.provider = provider;
        this.status = status;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return getId() != null ? getId().equals(user.getId()) : user.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
