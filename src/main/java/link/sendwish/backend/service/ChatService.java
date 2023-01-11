package link.sendwish.backend.service;

import link.sendwish.backend.common.exception.*;
import link.sendwish.backend.dtos.ChatRoomResponseDto;
import link.sendwish.backend.entity.ChatRoom;
import link.sendwish.backend.entity.ChatRoomMember;
import link.sendwish.backend.entity.Member;
import link.sendwish.backend.repository.ChatRoomMemberRepository;
import link.sendwish.backend.repository.ChatRoomRepository;
import link.sendwish.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    public List<ChatRoomResponseDto> findRoomByMember(Member member) {
        List<ChatRoomResponseDto> rooms = chatRoomMemberRepository
                .findAllByMemberOrderByIdDesc(member)
                .orElseThrow(MemberChatRoomNotFoundException::new)
                .stream()
                .map(target -> ChatRoomResponseDto
                        .builder()
                        .chatRoomId(target.getChatRoom().getId())
                        .title(target.getChatRoom().getTitle())
                        .build()
                ).toList();
        log.info("해당 맴버의 [닉네임] : {}, 채팅방 일괄 조회 [채팅방 갯수] : {}", member.getNickname(), rooms.size());
        return rooms;
    }

    public ChatRoomResponseDto findRoomById(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        log.info("채팅방 단건 조회 [제목] : {}", chatRoom.getTitle());
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .title(chatRoom.getTitle())
                .build();
    }

    @Transactional
    public ChatRoomResponseDto createRoom(String title, String nickname){
        ChatRoom chatRoom = ChatRoom.builder()
                .title(title)
                .chatRoomMembers(new ArrayList<>())
                .build();

        Member member = memberRepository.findByNickname(nickname).orElseThrow(MemberNotFoundException::new);

        ChatRoomMember chatRoomMember = ChatRoomMember
                .builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();

        ChatRoom save = chatRoomRepository.save(chatRoom);

        member.addMemberChatRoom(chatRoomMember);
        chatRoom.addMemberChatRoom(chatRoomMember);

        assert chatRoom.getTitle().equals(save.getTitle());
        log.info("채팅방 생성 [제목] : {}", save.getTitle());
        return ChatRoomResponseDto.builder()
                .chatRoomId(save.getId())
                .title(save.getTitle())
                .build();
    }
}
