package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.PhotonMemberInOutDto;
import soma.test.waggle.dto.WorldRoomCreateRequestDto;
import soma.test.waggle.dto.WorldRoomResponseDto;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonResponseDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.*;
import soma.test.waggle.error.ErrorCode;
import soma.test.waggle.error.exception.PeopleOverLimitException;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorldRoomService {

    private final WorldRoomRepository worldRoomRepository;
    private final EntranceRoomRepository entranceRoomRepository;
    private final MemberRepository memberRepository;
    private final ConversationRepositoty conversationRepositoty;
    private final SentenceRepository sentenceRepository;
    private final ConversationService conversationService;

    private static final int PEOPLE_LIMIT = 20;

    @Transactional(readOnly = true)
    public WorldRoomResponseDto getWorldRoomInfo(Long worldRoomId) {
        return WorldRoomResponseDto.of(worldRoomRepository.find(worldRoomId));
    }

    /**
     * OnStatusType 이 Y인 worldRoom
     * */
    @Transactional(readOnly = true)
    public List<WorldRoomResponseDto> openWorldRoomList(){
        return worldRoomRepository.findAllByCriteria(OnStatusType.Y).stream()
                .map(WorldRoomResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * worldRoomCreateRequestDto 를 바탕으로 WorldRoom 생성 및 저장
     * */
    public WorldRoomCreateRequestDto createWorldRoom(WorldRoomCreateRequestDto worldRoomCreateRequestDto) {
        return WorldRoomCreateRequestDto.of(worldRoomRepository.save(worldRoomCreateRequestDto.toWorldRoom()));
    }

    /**
     * Photon webhook 으로 worldRoom 의 open 과 close 변경
     * close 방식에 대해서 Unity 와 논의 후 후속 처리 추가 필요
     * */
    public PhotonResponseDto pathCreateOrClose(PhotonRoomIdDto photonRoomIdDto, OnStatusType onStatusType) {
        Optional<WorldRoom> findRoom = worldRoomRepository.findById(photonRoomIdDto.getRoomId());
        if (findRoom.isEmpty()) {
            return new PhotonResponseDto(1, "No Room");
        } else {
            WorldRoom worldRoom = findRoom.get();
            /*
            * close 방식에 대해서 Unity 와 논의 후 후속 처리 추가 필요
            * */
            worldRoom.setOnStatus(onStatusType);
            return new PhotonResponseDto(0, "OK");
        }
    }

    /**
     * 멤버가 worldRoom 에 입장했을 때의 webhook
     * 1. entranceRoom 생성
     * 2. worldRoom 사람 수 +1
     * 3. 멤버 OnState => Y
     * 4. cache 대화 관리 시작
     * */
    public PhotonResponseDto pathJoin(PhotonMemberDto photonMemberDto) {

        // 멤버와 worldRoom 을 가져온다
        Member member = memberRepository.find(photonMemberDto.getMemberId());
        WorldRoom worldRoom = worldRoomRepository.find(photonMemberDto.getRoomId());

        // worldRoom 인원 제한 초과시 exception
        if (worldRoom.getPeople() >= PEOPLE_LIMIT){
            throw new PeopleOverLimitException(ErrorCode.THE_NUMBER_OF_PEOPLE_OVER_LIMIT);
        }

        // entranceRoom 생성 및 저장
        EntranceRoom entranceRoom = EntranceRoom
                .builder()
                .worldRoom(worldRoom)
                .member(member)
                .isLast(OnStatusType.Y)
                .joinTime(LocalDateTime.now())
                .build();
        entranceRoomRepository.save(entranceRoom);

        // worldRoom 사람 수 +1
        worldRoom.setPeople(worldRoom.getPeople()+1);
        // 멤버 입장 중이라고 변경
        member.setEntranceStatus(OnStatusType.Y);

        // member 가 방에 입장하였으므로 대화 관리(내 대화를 듣고 있는 사람, 토픽 추출을 위한 대화 set 모음) 시작
        conversationService.joinRoom(photonMemberDto.getMemberId());

        return new PhotonResponseDto(0, "OK");
    }

    /**
     * 멤버가 worldRoom 에서 퇴장했을 때의 webhook
     * 1. entranceRoom isLast = N
     * 2. entranceRoom leaveTime = now()
     * 3. worldRoom 사람 수 -1
     * 4. 멤버 OnState => N
     * 5. 대화 시간 저장
     * 6. cache 대화 관리 제거
     * */
    public PhotonResponseDto pathLeave(PhotonMemberDto photonMemberDto) {

        Member member = memberRepository.find(photonMemberDto.getMemberId());
        WorldRoom worldRoom = worldRoomRepository.find(photonMemberDto.getRoomId());
        EntranceRoom entranceRoom = entranceRoomRepository.findByMemberId(photonMemberDto.getMemberId());

        entranceRoom.setIsLast(OnStatusType.N);
        entranceRoom.setLeaveTime(LocalDateTime.now());
        member.setEntranceStatus(OnStatusType.N);
        worldRoom.setPeople(worldRoom.getPeople()-1);

        // 대화 시간 저장 : 단위 sec
        Duration duration = Duration.between(entranceRoom.getJoinTime(), entranceRoom.getLeaveTime());
        Long seconds = duration.getSeconds();
        member.setConversationTime(member.getConversationTime() + seconds);

        // member 가 방을 나갔으므로 대화 관리 제거
        conversationService.leaveRoom(photonMemberDto.getMemberId());

        return new PhotonResponseDto(0, "OK");
    }

    public PhotonResponseDto pathEvent(PhotonConversationDto photonConversationDto) {
        // 발화 문장이 들어오면 대화 관리를 위해 전달
        conversationService.sentence(photonConversationDto);
        return new PhotonResponseDto(0,"ok");
    }

    public PhotonResponseDto pathEvent(PhotonMemberInOutDto photonMemberInOutDto) {
        if (photonMemberInOutDto.getIsIn()){
            conversationService.vivoxMemberIn(photonMemberInOutDto);
        } else {
            conversationService.vivoxMemberOut(photonMemberInOutDto);
        }
        return new PhotonResponseDto(0,"ok");
    }

}
