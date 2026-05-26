package com.mapleinfo.register.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor

public class User {

    @Id
    @Column(name = "user_id" , length = 50)
    private String id;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<CharacterInfo> characterInfoList = new ArrayList<>();

    public void addCharacter(CharacterInfo character){
        if (this.characterInfoList.size() >= 5){
            throw new IllegalArgumentException("캐릭터는 최대 5개까지만 등록할 수 있습니다.");
        }
        this.characterInfoList.add(character);
        if (character.getUser() != this) {
            character.setUser(this);
        }
    }
}
