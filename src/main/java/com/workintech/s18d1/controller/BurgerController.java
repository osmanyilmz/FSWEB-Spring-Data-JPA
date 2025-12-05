package com.workintech.s18d1.controller;

import com.workintech.s18d1.dao.BurgerDao;
import com.workintech.s18d1.dao.BurgerDaoImpl;
import com.workintech.s18d1.entity.BreadType;
import com.workintech.s18d1.entity.Burger;
import com.workintech.s18d1.exceptions.BurgerException;
import com.workintech.s18d1.exceptions.BurgerValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/workintech/burgers")
public class BurgerController {

    private final BurgerDao burgerDao;

    @Autowired
    public BurgerController(BurgerDao  burgerDao) {
        this.burgerDao = burgerDao;
    }

    @GetMapping
    public List<Burger> getAll() {
        log.info("Fetching all burgers");
        return burgerDao.findAll();
    }

    @GetMapping("/{id}")
    public Burger getById(@PathVariable Long id) {
        log.info("Fetching burger with id {}", id);
        Burger burger = burgerDao.findById(id);

        if (burger == null) {
            log.error("Burger with id {} not found", id);
            throw new BurgerException("Burger not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        return burger;
    }

    @PostMapping
    public Burger create(@RequestBody Burger burger) {
        log.info("Creating new burger: {}", burger);

        BurgerValidation.validateBurger(burger);

        return burgerDao.save(burger);
    }

    @PutMapping("/{id}")
    public Burger update(@PathVariable Long id, @RequestBody Burger updated) {
        log.info("Updating burger with id {}", id);

        Burger existing = burgerDao.findById(id);

        if (existing == null) {
            log.error("Burger not found for update, id {}", id);
            throw new BurgerException("Burger not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        BurgerValidation.validateBurger(updated);

        updated.setId(id);
        return burgerDao.update(updated);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        log.info("Deleting burger with id {}", id);

        Burger existing = burgerDao.findById(id);

        if (existing == null) {
            log.error("Burger not found for delete, id {}", id);
            throw new BurgerException("Burger not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        burgerDao.remove(id);
        return "Burger deleted with id: " + id;
    }

    @GetMapping("/findByPrice")
    public List<Burger> findByPrice(@RequestBody Double price) {
        log.info("Finding burgers with price greater than {}", price);

        if (price == null || price < 0) {
            throw new BurgerException("Price must be a positive number", HttpStatus.BAD_REQUEST);
        }

        return burgerDao.findByPrice(price);
    }

    @GetMapping("/findByBreadType")
    public List<Burger> findByBreadType(@RequestBody BreadType breadType) {
        log.info("Finding burgers with bread type {}", breadType);

        if (breadType == null) {
            throw new BurgerException("BreadType cannot be null", HttpStatus.BAD_REQUEST);
        }

        return burgerDao.findByBreadType(breadType);
    }

    @GetMapping("/findByContent")
    public List<Burger> findByContent(@RequestBody String content) {
        log.info("Finding burgers containing content: {}", content);

        if (content == null || content.isBlank()) {
            throw new BurgerException("Content cannot be empty", HttpStatus.BAD_REQUEST);
        }

        return burgerDao.findByContent(content);
    }
}
