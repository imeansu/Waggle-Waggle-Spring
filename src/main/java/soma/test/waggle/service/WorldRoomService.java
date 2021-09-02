package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.WorldRoomResponseDto;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.repository.WorldRoomRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorldRoomService {

    private final WorldRoomRepository worldRoomRepository;


    @Transactional(readOnly = true)
    public List<WorldRoomResponseDto> openWorldRoomList(){
        return worldRoomRepository.findAllByCriteria(OnStatus.Y).stream()
                .map(WorldRoomResponseDto::of)
                .collect(Collectors.toList());
    }

}
