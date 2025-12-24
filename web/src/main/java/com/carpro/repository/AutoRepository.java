package com.carpro.repository;

import com.carpro.model.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Long> {

    @Query("SELECT a FROM Auto a WHERE LOWER(a.marca) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.modello) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Auto> searchByMarcaOrModello(@Param("search") String search);
}
