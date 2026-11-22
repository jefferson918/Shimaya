package com.shimaya.demo.controller;

import com.shimaya.demo.dao.SedeDAO;
import com.shimaya.demo.model.Sede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@CrossOrigin(origins = "*")
public class SedeController {

    @Autowired
    private SedeDAO sedeDAO;

    @GetMapping
    public List<Sede> obtenerSedes() {
        return sedeDAO.obtenerTodas();
    }
}