package com.tcs.retoemilio.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.retoemilio.Excepciones.FondosInsuficientesExcepcion;
import com.tcs.retoemilio.Models.Movimientos;
import com.tcs.retoemilio.Services.MovimientosService;
import com.tcs.retoemilio.modelsDTO.CrearMovimientoDTO;
import com.tcs.retoemilio.modelsDTO.ReporteMovimientosDTO;

@RestController
public class F2F3Controller {

    @Autowired
    private MovimientosService movimientosService;

    @GetMapping(value = "/obtenerMovimiento/{identificacion}")
    public ResponseEntity<Movimientos> obtenerMovimiento(@PathVariable String identificacion) {
        Movimientos movimiento = movimientosService.obtenerMovimiento(identificacion);
        return ResponseEntity.ok(movimiento);
    }

    @PostMapping(value = "/movimientos")
    public Map<String, Object> generarMovimiento(@RequestBody CrearMovimientoDTO movimientoDTO) {
        Map<String, Object> response = new HashMap<>();

        try{
            ReporteMovimientosDTO reporteDTO = movimientosService.generarMovimiento(movimientoDTO);
            response.put("mensaje", "Movimiento Generado");
            response.put("resultado", reporteDTO);
            response.put("codigo", 200);
        } catch (FondosInsuficientesExcepcion e) {
            response.put("mensaje", "Error: " + e.getMessage());
            response.put("codigo", 400);
        } catch (Exception e) {
            response.put("mensaje", "Error inesperado al eliminar el cliente");
            response.put("codigo", 500);
        }
        return response;
    }
}
