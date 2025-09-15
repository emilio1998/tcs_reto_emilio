package com.tcs.retoemilio.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.tcs.retoemilio.Excepciones.FormatoNumericoExcepcion;
import com.tcs.retoemilio.Models.Cliente;
import com.tcs.retoemilio.Models.Cuenta;
import com.tcs.retoemilio.Repo.CuentaRepo;
import com.tcs.retoemilio.Services.ClienteService;
import com.tcs.retoemilio.Services.CuentaService;
import com.tcs.retoemilio.modelsDTO.CrearClienteDTO;
import com.tcs.retoemilio.modelsDTO.CrearCuentaDTO;
import com.tcs.retoemilio.modelsDTO.EditarClienteDTO;
import com.tcs.retoemilio.modelsDTO.EditarCuentaDTO;

import org.springframework.web.bind.annotation.*;



@RestController
public class F1Controller {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaRepo cuentaRepo;

    //Algunas pruebas

    @GetMapping(value = "/obtenerUsuarios")
    public List<Cliente> obtenerUsuarios() {
        return clienteService.obtenerUsuarios();
    }
    
    @GetMapping(value = "/obtenerCuentas")
    public List<Cuenta> obtenerCuentas() {
        return cuentaRepo.findAllWithPersona();
    }




    //RETO
    
    //CLIENTES

    @PostMapping(value = "/clientes")
    public Map<String, Object> crearCliente(@RequestBody CrearClienteDTO clienteDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
            clienteService.crearCliente(clienteDTO);
            response.put("mensaje", "El cliente con el num de identificacion "+ clienteDTO.getIdentificacion() +" se registró con éxito");
            response.put("datos", clienteDTO);
            response.put("codigo", 200);
        } catch (FormatoNumericoExcepcion e) {
            response.put("mensaje", "Error: " + e.getMessage());
            response.put("codigo", 400);
        } catch (Exception e) {
            response.put("mensaje", "Error inesperado al eliminar el cliente");
            response.put("codigo", 500);
        }
        return response;
    }

    @PutMapping(value = "/clientes/{identificacion}")
    public Map<String, Object> editarCliente(@RequestBody EditarClienteDTO clienteDTO, @PathVariable String identificacion) {
        Map<String, Object> response = new HashMap<>();
        try{
            clienteService.editarCliente(clienteDTO, identificacion);
            response.put("mensaje", "El cliente con el num de identificacion "+ identificacion +" se editó con éxito");
            response.put("datos", clienteDTO);
            response.put("codigo", 200);
        } catch (FormatoNumericoExcepcion e) {
            response.put("mensaje", "Error: " + e.getMessage());
            response.put("codigo", 400);
        } catch (Exception e) {
            response.put("mensaje", "Error inesperado al eliminar el cliente");
            response.put("codigo", 500);
        }
        return response;
    }

    @DeleteMapping(value = "/clientes/{identificacion}")
    public Map<String, Object> eliminarCliente(@PathVariable String identificacion) {
        Map<String, Object> response = new HashMap<>();

        try{
            clienteService.eliminarCliente(identificacion);
            response.put("mensaje", "El cliente con el num de identificacion "+ identificacion +" se eliminó con éxito");
            response.put("codigo", 200);
        } catch (RuntimeException e) {
            response.put("mensaje", "Error: " + e.getMessage());
            response.put("codigo", 400);
        } catch (Exception e) {
            response.put("mensaje", "Error inesperado al eliminar el cliente");
            response.put("codigo", 500);
        }
        return response;
    }

    //CUENTAS

    @PostMapping(value = "/cuentas")
    public Map<String, Object> crearCuenta(@RequestBody CrearCuentaDTO cuentaDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
            cuentaService.crearCuenta(cuentaDTO);
            response.put("mensaje", "La cuenta con el num de identificación "+ cuentaDTO.getIdentificacion() +" se registró con éxito");
            response.put("datos", cuentaDTO);
            response.put("codigo", 200);
        } catch (RuntimeException e) {
            response.put("mensaje", "Error: " + e.getMessage());
            response.put("codigo", 400);
        } catch (Exception e) {
            response.put("mensaje", "Error inesperado al eliminar el cliente");
            response.put("codigo", 500);
        }
        return response;
    }

    @PutMapping(value = "/cuentas/{identificacion}")
    public Map<String, Object> editarCuenta(@RequestBody EditarCuentaDTO cuentaDTO, @PathVariable String identificacion) {
        Map<String, Object> response = new HashMap<>();
        try{
            cuentaService.editarCuenta(cuentaDTO, identificacion);
            response.put("mensaje", "La cuenta con el num de identificación "+ identificacion +" se editó con éxito");
            response.put("datos", cuentaDTO);
            response.put("codigo", 200);
        } catch (RuntimeException e) {
            response.put("mensaje", "Error: " + e.getMessage());
            response.put("codigo", 400);
        } catch (Exception e) {
            response.put("mensaje", "Error inesperado al eliminar el cliente");
            response.put("codigo", 500);
        }
        return response;
    }

    @DeleteMapping(value = "/cuentas/{identificacion}")
    public Map<String, Object> eliminarCuenta(@PathVariable String identificacion) {
        Map<String, Object> response = new HashMap<>();

        try{
            cuentaService.eliminarCuenta(identificacion);
            response.put("mensaje", "La cuenta con el num de identificación "+ identificacion +" se eliminó con éxito");
            response.put("codigo", 200);
        } catch (RuntimeException e) {
            response.put("mensaje", "Error: " + e.getMessage());
            response.put("codigo", 400);
        } catch (Exception e) {
            response.put("mensaje", "Error inesperado al eliminar el cliente");
            response.put("codigo", 500);
        }
        return response;
    }
}
