package ass.ass.controller;

import ass.ass.models.Accounts;
import ass.ass.models.Orders;
import ass.ass.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orders/admin")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // Hiển thị danh sách tất cả đơn hàng
    @GetMapping
    public String viewAllOrders(Model model, HttpSession session) {
        // Kiểm tra quyền admin
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        // Lấy danh sách tất cả đơn hàng từ service
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders"; // Trả về template admin/orders.html
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/details/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model, HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        // Lấy thông tin đơn hàng và chi tiết đơn hàng
        Orders order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderService.getOrderDetailsByOrderId(orderId));
        return "admin/order_details"; // Trả về template admin/order-details.html
    }
}