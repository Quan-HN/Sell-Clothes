package ass.ass.controller;

import ass.ass.dao.CategoryDao;
import ass.ass.dao.ProductDao;
import ass.ass.models.Categories;
import ass.ass.models.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products/admin") // Đặt mapping chung để dễ quản lý
public class ProductsController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    @GetMapping("")
    public String getAllProducts(Model model) {
        List<Products> products = productDao.findAll();
        model.addAttribute("products", products);
        return "admin/products"; // Trả về template HTML
    }

    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        List<Categories> categories = categoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", new Products());
        return "admin/add_product"; // Trả về template form thêm sản phẩm
    }

    @PostMapping("/new")
    public String addProduct(@ModelAttribute Products product,
            @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String cleanName = removeAccentsAndSpaces(product.getName());
                String originalFilename = imageFile.getOriginalFilename();
                @SuppressWarnings("null")
                String extension = originalFilename.substring(originalFilename.lastIndexOf(""));
                String newFileName = cleanName + extension;

                String imagePath = "C:/Users/PHUOC THANH/Documents/FPT Polytechnic/5_Spring_2025/Java 5/ass/src/main/resources/static/photos/"
                        + newFileName;

                imageFile.transferTo(new File(imagePath));
                product.setImage(newFileName);
            } catch (IOException e) {
                System.out.println("LỖI: " + e.getMessage());
                // Consider adding better error handling
            }
        }
        productDao.save(product);
        return "redirect:/products/admin";
    }

    // Helper method to remove accents and spaces
    private String removeAccentsAndSpaces(String str) {
        if (str == null)
            return "";

        // Convert to lowercase and replace spaces with nothing
        str = str.toLowerCase().replaceAll("\\s+", "");

        // Remove Vietnamese accents
        str = str.replaceAll("[ ]", "")
                .replaceAll("[àáạảãâầấẩậẫăằắẳặẵ]", "a")
                .replaceAll("[èéẹẻẽêềếểệễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốổộỗơờớởợỡ]", "o")
                .replaceAll("[ùúụủũưừứửựữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("đ", "d");

        return str;
    }

    @GetMapping("/{id}")
    public String showEditProductForm(@PathVariable int id, Model model) {
        Products product = productDao.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products/admin";
        }
        List<Categories> categories = categoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product);
        return "admin/edit_product"; // Trang sửa sản phẩm
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable int id,
            @ModelAttribute Products product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Products existingProduct = productDao.findById(id).orElse(null);
        if (existingProduct != null) {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = "C:/Users/PHUOC THANH/Documents/FPT Polytechnic/5_Spring_2025/Java 5/ass/src/main/resources/static/images/"
                        + imageFile.getOriginalFilename();
                try {
                    imageFile.transferTo(new File(imagePath));
                    product.setImage("/images/" + imageFile.getOriginalFilename());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                product.setImage(existingProduct.getImage()); // Giữ hình ảnh cũ
            }
            product.setId(id); // Đảm bảo ID không bị thay đổi
            productDao.save(product);
        }
        return "redirect:/products/admin";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable int id) {
        productDao.deleteById(id);
        return "redirect:/products/admin";
    }
}