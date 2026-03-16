package com.restaurant.menuservice.controller;
import com.restaurant.menuservice.model.MenuItem;
import com.restaurant.menuservice.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/menu")
@RequiredArgsConstructor @CrossOrigin(origins = "*")
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/items")
    public ResponseEntity<List<MenuItem>> getAll(@RequestParam(required=false) String category) {
        return ResponseEntity.ok(menuService.getAll(category));
    }
    @GetMapping("/items/{id}")
    public ResponseEntity<MenuItem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getById(id));
    }
    @GetMapping("/categories")
    public ResponseEntity<List<String>> categories() {
        return ResponseEntity.ok(menuService.getCategories());
    }
    @PostMapping("/items")
    public ResponseEntity<MenuItem> create(@RequestBody MenuItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.create(item));
    }
    @PutMapping("/items/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable Long id, @RequestBody MenuItem item) {
        return ResponseEntity.ok(menuService.update(id, item));
    }
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id); return ResponseEntity.noContent().build();
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("Menu Service UP"); }
}
