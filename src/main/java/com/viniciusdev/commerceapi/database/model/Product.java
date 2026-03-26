package com.viniciusdev.commerceapi.database.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    @ManyToMany
    @JoinTable (name = "product_category", joinColumns = @JoinColumn(name ="product_id"), inverseJoinColumns = @JoinColumn(name ="category_id"))
    @Setter(AccessLevel.NONE)
    Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "product")
    @Setter(AccessLevel.NONE)
    Set <OrderItem> orders = new HashSet<>();

    public Product(Long id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public void clearCategories() {
        categories.clear();
    }

    public List<Order> getOrders() {
        return orders.stream()
                .map(OrderItem::getOrder)
                .toList();
    }
}
