package ass.ass.controller;

import ass.ass.models.Accounts;
import ass.ass.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@SessionAttributes("user")
public class LoginController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("accounts", new Accounts());
        return "index/login";
    }

    @PostMapping("/register/new")
    public String newRegister(Model model, @Valid @ModelAttribute("accounts") Accounts accounts, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("errorMessage", "Cần nhập đúng các trường dữ liệu!!");
            return "index/login";
        }
        if (accountRepository.findById(accounts.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
            return "index/login";
        }
        accounts.setAdmin(false);
        accounts.setActivated(false);
        accountRepository.save(accounts);
        model.addAttribute("message", "Đã đăng ký thành công!!");
        return "index/login";
    }

    @GetMapping("/login")
    public String loginHome() {
        return "index/login";
    }

    @PostMapping("/login")
    public String login(Model model, @RequestParam String username, @RequestParam String password,
            HttpSession session) {
        Accounts account = accountRepository.findByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            session.setAttribute("user", account); // Lưu user vào session
            model.addAttribute("user", account); // Thêm vào model cho @SessionAttributes
            return "redirect:/home";
        } else {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "index/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, SessionStatus status) {
        status.setComplete();
        session.invalidate();
        return "redirect:/login";
    }
}