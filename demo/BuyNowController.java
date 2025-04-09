package ass.ass.controller;

import ass.ass.models.*;
import ass.ass.repository.OrderDetailsRepository;
import ass.ass.repository.OrderRepository;
import ass.ass.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.time.LocalDate;

@Controller
@RequestMapping("/order")
public class BuyNowController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ProductRepository productsRepository;

    @PostMapping("/buy-now")
    @ResponseBody
    public String buyNow(@RequestParam String username,
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        // Tạo đơn hàng mới
        Orders order = new Orders();
        order.setCreateDate(Date.valueOf(LocalDate.now()));
        order.setAddress(""); // Để trống, yêu cầu người dùng nhập sau
        Accounts account = new Accounts();
        account.setUsername(username);
        order.setAccount(account);

        // Lưu đơn hàng
        order = orderRepository.save(order);

        // Tạo chi tiết đơn hàng
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        OrderDetails orderDetail = new OrderDetails();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        
        orderDetail.setQuantity(quantity);
orderDetail.setPrice(product.getPrice());
        // Lưu chi tiết đơn hàng
        orderDetailsRepository.save(orderDetail);

        return String.valueOf(order.getId()); // Trả về ID đơn hàng
    }
}