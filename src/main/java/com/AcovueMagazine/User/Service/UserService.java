package com.AcovueMagazine.User.Service;

import com.AcovueMagazine.User.Aggregate.UserEntity;
import com.AcovueMagazine.User.Dto.CreateUserRequest;
import com.AcovueMagazine.User.Repository.UserRepository;
import com.AcovueMagazine.User.Security.Dto.UserInfoResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional
    public void createUser(CreateUserRequest newUser) {
        UserEntity user = modelMapper.map(newUser, UserEntity.class);
        user.encryptPassword(passwordEncoder.encode(newUser.getPwd()));
        userRepository.save(user);
    }

    // 로그인 할때 authenticationManager를 통해 호출 될 메소드
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 인증 토큰에 담긴 userId가 메소드로 넘어오므로 해당 값을 기준으로 DB에서 조회
        UserEntity loginUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(loginUser.getUserRole().name()));

        return new User(loginUser.getUserId(), loginUser.getPwd(), grantedAuthorities);
    }

    public UserInfoResponse getUserInfoById(Long id) {

        UserEntity user = userRepository.findById(id).orElseThrow();
        return modelMapper.map(user, UserInfoResponse.class);
    }
}
