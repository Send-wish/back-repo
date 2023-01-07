package link.sendwish.backend.service;

import link.sendwish.backend.auth.JwtTokenProvider;
import link.sendwish.backend.auth.TokenInfo;
import link.sendwish.backend.common.exception.MemberExisitingIDException;
import link.sendwish.backend.common.exception.MemberNotFoundException;
import link.sendwish.backend.common.exception.PasswordNotSameException;
import link.sendwish.backend.dtos.MemberFriendAddRequestDto;
import link.sendwish.backend.dtos.MemberRequestDto;
import link.sendwish.backend.entity.Member;
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
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member createMember(MemberRequestDto dto) {
        String encode = passwordEncoder.encode(dto.getPassword());
//         멤버 아이디 중복여부 체크
//         (false이면 만들려는 ID가 repository에 있음)
        if (checkExistingID(dto.getNickname()) == false){
            throw new MemberExisitingIDException();
        }

        Member member = Member.builder()
                .nickname(dto.getNickname())
                .password(encode)
                .roles(List.of("USER"))
                .memberCollections(new ArrayList<>())
                .friends(new ArrayList<>())
                .build();
        Member savedMember = memberRepository.save(member);
        log.info("새로운 회원가입 [ID] : {}, [PW] : {}", savedMember.getNickname(), savedMember.getPassword());
        return savedMember;
    }

    @Transactional
    public TokenInfo login(String nickname, String password) {
        // 멤버 아이디가 있는지, 패스워드가 제대로 되었는지
        Member member = memberRepository.findBynickname(nickname).orElseThrow(MemberNotFoundException::new);
        if (passwordEncoder.matches(password, member.getPassword())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(nickname, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            return jwtTokenProvider.generateToken(authentication);
        }
        throw new PasswordNotSameException();
    }

    public Member findMember(String nickname) {
        return memberRepository.findBynickname(nickname).orElseThrow(MemberNotFoundException::new);
    }

    /**
     * 아이디가 존재하는지 확인
     */
    public boolean checkExistingID(String nickname ){
        if (!memberRepository.findBynickname(nickname).isEmpty()) {
            return false;
        }
        return true;
    }

    @Transactional
    public Member addFriendToMe(MemberFriendAddRequestDto dto){
        Long myId = dto.getMemberId();
        Long friendId = dto.getAddMemberId();

        Member myMember = memberRepository.findById(myId).orElseThrow(MemberNotFoundException::new);
        Member friendMember = memberRepository.findById(friendId).orElseThrow(MemberNotFoundException::new);

        myMember.addFriendInList(friendMember);
        return friendMember;
    }

}

