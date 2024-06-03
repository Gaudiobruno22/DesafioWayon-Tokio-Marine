package com.example.api.service;

import java.util.List;
import java.util.Optional;

import com.example.api.exception.RequiredObjectException;
import com.example.api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.api.domain.Customer;
import com.example.api.repository.CustomerRepository;

@Service
public class CustomerService {

	private CustomerRepository repository;

	@Autowired
	public CustomerService(CustomerRepository repository) {
		this.repository = repository;
	}

	public List<Customer> findAll() {
		return repository.findAllByOrderByNameAsc();
	}

	public Optional<Customer> findById(Long id) {
		return repository.findById(id);
	}

	public Customer newCustomer(Customer newCustomer){
		return repository.save(newCustomer);
	}

	public Boolean findCustomer(Long Id){
		return repository.existsById(Id);
	}

	public Customer atualizaCadastro(Customer cadastro) {
		if (cadastro == null) {
			throw new RequiredObjectException();
		}
		Customer cad = repository.findById(cadastro.getId())
				.orElseThrow(ResourceNotFoundException::new);
		atualizarDados(cad, cadastro);
		return repository.save(cad);
	}

	private void atualizarDados(Customer existente, Customer novo) {
		existente.setName(novo.getName());
		existente.setEmail(novo.getEmail());
	}

	public Page<Customer> buscaCadastros(Pageable pageable){
		var solicitacaoPage = repository.findAll(pageable);
        return solicitacaoPage;
    }
}
