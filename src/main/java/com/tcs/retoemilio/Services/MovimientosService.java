package com.tcs.retoemilio.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.retoemilio.Excepciones.FechaExcepcion;
import com.tcs.retoemilio.Excepciones.FondosInsuficientesExcepcion;
import com.tcs.retoemilio.Models.Cuenta;
import com.tcs.retoemilio.Models.Movimientos;
import com.tcs.retoemilio.Repo.CuentaRepo;
import com.tcs.retoemilio.Repo.MovimientosRepo;
import com.tcs.retoemilio.modelsDTO.CrearMovimientoDTO;
import com.tcs.retoemilio.modelsDTO.ReporteMovimientosDTO;

@Service
public class MovimientosService {
    @Autowired
    private CuentaRepo cuentaRepo;

    @Autowired
    private MovimientosRepo movimientosRepo;

    public List<ReporteMovimientosDTO> obtenerMovimientos(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new FechaExcepcion("Las fechas no pueden ser nulas. Formato esperado: yyyy-MM-dd");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new FechaExcepcion("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);
        List<Movimientos> arrayMovimientos = movimientosRepo.findAll();
        List<ReporteMovimientosDTO> reporteArray = new ArrayList();

        for (Movimientos mov: arrayMovimientos) {
            LocalDateTime obtenerFecha = mov.getFechaMovimiento();

            if(obtenerFecha.isAfter(inicio) && obtenerFecha.isBefore(fin) && mov.getCuenta().getEstado().equalsIgnoreCase("activo")) {
                ReporteMovimientosDTO reporteObjeto = new ReporteMovimientosDTO();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String fechaFormateada = mov.getFechaMovimiento().format(formatter);
                reporteObjeto.setFechaMovimiento(fechaFormateada);
                reporteObjeto.setCliente(mov.getCuenta().getPersona().getNombre());
                reporteObjeto.setNumeroCuenta(mov.getCuenta().getNumero_cuenta());
                reporteObjeto.setTipo(mov.getCuenta().getTipo_cuenta());
                reporteObjeto.setSaldoInicial(mov.getCuenta().getSaldo_inicial());
                reporteObjeto.setEstado(mov.getCuenta().getEstado());
                reporteObjeto.setMovimiento(mov.getValor());
                reporteObjeto.setSaldoDisponible(mov.getSaldo());
                reporteArray.add(reporteObjeto);
            }
        }
        return reporteArray;
    }

    public Movimientos obtenerMovimiento(String identificacion) {
        Movimientos movimiento = movimientosRepo.findByCuenta_Persona_Identificacion(identificacion)
            .orElseThrow(() -> new RuntimeException("No encontrada"));

        return movimiento;
    }

    public Movimientos generarMovimiento(CrearMovimientoDTO movimientoDTO) {
        Cuenta cuenta = cuentaRepo.findByPersona_Identificacion(movimientoDTO.getIdentificacion())
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (cuenta.getSaldo_inicial() == 0 && 
            movimientoDTO.getTipoMovimiento().equalsIgnoreCase("RETIRO")) 
                throw new FondosInsuficientesExcepcion("La persona con identificación " 
                + movimientoDTO.getIdentificacion() + " no tiene saldo disponible.");


        if (movimientoDTO.getTipoMovimiento().equalsIgnoreCase("RETIRO") && 
            movimientoDTO.getValor() > cuenta.getSaldo_inicial()) {
            throw new FondosInsuficientesExcepcion("Saldo insuficiente. Disponible: " 
                + cuenta.getSaldo_inicial() + " - Solicitado: " + movimientoDTO.getValor());
        }

        float valor = movimientoDTO.getValor();

        if (movimientoDTO.getTipoMovimiento().equalsIgnoreCase("RETIRO"))
            valor = -valor;

        float actualizarSaldo = cuenta.getSaldo_inicial() + valor;
        Movimientos movimiento = new Movimientos();
        movimiento.setTipoMovimiento(movimientoDTO.getTipoMovimiento());
        movimiento.setDetalle_movimiento(movimientoDTO.getDetalle_movimiento());
        movimiento.setValor(movimientoDTO.getValor());
        movimiento.setSaldo(actualizarSaldo);
        movimiento.setCuenta(cuenta);

        cuenta.setSaldo_inicial(actualizarSaldo);
        cuentaRepo.save(cuenta);

        return movimientosRepo.save(movimiento);
    }
}
