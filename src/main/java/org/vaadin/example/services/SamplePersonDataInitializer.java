package org.vaadin.example.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.example.data.SamplePerson;
import org.vaadin.example.data.SamplePersonRepository;

import com.github.javafaker.Faker;

import jakarta.annotation.PostConstruct;

/**
 * SamplePersonDataInitializer sınıfı, uygulamayı başlatırken örnek personel
 * verisi ekleyen bir servistir.
 * Bu sınıf, veritabanına rastgele veri ekler, ancak verilerin yalnızca bir kez
 * eklenmesini sağlar.
 * 
 * Bileşenler:
 * - repository: SamplePerson verilerini kaydetmek için kullanılan veritabanı
 * repository'si.
 * - faker: Rasgele veri üretmek için kullanılan Faker nesnesi.
 */
@Service
public class SamplePersonDataInitializer {

    private final SamplePersonRepository repository; // Veritabanına veri eklemek için kullanılan repository
    private final Faker faker; // Rasgele veriler oluşturmak için kullanılan Faker nesnesi

    /**
     * SamplePersonDataInitializer sınıfının kurucusu.
     * 
     * @param repository SamplePerson veritabanı repository'si.
     */
    public SamplePersonDataInitializer(SamplePersonRepository repository) {
        this.repository = repository;
        this.faker = new Faker(); // Faker nesnesini başlatır
    }

    /**
     * Uygulama başlatıldığında, veritabanında herhangi bir SamplePerson verisi
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
            List<SamplePerson> samplePeople = generateMockData(); // Rasgele veri oluşturur
            repository.saveAll(samplePeople); // Oluşturulan verileri veritabanına kaydeder
        }
    }

    /**
     * Rasgele veriler üreten bir yardımcı metod.
     * Bu metod, 10 adet rastgele personel oluşturur ve her biri için ad, soyad ve
     * ulusal numara (national number) üretir.
     * 
     * @return List<SamplePerson> Rasgele oluşturulmuş personel listesi.
     */
    private List<SamplePerson> generateMockData() {
        return IntStream.rangeClosed(1, 10) // 1'den 10'a kadar olan sayılarla döngü başlatılır
                .mapToObj(i -> {
                    SamplePerson person = new SamplePerson();
                    person.setFirstName(faker.name().firstName()); // Rastgele bir ilk isim oluşturur
                    person.setLastName(faker.name().lastName()); // Rastgele bir soyisim oluşturur
                    person.setNationalNumber(generateNationalNumber()); // Rastgele bir ulusal numara oluşturur
                    return person;
                })
                .collect(Collectors.toList()); // Listeye dönüştürür
    }

    /**
     * Rasgele bir ulusal numara (örneğin, 10 haneli bir sayı) üreten bir metod.
     * 
     * @return String Rastgele bir ulusal numara.
     */
    private String generateNationalNumber() {
        return faker.number().digits(10); // 10 haneli bir sayı üretir
    }
}
