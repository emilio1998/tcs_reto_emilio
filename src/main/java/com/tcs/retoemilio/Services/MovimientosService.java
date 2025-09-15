package com.tcs.retoemilio.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
        arrayMovimientos = arrayMovimientos.stream()
                .sorted(
                        Comparator.comparing((Movimientos m) -> m.getCuenta().getPersona().getNombre()) // primero nombre asc
                                .thenComparing(Movimientos::getId) // luego id asc
                )
                .toList();
        List<ReporteMovimientosDTO> reporteArray = new ArrayList();

        int countMovimiento = 0;
        for (Movimientos mov: arrayMovimientos) {
            LocalDateTime obtenerFecha = mov.getFechaMovimiento();

            if(obtenerFecha.isAfter(inicio) && obtenerFecha.isBefore(fin) && mov.getCuenta().getEstado().equalsIgnoreCase("activo")) {
                ReporteMovimientosDTO reporteObjeto = new ReporteMovimientosDTO();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String fechaFormateada = mov.getFechaMovimiento().format(formatter);
                float saldoInicial = 0;

                if (countMovimiento != 0 && 
                    arrayMovimientos.get(countMovimiento-1).getCuenta().
                        getPersona().getNombre().equalsIgnoreCase(mov.getCuenta().getPersona().getNombre())) {
                            saldoInicial = arrayMovimientos.get(countMovimiento-1).getSaldo();
                        } 

                reporteObjeto.setFechaMovimiento(fechaFormateada);
                reporteObjeto.setCliente(mov.getCuenta().getPersona().getNombre());
                reporteObjeto.setNumeroCuenta(mov.getCuenta().getNumero_cuenta());
                reporteObjeto.setTipo(mov.getCuenta().getTipo_cuenta());
                reporteObjeto.setTipoMovimiento(mov.getTipoMovimiento());
                reporteObjeto.setSaldoInicial(saldoInicial);
                reporteObjeto.setEstado(mov.getCuenta().getEstado());
                reporteObjeto.setMovimiento(mov.getValor());
                reporteObjeto.setSaldoDisponible(mov.getSaldo());
                reporteArray.add(reporteObjeto);
            }
            countMovimiento += 1;
        }
        return reporteArray;
    }

    public Movimientos obtenerMovimiento(String identificacion) {
        Movimientos movimiento = movimientosRepo.findByCuenta_Persona_Identificacion(identificacion)
            .orElseThrow(() -> new RuntimeException("No encontrada"));

        return movimiento;
    }

    public ReporteMovimientosDTO generarMovimiento(CrearMovimientoDTO movimientoDTO) {
        Cuenta cuenta = cuentaRepo.findByPersona_Identificacion(movimientoDTO.getIdentificacion())
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        float saldoInicial = cuenta.getSaldo_inicial();

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

        movimientosRepo.save(movimiento);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String fechaFormateada = now.format(formatter);
        
        ReporteMovimientosDTO reporteDTO = new ReporteMovimientosDTO();
        reporteDTO.setFechaMovimiento(fechaFormateada);
        reporteDTO.setCliente(cuenta.getPersona().getNombre());
        reporteDTO.setNumeroCuenta(cuenta.getNumero_cuenta());
        reporteDTO.setTipo(cuenta.getTipo_cuenta());
        reporteDTO.setTipoMovimiento(movimientoDTO.getTipoMovimiento());
        reporteDTO.setSaldoInicial(saldoInicial);
        reporteDTO.setEstado(cuenta.getEstado());
        reporteDTO.setMovimiento(valor);
        reporteDTO.setSaldoDisponible(actualizarSaldo);

        return reporteDTO;
    }
}
