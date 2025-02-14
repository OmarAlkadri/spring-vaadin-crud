package org.vaadin.example.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.example.data.Person;

import com.github.javafaker.Faker;

@Service
@ConditionalOnProperty(name = "db.enabled", havingValue = "false")
public class PersonServiceDummy implements IPersonService {

    private final List<Person> dummyData;

    public PersonServiceDummy() {
        Faker faker = new Faker();
        this.dummyData = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> {
                    Person person = new Person();
                    person.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
                    person.setFirstName(faker.name().firstName());
                    person.setLastName(faker.name().lastName());
                    person.setNationalNumber(faker.number().digits(10));
                    return person;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Person> get(Long id) {
        return dummyData.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public Person save(Person entity) {
        entity.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
        dummyData.add(entity);
        return entity;
    }

    @Override
    public Person update(Long id, Person entity) {
        for (int i = 0; i < dummyData.size(); i++) {
            if (dummyData.get(i).getId().equals(id)) {
                entity.setId(id);
                dummyData.set(i, entity);
                return entity;
            }
        }
        throw new IllegalArgumentException("Personel bulunamadı!");
    }

    @Override
    public void delete(Long id) {
        dummyData.removeIf(p -> p.getId().equals(id));
    }

    @Override
    public Page<Person> list(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dummyData.size());

        List<Person> pagedList = dummyData.subList(start, end);
        return new PageImpl<>(pagedList, pageable, dummyData.size());
    }

    @Override
    public long count() {
        return dummyData.size();
    }

    public List<Person> searchByName(String firstName) {
        return dummyData.stream()
                .filter(person -> person.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                .collect(Collectors.toList());
    }
}
