package soma.test.waggle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final HttpSession httpSession;
    private int num;
}
