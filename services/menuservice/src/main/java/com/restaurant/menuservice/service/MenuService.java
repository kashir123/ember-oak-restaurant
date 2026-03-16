package com.restaurant.menuservice.service;
import com.restaurant.menuservice.model.MenuItem;
import com.restaurant.menuservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class MenuService {
    private final MenuItemRepository repo;

    public List<MenuItem> getAll(String category) {
        if (category != null && !category.isBlank())
            return repo.findByCategoryAndAvailableTrue(category);
        return repo.findByAvailableTrue();
    }
    public MenuItem getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Item not found: " + id));
    }
    public List<String> getCategories() {
        return repo.findAll().stream().map(MenuItem::getCategory).distinct().sorted().toList();
    }
    public MenuItem create(MenuItem item) { return repo.save(item); }
    // Get featured items for home page
    public List<MenuItem> getFeatured() {
        return repo.findByFeaturedTrue();
    }
    public MenuItem update(Long id, MenuItem updated) {
        MenuItem existing = getById(id);
        existing.setName(updated.getName()); existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice()); existing.setCategory(updated.getCategory());
        existing.setImageUrl(updated.getImageUrl()); existing.setAvailable(updated.isAvailable());
        return repo.save(existing);
    }
    public void delete(Long id) { repo.deleteById(id); }
}
