package ass.ass.controller;

import ass.ass.models.Accounts;
import ass.ass.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String viewOrders(@RequestParam String username, Model model, HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null || !user.getUsername().equals(username)) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.getOrdersByUsername(username));
        model.addAttribute("username", username);
        return "index/orders";
    }
}