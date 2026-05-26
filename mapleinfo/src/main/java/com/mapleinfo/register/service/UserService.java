package com.mapleinfo.register.service;

import com.mapleinfo.register.dto.LoginRequestDTO;
import com.mapleinfo.register.dto.UserRequestDTO;
import com.mapleinfo.register.entity.CharacterInfo;
import com.mapleinfo.register.entity.User;
import com.mapleinfo.register.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void singUp(UserRequestDTO requestDTO){
        if (userRepository.existsById(requestDTO.getId())){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (requestDTO.getNickname() == null || requestDTO.getNickname().isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 캐릭터를 등록해야 합니다.");
        }
        if (requestDTO.getNickname().size() > 5) {
            throw new IllegalArgumentException("캐릭터는 최대 5개까지만 등록할 수 있습니다.");
        }

        User user = new User();
        user.setId(requestDTO.getId());
        user.setPassword(requestDTO.getPassword());

        for (String nickname : requestDTO.getNickname()){
            if (nickname != null && !nickname.isEmpty()){
                CharacterInfo character = new CharacterInfo();
                character.setCharacterName(nickname);
                user.addCharacter(character);
            }
        }

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(LoginRequestDTO requestDTO){
        User user = userRepository.findById(requestDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (!user.getPassword().equals(requestDTO.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
