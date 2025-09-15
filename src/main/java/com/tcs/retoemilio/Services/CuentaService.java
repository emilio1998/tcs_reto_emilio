package com.tcs.retoemilio.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.retoemilio.Models.Cliente;
import com.tcs.retoemilio.Models.Cuenta;
import com.tcs.retoemilio.Models.Persona;
import com.tcs.retoemilio.Repo.ClienteRepo;
import com.tcs.retoemilio.Repo.CuentaRepo;
import com.tcs.retoemilio.modelsDTO.CrearCuentaDTO;
import com.tcs.retoemilio.modelsDTO.EditarCuentaDTO;

@Service
public class CuentaService {
    
    @Autowired
    private CuentaRepo cuentaRepo;

    @Autowired
    private ClienteRepo clienteRepo;

    private static boolean esSoloNumerosYLongitudValida(String texto) {
        return texto.matches("\\d{10}");
    }

    private static boolean esSoloNumeros(String texto) {
        return texto != null && texto.matches("\\d+");
    }

    public Cuenta crearCuenta(CrearCuentaDTO cuentaDTO) {

        if(!esSoloNumerosYLongitudValida(cuentaDTO.getIdentificacion()))
            throw new RuntimeException("La cédula debe tener 10 dígitos y no puede tener otro caracter más que números");

        Cliente cliente = clienteRepo.findByIdentificacion(cuentaDTO.getIdentificacion())
            .orElseThrow(() -> new RuntimeException("No encontrada"));

        if(!esSoloNumeros(cuentaDTO.getNumero_cuenta()))
            throw new RuntimeException("El número de cuenta solo debe tener números");

        Persona persona = new Persona();
        persona.setIdentificacion(cliente.getIdentificacion());
        persona.setNombre(cliente.getNombre());
        persona.setGenero(cliente.getGenero());
        persona.setEdad(cliente.getEdad());
        persona.setDireccion(cliente.getDireccion());
        persona.setTelefono(cliente.getTelefono());

        Cuenta cuenta = new Cuenta();
        cuenta.setNumero_cuenta(cuentaDTO.getNumero_cuenta());
        cuenta.setTipo_cuenta(cuentaDTO.getTipo_cuenta());
        cuenta.setSaldo_inicial(cuentaDTO.getSaldo_inicial());
        cuenta.setEstado(cuentaDTO.getEstado());
        cuenta.setPersona(persona);
        return cuentaRepo.save(cuenta);
    }

    public Cuenta editarCuenta(EditarCuentaDTO cuentaDTO, String identificacion) {
        Cuenta cuenta = cuentaRepo.findByPersona_Identificacion(identificacion)
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if(!esSoloNumeros(cuentaDTO.getNumero_cuenta()))
            throw new RuntimeException("El número de cuenta solo debe tener números");

        cuenta.setNumero_cuenta(cuentaDTO.getNumero_cuenta());
        cuenta.setTipo_cuenta(cuentaDTO.getTipo_cuenta());
        cuenta.setSaldo_inicial(cuentaDTO.getSaldo_inicial());
        cuenta.setEstado(cuentaDTO.getEstado());
        return cuentaRepo.save(cuenta);
    }

    public void eliminarCuenta(String identificacion) {
        Cuenta cuenta = cuentaRepo.findByPersona_Identificacion(identificacion)
            .orElseThrow(() -> new RuntimeException("No encontrada"));
        cuentaRepo.delete(cuenta);
    }
}
