package org.vaadin.example.services.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.example.domain.model.Person;
import org.vaadin.example.services.IPersonService;

import com.github.javafaker.Faker;

/**
 * Bu dummy class, dummy verilerle çalışan bir service implementasyonudur.
 * Gerçek bir database yerine, bellek içi bir list kullanarak Person object
 * işlemlerini gerçekleştirir.
 */
@Service
@Primary
@Profile("dummy") // Bu service yalnızca "dummy" profile aktif olduğunda çalışır.
@ConditionalOnProperty(name = "db.enabled", havingValue = "false", matchIfMissing = true)
// Eğer "db.enabled" true ise veya tanımlanmamışsa bu service etkin olur.
public class PersonServiceDummy implements IPersonService {

    private final List<Person> dummyData;

    public PersonServiceDummy() {
        Faker faker = new Faker();
        this.dummyData = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> {
                    Person person = new Person();
                    person.setId(Math.abs(UUID.randomUUID().getMostSignificantBits())); // UUID'den pozitif bir ID
                                                                                        // üretiliyor.
                    person.setFirstName(faker.name().firstName());
                    person.setLastName(faker.name().lastName());
                    person.setNationalNumber(faker.number().digits(10)); // Rastgele 10 haneli bir sayı üretiliyor.
                    return person;
                })
                .collect(Collectors.toList()); // Stream, list'e çevriliyor.
    }

    @Override
    public Optional<Person> get(Long id) {
        return dummyData.stream().filter(p -> p.getId().equals(id)).findFirst(); // ID'ye göre ilk eşleşen object
                                                                                 // döndürülüyor.
    }

    @Override
    public Person save(Person entity) {
        entity.setId(Math.abs(UUID.randomUUID().getMostSignificantBits())); // Yeni bir unique ID atanıyor.
        dummyData.add(entity);
        return entity;
    }

    @Override
    public Person update(Long id, Person entity) {
        for (int i = 0; i < dummyData.size(); i++) {
            if (dummyData.get(i).getId().equals(id)) {
                entity.setId(id); // Güncellenen object'in ID'si korunuyor.
                dummyData.set(i, entity); // List'teki ilgili object değiştiriliyor.
                return entity;
            }
        }
        throw new IllegalArgumentException("Person bulunamadı!");
    }

    @Override
    public void delete(Long id) {
        dummyData.removeIf(p -> p.getId().equals(id)); // Belirtilen ID'ye sahip object list'ten kaldırılıyor.
    }

    @Override
    public Page<Person> list(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dummyData.size()); // Pagination için başlangıç ve bitiş
                                                                                // indeksleri belirleniyor.

        List<Person> pagedList = dummyData.subList(start, end); // Sayfaya uygun kısmı alıyoruz.
        return new PageImpl<>(pagedList, pageable, dummyData.size());
    }

    @Override
    public int count() {
        return (int) dummyData.size(); // List uzunluğu döndürülüyor.
    }

    public List<Person> searchByName(String firstName) {
        return dummyData.stream()
                .filter(person -> person.getFirstName().toLowerCase().contains(firstName.toLowerCase())) // Name içinde
                                                                                                         // arama
                                                                                                         // yapılıyor.
                .collect(Collectors.toList()); // Sonuçlar list'e çevriliyor.
    }

    @Override
    public Page<Person> searchByName(String firstName, Pageable pageable) {
        List<Person> filteredList = dummyData.stream()
                .filter(person -> person.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                .collect(Collectors.toList()); // Name'e göre filtreleme yapılıyor.

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredList.size());

        List<Person> pagedList = filteredList.subList(start, end); // Pagination için aralıktaki veriyi seçiyoruz.
        return new PageImpl<>(pagedList, pageable, filteredList.size());
    }

    @Override
    public int countByName(String name) {
        return (int) dummyData.stream()
                .filter(person -> person.getFirstName().toLowerCase().contains(name.toLowerCase()))
                .count(); // Name'e göre filtrelenmiş sonuçların sayısı döndürülüyor.
    }
}