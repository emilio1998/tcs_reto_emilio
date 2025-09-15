package com.tcs.retoemilio.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.retoemilio.Models.Movimientos;

@Repository
public interface MovimientosRepo extends JpaRepository<Movimientos, Integer> {
    Optional<Movimientos> findByCuenta_Persona_Identificacion(String identificacion);
}
