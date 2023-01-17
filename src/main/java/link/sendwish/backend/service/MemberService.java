package link.sendwish.backend.service;

import link.sendwish.backend.auth.JwtTokenProvider;
import link.sendwish.backend.auth.TokenInfo;
import link.sendwish.backend.common.exception.*;
import link.sendwish.backend.dtos.friend.FriendAddRequestDto;
import link.sendwish.backend.dtos.friend.FriendAddResponseDto;
import link.sendwish.backend.dtos.friend.FriendDeleteResponseDto;
import link.sendwish.backend.dtos.friend.FriendResponseDto;
import link.sendwish.backend.dtos.member.MemberRequestDto;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.entity.MemberFriend;
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
                .img(dto.getImg())
                .roles(List.of("USER"))
                .memberCollections(new ArrayList<>())
                .memberItems(new ArrayList<>())
                .friends(new ArrayList<>())
                .chatRoomMembers(new ArrayList<>())
                .build();
        Member savedMember = memberRepository.save(member);
        log.info("새로운 회원가입 [ID] : {}, [PW] : {}, [IMG] : {}", savedMember.getNickname(), savedMember.getPassword(), savedMember.getImg());
        return savedMember;
    }

    @Transactional
    public TokenInfo login(String nickname, String password) {
        // 멤버 아이디가 있는지, 패스워드가 제대로 되었는지
        Member member = memberRepository.findByNickname(nickname).orElseThrow(MemberNotFoundException::new);
        if (passwordEncoder.matches(password, member.getPassword())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(nickname, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            return jwtTokenProvider.generateToken(authentication);
        }
        throw new PasswordNotSameException();
    }

    public Member findMember(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(MemberNotFoundException::new);
    }

    /**
     * 아이디가 존재하는지 확인
     */
    public boolean checkExistingID(String nickname ){
        if (!memberRepository.findByNickname(nickname).isEmpty()) {
            return false;
        }
        return true;
    }

    @Transactional
    public FriendAddResponseDto addFriendToMe(FriendAddRequestDto dto){
        String myNickname = dto.getMemberNickname();
        String friendNickname = dto.getAddMemberNickname();

        Member myMember = memberRepository.findByNickname(myNickname).orElseThrow(MemberNotFoundException::new);
        Member friendMember = memberRepository.findByNickname(friendNickname).orElseThrow(MemberNotFoundException::new);

        assert (myNickname == myMember.getNickname());
        assert(friendNickname == friendMember.getNickname());

        if (myMember.getNickname() == friendMember.getNickname()){
            throw new FriendMemberSameException();
        }

        for (MemberFriend f : myMember.getFriends()){
            if (f.getFriendId().equals(friendMember.getId())){
                throw new MemberFriendExistingException();
            } else {
                log.info("나의 nickname : {}, 친구의 nickname : {}", myNickname, friendNickname);
            }
        }

        MemberFriend friend = MemberFriend.builder()
                .friendId(friendMember.getId())
                .build();

        myMember.addFriendInList(friend);

        return FriendAddResponseDto.builder()
                .myNickname(myMember.getNickname())
                .friendNickname(friendMember.getNickname())
                .build();
    }

    public List<FriendResponseDto> findFriendsByMember(String nickname){
        Member member = memberRepository.findByNickname(nickname)
                                        .orElseThrow(MemberNotFoundException::new);

        List<FriendResponseDto> dtos = new ArrayList<>();
        for (MemberFriend f : member.getFriends()){
            Member friendMember = memberRepository.findById(f.getFriendId())
                    .orElseThrow(MemberNotFoundException::new);
            dtos.add(FriendResponseDto.builder()
                    .friend_id(friendMember.getId())
                    .friend_nickname(friendMember.getNickname())
                    .friend_img(friendMember.getImg())
                    .build());
            log.info("친구 NickName : {}", friendMember.getNickname());
        }
        return dtos;
    }

    @Transactional
    public FriendDeleteResponseDto deleteFriend(String nickname, String friendNickname){
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(MemberNotFoundException::new);
        Member friendMember = memberRepository.findByNickname(friendNickname)
                .orElseThrow(MemberNotFoundException::new);

        log.info("친구 삭제 전 - member : {}", member.getFriends());

        for (MemberFriend f : member.getFriends()){
            if (f.getFriendId().equals(friendMember.getId())){
                member.removeFriendInList(f);
                log.info("친구 삭제 후 - member : {}", member.getFriends());
                return FriendDeleteResponseDto.builder()
                        .nickname(member.getNickname())
                        .friendNickname(friendMember.getNickname())
                        .build();
            }
        }
        throw new MemberFriendNotFoundException();
    }
}

