package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class NotificationController {

    @PostMapping
    public ResponseEntity<String> receiveNotification(@RequestBody String message) {
        System.out.println("🔔 Notification reçue : " + message);
        return ResponseEntity.ok("Notification bien reçue");
    }
}
