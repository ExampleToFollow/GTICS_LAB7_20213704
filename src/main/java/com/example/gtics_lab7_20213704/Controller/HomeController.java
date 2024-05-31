package com.example.gtics_lab7_20213704.Controller;

import com.example.gtics_lab7_20213704.Entity.User;
import com.example.gtics_lab7_20213704.Repository.ResourceRepository;
import com.example.gtics_lab7_20213704.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

@RestController
public class HomeController {
    //Consideramos roles "Contador"  , "Cliente" , "AnalistaPromociones" y "AnalistaLogico"
    //Consideramos resources "ServidorDeContabilidad" , "ServidorDeClientes" , "ServidorDePromociones" , "impresora"

    final ResourceRepository resourceRepository;
    final UserRepository userRepository;

    public HomeController(ResourceRepository resourceRepository, UserRepository userRepository) {
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;

    }

    //Creado de un rol contador
    @GetMapping(value = "/SDN/createContador")
    public Object createContador(@RequestParam(value = "name", required = false) String name) {
        try {
            if (name != null) {
                User u = new User();
                u.setActive(true);
                u.setType("Contador");
                u.setName(name);
                u.setAuthorizedResource(resourceRepository.findResourceByName("ServidorDeContabilidad"));
                User newU = userRepository.save(u);
                LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
                response.put("Estado", "creado con exito");
                response.put("Id del contador creado ", "" + newU.getId());
                response.put("Date", "" + LocalDateTime.now());
                return ResponseEntity.ok(response);
            } else {
                HashMap<String, Object> er = new HashMap<>();
                er.put("error", "Debes ingresar un nombre");
                er.put("date", "" + LocalDateTime.now());
                return ResponseEntity.badRequest().body(er);
            }
        } catch (Exception err) {
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "ocurrio un error inesperado");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }


    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Object gestionExcetion(HttpServletRequest request) {
        HashMap<String, Object> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("GET")) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Auxilio");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

    //Listado de personas autorizadas por rol
    @GetMapping(value = "/SDN/usersWithAuthorization")
    public Object usersWithAuthorization(@RequestParam(value = "resourceName", required = false) String resourceName) {
        try {
            if (resourceName != null) {
                //Debe ser un recurso valido
                //Consideramos resources "ServidorDeContabilidad" , "ServidorDeClientes" , "ServidorDePromociones" , "impresora"
                if ((Arrays.asList("ServidorDePromociones", "ServidorDeContabilidad", "ServidorDeClientes", "impresora")).contains(resourceName)) {
                    return ResponseEntity.ok(userRepository.findByIdResourceName(resourceRepository.findResourceByName(resourceName).getId()));
                }else{
                    System.out.println("Hola 1");
                    HashMap<String, Object> er = new HashMap<>();
                    er.put("error", "Debes ingresar un nombre de recurso valido");
                    er.put("date", "" + LocalDateTime.now());
                    return ResponseEntity.badRequest().body(er);
                }
            } else {
                System.out.println("Hola 2");
                HashMap<String, Object> er = new HashMap<>();
                er.put("error", "Debes ingresar el nombre del recurso");
                er.put("date", "" + LocalDateTime.now());
                return ResponseEntity.badRequest().body(er);
            }
        } catch (Exception err) {
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "ocurrio un error inesperado");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }

    @DeleteMapping(value = "/SDN/borrarUsuario/{id}")
    public Object borrar(@PathVariable("id") String idUser) {
        HashMap<String, Object> responseMap = new HashMap<>();
        try {
            int id = Integer.parseInt(idUser);
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                responseMap.put("estado", "borrado exitoso");
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "no se encontró el user con id: " + id);
                return ResponseEntity.badRequest().body(responseMap);
            }
        } catch (NumberFormatException ex) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "El ID debe ser un número");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    //Apagado logico de todos los usuario //apagarTodo


    @GetMapping(value = "/SDN/apagadoLogico")
    public Object usersWithAuthorization() {
        userRepository.apagarTodo();
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("estado", "Apagado exitoso");
        return ResponseEntity.ok(responseMap);
    }

    //Actualizar el tipo de usuario


    @PutMapping(value = "/SDN/UpdateTipo")
    public Object updateTipo(@RequestBody User user ){
        HashMap<String, Object> responseMap = new HashMap<>();
        if (user.getId() != null && user.getId() > 0) {
            Optional<User> optUser = userRepository.findById(user.getId());
            if (optUser.isPresent()) {
                //Consideramos roles "Contador"  , "Cliente" , "AnalistaPromociones" y "AnalistaLogico"
                if(user.getType() != null  && (Arrays.asList("Contador", "Cliente", "AnalistaPromociones", "AnalistaLogico")).contains(user.getType())){
                    User auxPlayer = optUser.get();
                    auxPlayer.setType(user.getType());
                    User newUser = userRepository.save(auxPlayer);
                    //Cambiamos sus permisos
                    switch(user.getType()){
                        case "Contador":
                            userRepository.updateAuth(user.getId(), resourceRepository.findResourceByName("ServidorDeContabilidad").getId());
                            break;
                        case "Cliente":
                            userRepository.updateAuth(user.getId(), resourceRepository.findResourceByName("ServidorDeClientes").getId());
                            break;
                        case "AnalistaPromociones":
                            userRepository.updateAuth(user.getId(), resourceRepository.findResourceByName("ServidorDePromociones").getId());
                            break;
                        case "AnalistaLogico":
                            userRepository.updateAuth(user.getId(), resourceRepository.findResourceByName("impresora").getId());
                            break;
                    }
                    responseMap.put("estado", "actualizado");
                    return ResponseEntity.ok(responseMap);
                }else {
                        responseMap.put("estado", "error");
                        responseMap.put("msg", "debe ingresar un valor de type");
                        return ResponseEntity.badRequest().body(responseMap);
                }
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "El usuario a actualizar no existe");
                return ResponseEntity.badRequest().body(responseMap);
            }
        } else {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un ID");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }









    }