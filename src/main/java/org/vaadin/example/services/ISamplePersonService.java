package org.vaadin.example.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.example.data.SamplePerson;

public interface ISamplePersonService {
    Optional<SamplePerson> get(Long id);

    SamplePerson save(SamplePerson entity);

    void delete(Long id);

    Page<SamplePerson> list(Pageable pageable);

    Page<SamplePerson> list(Pageable pageable, Specification<SamplePerson> filter);

    long count();
}
