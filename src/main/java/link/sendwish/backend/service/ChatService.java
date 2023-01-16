package link.sendwish.backend.service;

import link.sendwish.backend.common.exception.*;
import link.sendwish.backend.dtos.chat.ChatMessageResponseDto;
import link.sendwish.backend.dtos.chat.ChatRoomResponseDto;
import link.sendwish.backend.entity.*;
import link.sendwish.backend.repository.*;
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
    private final ChatRoomMessageRepository chatRoomMessageRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatRoomResponseDto> findRoomByMember(Member member) {
        List<ChatRoomResponseDto> dtos = chatRoomMemberRepository
                .findAllByMemberOrderByIdDesc(member)
                .orElseThrow(MemberChatRoomNotFoundException::new)
                .stream()
                .map(target -> ChatRoomResponseDto
                        .builder()
                        .chatRoomId(target.getChatRoom().getId())
                        .build()
                ).toList();
        log.info("해당 맴버의 [닉네임] : {}, 채팅방 일괄 조회 [채팅방 갯수] : {}", member.getNickname(), dtos.size());
        return dtos;
    }

    public ChatRoomResponseDto findRoomById(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        log.info("채팅방 단건 조회 [ID] : {}", chatRoom.getId());
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .build();
    }

    @Transactional
    public ChatRoomResponseDto createRoom(List<String> memberIdList, Long CollectionId){
        ChatRoom chatRoom = ChatRoom.builder()
                .collectionId(CollectionId)
                .chatRoomMembers(new ArrayList<>())
                .build();

        ChatRoom save = chatRoomRepository.save(chatRoom);

        // [todo] memberList가 해당 collection에 속한 member인지 확인
        List<ChatRoomMember> chatRoomMembers = memberIdList.stream().map(nickname -> {
            Member member = memberRepository.findByNickname(nickname).orElseThrow(MemberNotFoundException::new);
            ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                    .chatRoom(chatRoom)
                    .member(member)
                    .build();
            chatRoom.addMemberChatRoom(chatRoomMember);
            member.addMemberChatRoom(chatRoomMember);
            return chatRoomMember;
        }).toList();

        assert chatRoom.getCollectionId().equals(chatRoomMembers.get(0).getChatRoom().getCollectionId());
        log.info("채팅방 생성 [ID] : {}", chatRoom.getId());
        return ChatRoomResponseDto.builder()
                .chatRoomId(save.getId())
                .build();
    }

    // 해당 채팅방의 모든 채팅목록 불러오기
    public List<ChatRoomMessage> findAllChatByRoomId(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(MemberChatRoomNotFoundException::new);
        // 해당 채팅방의 모든 채팅목록 불러와서
        List<ChatRoomMessage> chatList = chatRoomMessageRepository.findAllByChatRoom(chatRoom);
        log.info("해당 방 [ID] : {}, 채팅내역 일괄 조회 [채팅 메세지 갯수] : {}", chatRoom.getId(), chatList.size());
        return chatList;
    }

    @Transactional
    public ChatMessageResponseDto saveChatMessage(ChatMessage message) {
        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(MemberChatRoomNotFoundException::new);

        ChatMessage chatMessage = ChatMessage.builder()
                .message(message.getMessage())
                .roomId(message.getRoomId())
                .sender(message.getSender())
                .type(message.getType())
                .build();
        ChatRoomMessage chatRoomMessage = ChatRoomMessage.builder()
                .chatRoom(chatRoom)
                .chatMessage(chatMessage)
                .build();

        ChatMessage save = chatMessageRepository.save(chatMessage);

        chatRoom.addMessageChatRoom(chatRoomMessage);

        assert message.getMessage().equals(save.getMessage());
        log.info("메세지 저장 [내용] : {}", save.getMessage());
        log.info("메세지 저장 [일시] : {}", save.getCreateAt());
        return ChatMessageResponseDto.builder()
                .chatRoomId(save.getRoomId())
                .message(save.getMessage())
                .sender(save.getSender())
                .createAt(save.getCreateAt())
                .build();
    }

    public Long getRoomId(Long collectionId){
        ChatRoom chatRoom = chatRoomRepository.findByCollectionId(collectionId).orElseThrow(ChatRoomNotFoundException::new);
        return chatRoom.getId();
    }
}
