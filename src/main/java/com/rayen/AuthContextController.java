package com.rayen;
import com.rayen.AuthContextService;
import com.rayen.userManaement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth-context")
@RequiredArgsConstructor
public class AuthContextController {

    private final AuthContextService authContextService;

    // GET /api/v1/auth-context/user-id
    @GetMapping("/user-id")
    public ResponseEntity<Long> getLoggedInUserId() {
        return ResponseEntity.ok(authContextService.getLoggedInUserId());
    }

    // GET /api/v1/auth-context/me
    @GetMapping("/me")
    public ResponseEntity<User> getLoggedInUser() {
        return ResponseEntity.ok(authContextService.getLoggedInUser());
    }
}
