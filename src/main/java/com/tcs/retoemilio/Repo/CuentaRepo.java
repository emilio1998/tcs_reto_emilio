package com.tcs.retoemilio.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.retoemilio.Models.Cuenta;

@Repository
public interface CuentaRepo extends JpaRepository<Cuenta, Integer> {
    @Query(value = "SELECT c.*, NOMBRE, GENERO, EDAD, DIRECCION, TELEFONO FROM CUENTA c INNER JOIN PERSONA p ON c.IDENTIFICACION = p.IDENTIFICACION", nativeQuery = true)
    List<Cuenta> findAllWithPersona();

    Optional<Cuenta> findByPersona_Identificacion(String identificacion);
}
