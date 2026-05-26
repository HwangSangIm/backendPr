package com.mapleinfo.register.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "characters")
@Getter
@Setter
@NoArgsConstructor

public class CharacterInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long id;

    @Column(name = "character_name" , nullable = false , length = 20)
    private String characterName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    public void setUser(User user){
        this.user = user;

        if (user != null && !user.getCharacterInfoList().contains(this)){
            user.getCharacterInfoList().add(this);
        }
    }
}
