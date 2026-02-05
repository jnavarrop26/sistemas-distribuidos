package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import services.MailService;

@Controller
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/")
    public String index() {
        return "index"; // resolves to src/main/resources/templates/index.html
    }

    @PostMapping("/send")
    public String sendEmail(@RequestParam String to,
                            @RequestParam String subject,
                            @RequestParam String body,
                            RedirectAttributes redirect) {
        mailService.sendSimpleEmail(to, subject, body);
        redirect.addFlashAttribute("message", "Email sent successfully");
        return "redirect:/";
    }
}