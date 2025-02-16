package org.vaadin.example.services.initialization;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.example.domain.model.Person;
import org.vaadin.example.domain.repository.PersonRepository;

import com.github.javafaker.Faker;

import jakarta.annotation.PostConstruct;

/**
 * PersonDataInitializer sınıfı, uygulamayı başlatırken örnek personel
 * verisi ekleyen bir servistir.
 * Bu sınıf, veritabanına rastgele veri ekler, ancak verilerin yalnızca bir kez
 * eklenmesini sağlar.
 * 
 * Bileşenler:
 * - repository: Person verilerini kaydetmek için kullanılan veritabanı
 * repository'si.
 * - faker: Rasgele veri üretmek için kullanılan Faker nesnesi.
 */
@Service
public class PersonDataInitializer {

    private final PersonRepository repository;
    private final Faker faker;

    /**
     * PersonDataInitializer sınıfının kurucusu.
     * 
     * @param repository Person veritabanı repository'si.
     */
    public PersonDataInitializer(PersonRepository repository) {
        this.repository = repository;
        this.faker = new Faker();
    }

    /**
     * Uygulama başlatıldığında, veritabanında herhangi bir Person verisi
     * yoksa,
     * rasgele veriler oluşturulur ve veritabanına eklenir.
     * 
     * @PostConstruct: Bu metod, Spring konteyneri tarafından nesne başlatıldığında
     *                 otomatik olarak çalıştırılır.
     * @Transactional: Bu metod, veritabanı işlemlerini bir işlem (transaction)
     *                 içinde yürütür.
     */
    @PostConstruct
    @Transactional
    public void initData() {
        if (repository.count() == 0) { // Eğer veritabanında herhangi bir veri yoksa
            List<Person> samplePeople = generateMockData(); // Rasgele veri oluşturur
            repository.saveAll(samplePeople); // Oluşturulan verileri veritabanına kaydeder
        }
    }

    /**
     * Rasgele veriler üreten bir yardımcı metod.
     * Bu metod, 10 adet rastgele personel oluşturur ve her biri için ad, soyad ve
     * ulusal numara (national number) üretir.
     * 
     * @return List<Person> Rasgele oluşturulmuş personel listesi.
     */
    private List<Person> generateMockData() {
        return IntStream.rangeClosed(1, 100)
                .mapToObj(i -> {
                    Person person = new Person();
                    person.setFirstName(faker.name().firstName());
                    person.setLastName(faker.name().lastName());
                    person.setNationalNumber(generateNationalNumber());
                    return person;
                })
                .collect(Collectors.toList());
    }

    /**
     * Rasgele bir ulusal numara (örneğin, 10 haneli bir sayı) üreten bir metod.
     * 
     * @return String Rastgele bir ulusal numara.
     */
    private String generateNationalNumber() {
        return faker.number().digits(10);
    }
}
