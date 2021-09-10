package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.WorldCreateRequestDto;
import soma.test.waggle.dto.WorldRoomResponseDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonResponseDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.EntranceRoom;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.repository.EntranceRoomRepository;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.repository.WorldRoomRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorldRoomService {

    private final WorldRoomRepository worldRoomRepository;
    private final EntranceRoomRepository entranceRoomRepository;
    private final MemberRepository memberRepository;


    @Transactional(readOnly = true)
    public List<WorldRoomResponseDto> openWorldRoomList(){
        return worldRoomRepository.findAllByCriteria(OnStatus.Y).stream()
                .map(WorldRoomResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorldCreateRequestDto createWorld(WorldCreateRequestDto worldCreateRequestDto) {
        return WorldCreateRequestDto.of(worldRoomRepository.save(worldCreateRequestDto.toWorldRoom()));
    }

    @Transactional
    public PhotonResponseDto pathCreateOrClose(PhotonRoomIdDto photonRoomIdDto, OnStatus onStatus) {
        Optional<WorldRoom> findRoom = worldRoomRepository.findById(photonRoomIdDto.getRoomId());
        if (findRoom.isEmpty()) {
            return new PhotonResponseDto(1, "No Room");
        } else {
            WorldRoom worldRoom = findRoom.get();
            worldRoom.setOnStatus(onStatus);
            return new PhotonResponseDto(0, "OK");
        }
    }

    @Transactional
    public PhotonResponseDto pathJoin(PhotonMemberDto photonMemberDto) {

        Member member = memberRepository.find(photonMemberDto.getMemberId());
        WorldRoom worldRoom = worldRoomRepository.find(photonMemberDto.getRoomId());

        EntranceRoom entranceRoom = EntranceRoom
                .builder()
                .worldRoom(worldRoom)
                .member(member)
                .isLast(OnStatus.Y)
                .joinTime(LocalDateTime.now())
                .build();
        entranceRoomRepository.save(entranceRoom);

        worldRoom.setPeople(worldRoom.getPeople()+1);
        member.setEntranceStatus(OnStatus.Y);

        return new PhotonResponseDto(0, "OK");
    }

    @Transactional
    public PhotonResponseDto pathLeave(PhotonMemberDto photonMemberDto) {

        Member member = memberRepository.find(photonMemberDto.getMemberId());
        WorldRoom worldRoom = worldRoomRepository.find(photonMemberDto.getRoomId());
        EntranceRoom entranceRoom = entranceRoomRepository.findByMemberId(photonMemberDto.getMemberId());

        entranceRoom.setIsLast(OnStatus.N);
        entranceRoom.setLeaveTime(LocalDateTime.now());
        member.setEntranceStatus(OnStatus.N);
        worldRoom.setPeople(worldRoom.getPeople()-1);

        return new PhotonResponseDto(0, "OK");
    }
}
