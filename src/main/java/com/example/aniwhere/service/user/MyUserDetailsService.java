package com.example.aniwhere.service.user;

import com.example.aniwhere.application.config.security.MyUserDetails;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * 이메일을 기반으로 인증 객체를 조회
	 * @param email
	 * @return UserDetails
	 * @throws UsernameNotFoundException
	 */
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).
				orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않는 유저입니다."));
		return new MyUserDetails(user);
	}
}
