package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.repository.WorldRepository;

@Service
@RequiredArgsConstructor
public class WorldService {

    private final WorldRepository worldRepository;


}
