package link.sendwish.backend.service;

import link.sendwish.backend.common.exception.*;
import link.sendwish.backend.dtos.chat.*;
import link.sendwish.backend.dtos.collection.CollectionResponseDto;
import link.sendwish.backend.dtos.friend.FriendResponseDto;
import link.sendwish.backend.dtos.item.ItemResponseDto;
import link.sendwish.backend.entity.*;
import link.sendwish.backend.entity.Collection;
import link.sendwish.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {
    private final CollectionRepository collectionRepository;
    private final ItemRepository itemRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatRoomResponseDto> findRoomByMember(Member member) {
        List<ChatRoomResponseDto> dtos = chatRoomMemberRepository
                .findAllByMemberOrderByIdDesc(member)
                .orElseThrow(MemberChatRoomNotFoundException::new)
                .stream()
                .map(target -> ChatRoomResponseDto
                        .builder()
                        .chatRoomId(target.getChatRoom().getId())
                        .lastMessage(setLastChatMessageResponseDto(target.getChatRoom()))
                        .collection(setCollectionResponseDtoById(target.getChatRoom().getCollectionId(), member.getNickname()))
                        .friends(findFriendsByChat(target.getChatRoom()))
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
                .chatVoteMembers(new ArrayList<>())
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

    @Transactional
    public ChatMessageResponseDto saveChatMessage(ChatMessageRequestDto message) {
        log.info("채팅 메시지 저장 [내용] : {}", message.getMessage());
        log.info("메세지 [사용자] : {}", message.getSender());
        log.info("메세지 [TYPE] : {}", message.getType());
        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(MemberChatRoomNotFoundException::new);

        ChatMessage chatMessage = ChatMessage.builder()
                .message(message.getMessage())
                .chatRoom(chatRoom)
                .sender(message.getSender())
                .type(message.getType())
                .itemId(message.getItemId())
                .build();

        ChatMessage save = chatMessageRepository.save(chatMessage);

        chatRoom.addChatMessage(chatMessage);
        assert message.getMessage().equals(save.getMessage());
        log.info("메세지 저장 [내용] : {}", save.getMessage());
        log.info("메세지 저장 [일시] : {}", save.getCreateAt());

        String memberImg = memberRepository.findByNickname(message.getSender()).get().getImg();

        ChatMessageResponseDto responseDto = ChatMessageResponseDto.builder()
                .chatRoomId(save.getChatRoom().getId())
                .message(save.getMessage())
                .sender(save.getSender())
                .createAt(save.getCreateAt().toString())
                .senderImg(memberImg)
                .build();

        /* TALK인 경우 */
        if (save.getItemId() == null) {
            log.info("TALK 메세지 저장 [내용] : {}", save.getMessage());
            responseDto.setItemDto(null);
            return responseDto;
        }

        Item item = itemRepository.findById(save.getItemId()).orElseThrow(ItemNotFoundException::new);
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .itemId(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .imgUrl(item.getImgUrl())
                .originUrl(item.getOriginUrl())
                .build();

        responseDto.setItemDto(itemResponseDto);
        log.info("ITEM 메세지 저장 [내용] : {}", save.getMessage());

        return responseDto;
    }

    public Long getRoomId(Long collectionId){
        ChatRoom chatRoom = chatRoomRepository.findByCollectionId(collectionId).orElseThrow(ChatRoomNotFoundException::new);
        return chatRoom.getId();
    }

    public List<ChatAllMessageResponseDto> getChatsByRoom(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        if (chatMessageRepository.findAllByChatRoom(chatRoom).isEmpty()) {
            throw new ChatMessageNotFoundException();
        }
        List<ChatAllMessageResponseDto> chats = chatMessageRepository.findAllByChatRoom(chatRoom).get().stream()
                .map(target -> ChatAllMessageResponseDto.builder()
                        .message(target.getMessage())
                        .sender(target.getSender())
                        .senderImg(memberRepository.findByNickname(target.getSender()).get().getImg())
                        .chatRoomId(target.getChatRoom().getId())
                        .createAt(target.getCreateAt().toString())
                        .type(target.getType())
                        .itemDto(target.getItemId() == null ? null :
                                setItemResponseDtoByItemId(target.getItemId()))
                        .build()).collect(Collectors.toList());
        log.info("채팅방 [ID] : {}, 채팅 메시지 일괄 조회 [메시지 갯수] : {}", chatRoom.getId(), chats.size());
        return chats;
    }

    public Long getChatRoomIdByCollectionId(Long collectionId){
        ChatRoom chatRoom = chatRoomRepository.findByCollectionId(collectionId).orElseThrow(ChatRoomNotFoundException::new);
        return chatRoom.getId();
    }

    public ItemResponseDto setItemResponseDtoByItemId(Long ItemId){
        return ItemResponseDto.builder()
                .itemId(itemRepository.findById(ItemId).orElseThrow(ItemNotFoundException::new).getId())
                .name(itemRepository.findById(ItemId).get().getName())
                .price(itemRepository.findById(ItemId).get().getPrice())
                .imgUrl(itemRepository.findById(ItemId).get().getImgUrl())
                .originUrl(itemRepository.findById(ItemId).get().getOriginUrl())
                .build();
    }

    public ChatMessageLastResponseDto setLastChatMessageResponseDto(ChatRoom chatRoom){
        ChatMessage message = chatMessageRepository.findTopByChatRoomOrderByIdDesc(chatRoom).isPresent() ?
                chatMessageRepository.findTopByChatRoomOrderByIdDesc(chatRoom).get() : null;
        if (message == null) {
            return null;
        }
        return  ChatMessageLastResponseDto.builder()
                .message(message.getMessage())
                .sender(message.getSender())
                .createAt(message.getCreateAt().toString())
                .build();
    }

    public CollectionResponseDto setCollectionResponseDtoById(Long collectionId, String nickname){
        Collection find = collectionRepository.findById(collectionId).get();
        return CollectionResponseDto.builder()
                .collectionId(find.getId())
                .title(find.getTitle())
                .nickname(nickname)
                .defaultImage(find.getDefaultImgURL())
                .build();
    }

    public List<FriendResponseDto> findFriendsByChat(ChatRoom chatRoom){
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findMemberByChatRoom(chatRoom).get();

        List<FriendResponseDto> dtos = new ArrayList<>();
        chatRoomMembers.forEach(target -> {
            Member friendMember = memberRepository.findById(target.getMember().getId()).orElseThrow(MemberNotFoundException::new);
            dtos.add(FriendResponseDto.builder()
                    .friend_id(friendMember.getId())
                    .friend_nickname(friendMember.getNickname())
                    .friend_img(friendMember.getImg())
                    .build());
        });

        log.info("채팅방 맴버 조회 [ID] : {}, 채팅방 멤버 조회 [멤버 수] : {}", chatRoom.getId(), dtos.size());
        return dtos;
    }

}
