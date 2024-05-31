package com.example.gtics_lab7_20213704.Controller;

import com.example.gtics_lab7_20213704.Entity.User;
import com.example.gtics_lab7_20213704.Repository.ResourceRepository;
import com.example.gtics_lab7_20213704.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
public class HomeController {
    //Consideramos roles "Contador"  , "Cliente" , "AnalistaPromociones" y "AnalistaLogico"
    //Consideramos resources "Servidor de contabilidad" , "Servidor de clientes" , "Servidor de promociones" , "impresora"

    final ResourceRepository resourceRepository;
    final UserRepository userRepository;

    public HomeController(ResourceRepository resourceRepository ,UserRepository userRepository ){
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;

    }
    //Creado de un rol contador
    @GetMapping(value = "/SDN/createContador")
    public Object listLeaderBoardByRegion(@RequestParam(value = "name" ,required = false ) String name) {
        try {
            if(name !=null) {
                User u = new User();
                u.setActive(true);
                u.setType("Contador");
                u.setName(name);
                u.setAuthorizedResource(resourceRepository.findResourceByName("Servidor de contabilidad"));
                User newU = userRepository.save(u);
                LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
                response.put("Estado", "creado con exito");
                response.put("Id del contador creado ", "" + newU.getId());
                response.put("Date", "" + LocalDateTime.now());
                return ResponseEntity.ok(response);
            }else{
                HashMap<String, Object> er = new HashMap<>();
                er.put("error","Debes ingresar un nombre");
                er.put("date",""+ LocalDateTime.now());
                return ResponseEntity.badRequest().body(er);
            }
        }catch (Exception err){
            HashMap<String, Object> er = new HashMap<>();
            er.put("error","ocurrio un error inesperado");
            er.put("date",""+ LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }
    @ExceptionHandler({HttpMessageNotReadableException.class} )
    public Object gestionExcetion (HttpServletRequest request){
        HashMap<String , Object> responseMap = new HashMap<>();
        if(request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("GET")){
            responseMap.put("estado" , "error");
            responseMap.put("msg" , "Auxilio");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

    //Listado de personas autorizadas por rol







}
