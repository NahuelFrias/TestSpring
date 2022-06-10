package com.example.nahuel.test.springboot.app.controller;

import com.example.nahuel.test.springboot.app.models.Cuenta;
import com.example.nahuel.test.springboot.app.models.TransaccionDto;
import com.example.nahuel.test.springboot.app.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping //lo dejamos sin url porque esta en /api/cuentas
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> listar(){
        return cuentaService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cuenta detalle(@PathVariable Long id){ //traemos una cuenta especifica buscada por id
        return cuentaService.findById(id);
    }

    @PostMapping //cuando se hace un post en la raiz se ejecuta este metodo
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta guardar(@RequestBody Cuenta cuenta){
        return cuentaService.save(cuenta);
    }

    /*
    pasamos por argumento el objeto que contiene
    la cuentaOrigenId, cuentaDestinoId, y el monto
    se lo conoce como  Dto
    un objeto con los 3 parametros
     */
    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransaccionDto dto){
        cuentaService.transferir(dto.getCuentaOrigenId(),
                dto.getCuentaDestinoId(),
                dto.getMonto(),dto.getBancoId());
        //creo el Json
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","Ok");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion", dto);
        //lo envio en el response con codigo 200(ok)
        return ResponseEntity.ok(response);
    }

}
