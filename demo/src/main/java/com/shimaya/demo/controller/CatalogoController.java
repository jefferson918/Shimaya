package com.shimaya.demo.controller;

import com.shimaya.demo.model.Platillo;
import com.shimaya.demo.dao.PlatilloDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {

    @Autowired
    private PlatilloDAO platilloDAO;

    @GetMapping("/platillos")
    public List<Platillo> obtenerPlatillos() {
        return platilloDAO.obtenerTodos();
    }
}