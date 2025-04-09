package ass.ass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ass.ass.dao.CategoryDao;
import ass.ass.dao.ProductDao;
import ass.ass.models.Categories;
import ass.ass.models.Products;

@Controller
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    // Hiển thị danh sách danh mục
    @GetMapping("admin")
    public String getAllCategories(Model model) {
        List<Categories> categories = categoryDao.findAll();
        model.addAttribute("categories", categories);
        return "admin/categories"; // Trả về template admin/categories.html
    }

    // Hiển thị sản phẩm theo danh mục
    @GetMapping("/{categoryId}")
    public String getProductsByCategory(@PathVariable String categoryId, Model model) {
        List<Products> products = productDao.findByCategoryId(categoryId);
        model.addAttribute("products", products);
        return "index/category"; // Trả về template HTML cho người dùng
    }

    // Hiển thị form thêm danh mục
    @GetMapping("/admin/new")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Categories());
        return "admin/categories"; // Tái sử dụng view hoặc tạo view riêng nếu cần
    }

    // Xử lý thêm danh mục
    @PostMapping
    public String addCategory(@RequestParam String id, @RequestParam String name) {
        Categories category = Categories.builder().id(id).name(name).build();
        categoryDao.save(category);
        return "redirect:/categories/admin"; // Sửa ở đây
    }

    // Hiển thị form sửa danh mục
    @GetMapping("/admin/edit/{id}")
    public String showEditCategoryForm(@PathVariable String id, Model model) {
        Categories category = categoryDao.findById(id).orElse(null);
        if (category == null) {
            return "redirect:/categories"; // Nếu không tìm thấy, quay lại danh sách
        }
        model.addAttribute("category", category);
        return "admin/categories"; // Tái sử dụng view hoặc tạo view riêng nếu cần
    }

    // Xử lý sửa danh mục
    @PostMapping("/admin/update")
    public String updateCategory(@RequestParam String id, @RequestParam String name) {
        Categories category = Categories.builder().id(id).name(name).build();
        categoryDao.save(category);
        return "redirect:/categories/admin"; // Sửa ở đây
    }

    // Xử lý xóa danh mục
    @PostMapping("/admin/delete")
    public String deleteCategory(@RequestParam String id) {
        categoryDao.deleteById(id);
        return "redirect:/categories/admin"; // Sửa ở đây
    }
}