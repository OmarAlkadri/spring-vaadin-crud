package org.vaadin.example.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.example.data.Person;

public interface IPersonService {
    Optional<Person> get(Long id);

    Person save(Person entity);

    void delete(Long id);

    Page<Person> list(Pageable pageable);

    public List<Person> searchByName(String firstName);

    long count();

    Person update(Long id, Person entity);
}
