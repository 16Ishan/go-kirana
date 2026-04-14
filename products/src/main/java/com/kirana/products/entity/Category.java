package com.kirana.products.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {

  @Id @GeneratedValue private UUID id;

  private String name;
}
