package com.tcs.retoemilio.modelsDTO;

import lombok.Data;

@Data
public class CrearMovimientoDTO {
    private String identificacion;
    private String tipoMovimiento;
    private String detalle_movimiento;
    private Float valor;
}
