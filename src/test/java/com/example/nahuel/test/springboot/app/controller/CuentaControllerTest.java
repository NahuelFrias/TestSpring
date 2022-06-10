package com.example.nahuel.test.springboot.app.controller;

import com.example.nahuel.test.springboot.app.Datos;
import com.example.nahuel.test.springboot.app.models.Cuenta;
import com.example.nahuel.test.springboot.app.models.TransaccionDto;
import com.example.nahuel.test.springboot.app.services.CuentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuentaController.class) //indicamos a que clase le hacemos el test
class CuentaControllerTest {

    //pruebas para contexto MVC
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDetalle() throws Exception {
        //Hacemos una llamada el end point y esperamos que el resultado sea el correcto
        
        //Given
        //cuando invoque cuentaService.finById devolveme una cuenta, en este caso la primera (1L)
        when(cuentaService.findById(1L)).thenReturn(Datos.crearCuenta001().orElseThrow());
        //Then, va a esta URL y trae el JSON
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/1")
                        .contentType(MediaType.APPLICATION_JSON))
        //Then, esperamos un status OK porque así lo codeamos en el controlador
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))//esperamos que la resp sea un Json
                .andExpect(jsonPath("$.persona").value("Andrés"))//esperamos los datos
                .andExpect(jsonPath("$.saldo").value("1000"));
        //verificamos que se haya ejecutado el metodo
        verify(cuentaService).findById(1L);
    }

    @Test
    void testTransferir() throws Exception {

        //Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        System.out.println(objectMapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","Ok");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion", dto);

        System.out.println(objectMapper.writeValueAsString(response));
        //When
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito!"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenID").value(dto.getCuentaOrigenId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

    }

    @Test
    void testListar() throws Exception {
        //Given
        List<Cuenta> cuentas = Arrays.asList(Datos.crearCuenta001().orElseThrow(),
                Datos.crearCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);
        //When
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
        //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].persona").value("Andrés"))
                .andExpect(jsonPath("$[1].persona").value("Jhon"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));

        verify(cuentaService).findAll();

    }

    @Test
    void testGuardar() throws Exception {
        //Given
        Cuenta cuenta = new Cuenta(null,"Pepe",new BigDecimal("3000"));
        when(cuentaService.save(any())).then(invocation ->{
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        }); //para que me devuelva el id
        //When
        mvc.perform(MockMvcRequestBuilders.post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuenta)))
                //Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.persona", is("Pepe")))
                .andExpect(jsonPath("$.saldo", is(3000)));

        verify(cuentaService).save(any());
    }
}