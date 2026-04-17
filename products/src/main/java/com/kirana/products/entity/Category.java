package com.kirana.products.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

  @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

  private String name;
}
