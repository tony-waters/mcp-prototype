package uk.bit1.mcpprototype.support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class SupportWorkspaceController {

    @GetMapping({"/", "/support"})
    String showWorkspace() {
        return "support/workspace";
    }
}
