package link.sendwish.backend.service;

import link.sendwish.backend.auth.JwtTokenProvider;
import link.sendwish.backend.auth.TokenInfo;
import link.sendwish.backend.dtos.CollectionResponseDto;
import link.sendwish.backend.dtos.MemberRequestDto;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.entity.MemberCollection;
import link.sendwish.backend.repository.MemberCollectionRepository;
import link.sendwish.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberCollectionRepository memberCollectionRepository;

    @Transactional
    public Member createMember(MemberRequestDto dto) {
        String encode = passwordEncoder.encode(dto.getPassword());
        Member member = Member.builder()
                .memberId(dto.getMemberId())
                .password(encode)
                .roles(List.of("USER"))
                .build();
        Member savedMember = memberRepository.save(member);
        log.info("새로운 회원가입 [ID] : {}, [PW] : {}", savedMember.getMemberId(), savedMember.getPassword());
        return savedMember;
    }

    @Transactional
    public TokenInfo login(String memberId, String password) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(RuntimeException::new);
        if (passwordEncoder.matches(password, member.getPassword())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            return jwtTokenProvider.generateToken(authentication);
        }
        throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }

    public Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId).orElseThrow(RuntimeException::new);
    }

    public List<CollectionResponseDto> findMemberCollection(Member member) {
        return memberCollectionRepository
                .findAllByMember(member)
                .orElseThrow(RuntimeException::new)
                .stream()
                .map(target -> CollectionResponseDto
                        .builder()
                        .title(target.getCollection().getTitle())
                        .memberId(target.getMember().getUsername())
                        .build()
                ).toList();
    }
}
