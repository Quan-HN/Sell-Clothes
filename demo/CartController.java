package ass.ass.controller;

import ass.ass.dao.CartDao;
import ass.ass.models.Accounts;
import ass.ass.models.Cart;
import ass.ass.repository.CartRepository;
import ass.ass.services.CartService;
import ass.ass.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartDao cartDao;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Cart> cartItems = cartService.viewCart(user.getUsername());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("username", user.getUsername());
        return "index/cart";
    }

    @PostMapping("/add")
public String addToCart(@RequestParam(required = false) String username, @RequestParam int productId, @RequestParam int quantity, HttpSession session) {
    // Kiểm tra nếu người dùng đăng nhập từ session
    Accounts accounts = (Accounts) session.getAttribute("user");
    System.out.println("Session user: " + (accounts != null ? accounts.getUsername() : "null"));

    // Nếu người dùng không đăng nhập, có thể để username là null hoặc chuỗi rỗng
    if (accounts == null && (username == null || username.isEmpty())) {
        // Nếu không có username từ session và không có username từ request, có thể xử lý theo kiểu guest
        username = "guest"; // hoặc để null nếu bạn muốn xử lý giỏ hàng theo cách khác cho người dùng không đăng nhập
    } else if (accounts != null) {
        username = accounts.getUsername();
    }

    try {
        // Gọi dịch vụ thêm sản phẩm vào giỏ hàng, không cần phải kiểm tra người dùng đã đăng nhập hay chưa
        cartService.addToCart(username, productId, quantity);
        return "redirect:/home";
    } catch (Exception e) {
        System.out.println("Error adding to cart: " + e.getMessage());
        return "redirect:/home?error=add_failed";
    }
}


@PostMapping("/update")
public String updateCart(@RequestParam int productId, @RequestParam int quantity) {
    try {
        // Cập nhật số lượng sản phẩm trong giỏ hàng mà không cần xác thực người dùng
        cartService.updateCart(productId, quantity);
        return "redirect:/cart";  // Quay lại trang giỏ hàng
    } catch (Exception e) {
        System.out.println("Lỗi khi cập nhật giỏ hàng: " + e.getMessage());
        return "redirect:/cart?error=update_failed";
    }
}


    @PostMapping("/remove/{id}")
    public String deleteProduct(@PathVariable long id) {
        cartDao.deleteById(id);
        return "redirect:/cart?username=";
    }

    @PostMapping("/cart/checkout")
    public String checkout(@RequestParam String address, HttpSession session) {
        String username = (String) session.getAttribute("username");  // Lấy tên đăng nhập từ session
        
        if (username != null) {
            cartService.checkoutAndClearCart(username);  // Xóa giỏ hàng sau khi thanh toán
        }
    
        return "redirect:/home";  // Chuyển hướng về trang chủ
    }
    @PostMapping("/api/add")
@ResponseBody
public ResponseEntity<String> addToCartAPI(@RequestParam String productId,
                                           @RequestParam int quantity,
                                           HttpSession session) {
    String sessionId = (String) session.getAttribute("username");
    if (sessionId == null) {
        sessionId = "guest"; // Nếu chưa đăng nhập
    }

    try {
        int id = Integer.parseInt(productId);
        cartService.addToCart(sessionId, id, quantity);
        return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng.");
    } catch (NumberFormatException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: ID sản phẩm không hợp lệ.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
    }
}


    
}
