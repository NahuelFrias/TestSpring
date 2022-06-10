package com.example.nahuel.test.springboot.app.repositories;

import com.example.nahuel.test.springboot.app.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
}
