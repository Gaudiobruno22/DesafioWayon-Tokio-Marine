package com.example.api.web.rest;

import java.util.List;

import com.example.api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.api.domain.Customer;
import com.example.api.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private CustomerService service;

	@Autowired
	public CustomerController(CustomerService service) {
		this.service = service;
	}

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@GetMapping
	public List<Customer> findAll() {
		return service.findAll();
	}

	@GetMapping(value = "/todos")
	public ResponseEntity<Page<Customer>> buscaTodosCadastros(@RequestParam(value = "page", defaultValue = "0") Integer page,
															  @RequestParam(value = "limit", defaultValue = "5") Integer limit,
															  @RequestParam(value = "direction", defaultValue = "asc") String direction){

		var directionPage = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(page, limit, Sort.by(directionPage, "nome"));
		return ResponseEntity.ok(service.buscaCadastros(pageable));
	}

	@GetMapping("/{id}")
	public Customer findById(@PathVariable Long id) {
		return service.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
	}

	@PostMapping("/new")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Customer> insereCadastro(@RequestBody Customer cadastro){
		try {
			service.newCustomer(cadastro);
			return ResponseEntity.status(HttpStatus.CREATED).body(cadastro);
		}
		catch (Exception e) {
			logger.error("Erro ao inserir cadastro: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping(value = "/update/{id}")
	public ResponseEntity<Customer> atualizaCadastro(@PathVariable Long id, @RequestBody Customer cadastro){
		cadastro.setId(id);
		if(!service.findCustomer(cadastro.getId())) {
			throw new ResourceNotFoundException();
		}
		else {
			service.atualizaCadastro(cadastro);
			logger.info("Cadastro está atualizado? " + cadastro.toString());
		}
		return ResponseEntity.ok(cadastro);
	}
}
