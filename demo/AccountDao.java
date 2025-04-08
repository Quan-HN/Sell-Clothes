package ass.ass.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ass.ass.models.Accounts;

@Repository
public interface AccountDao extends JpaRepository<Accounts, String> {
    // Thêm các phương thức tùy chỉnh nếu cần
    Accounts findByEmail(String email);
}