package com.codealpine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/customers")
public class Main {
    private final CustomerRepository customerRepository;

    public Main(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping()
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping
    public Customer addCustomer(@RequestBody NewCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setAge(request.age());

        return customerRepository.save(customer);
    }

    record NewCustomerRequest(String name, String email, Integer age) {
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer id) {
        customerRepository.deleteById(id);
    }


    @GetMapping("{customerId}")
    public Optional<Customer> getCustomer(@PathVariable("customerId") Integer id){
        return customerRepository.findById(id);
    }

    record UpdateCustomerRequest(String name, String email, Integer age){}
    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer id, @RequestBody UpdateCustomerRequest request) throws Exception {
        Optional<Customer> customer = customerRepository.findById(id);

        boolean changes = false;

        if (request.age() != null && !request.age().equals(customer.get().getAge())) {
            customer.get().setAge(request.age());
            changes = true;
        }

        if (request.name() != null && !request.name().equals(customer.get().getName())) {
            customer.get().setName(request.name());
            changes = true;
        }

        if (request.email() != null && !request.email().equals(customer.get().getEmail())) {
            customer.get().setEmail(request.email());
        }

        if (!changes) {
            throw new Exception("no data changes found");
        }
        customerRepository.saveAndFlush(customer.get());
    }
}
