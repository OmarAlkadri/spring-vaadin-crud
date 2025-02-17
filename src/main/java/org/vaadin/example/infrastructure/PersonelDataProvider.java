package org.vaadin.example.infrastructure;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.vaadin.example.domain.dto.PageResponse;
import org.vaadin.example.domain.model.Person;

import com.github.javafaker.Faker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

/**
 * PersonelDataProvider, personel verilerini sağlamak için kullanılan veri
 * sağlayıcıdır.
 */
@Component
public class PersonelDataProvider extends AbstractBackEndDataProvider<Person, Void> {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8081/api/personel";
    private Pageable pageable = PageRequest.of(0, 5);
    private String searchQuery = "";
    private boolean hasNext;

    public PersonelDataProvider() {
        this.restTemplate = new RestTemplate();
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
        refreshAll();
    }

    /**
     * Arama sorgusunu ayarlar ve verileri günceller.
     * 
     * @param searchQuery Aranacak metin
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        refreshAll();
    }

    @Override
    protected Stream<Person> fetchFromBackEnd(Query<Person, Void> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();
        this.pageable = PageRequest.of(offset, limit);

        // API çağrısı için uygun URL oluşturulur
        String url;
        if (searchQuery == null || searchQuery != null && searchQuery.isBlank()) {
            url = String.format("%s/list?page=%d&size=%d", baseUrl, pageable.getPageNumber(),
                    pageable.getPageSize());
        } else {
            url = String.format("%s/search?page=%d&size=%d&name=%s", baseUrl, pageable.getPageNumber(),
                    pageable.getPageSize(), this.searchQuery);
        }

        // REST API çağrısı yapılır ve dönen yanıt işlenir
        ResponseEntity<PageResponse<Person>> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageResponse<Person>>() {
                });

        PageResponse<Person> pageResponse = response.getBody();
        if (pageResponse == null || pageResponse.getContent() == null) {
            return Stream.empty();
        }

        // API'den dönen verileri Page objesine çevirerek Stream olarak döndürür
        Page<Person> result = new PageImpl<>(pageResponse.getContent(), pageable, pageResponse.getTotalElements());
        hasNext = result.hasNext(); // Sonraki sayfa olup olmadığı kontrol edilir
        return result.stream();
    }

    @Override
    protected int sizeInBackEnd(Query<Person, Void> query) {
        // Toplam kayıt sayısını almak için ilgili API çağrısı yapılır
        String queryParam = (searchQuery != null && !searchQuery.isBlank()) ? searchQuery : "";
        String url = queryParam.isEmpty() ? baseUrl + "/count" : baseUrl + "/search/count?name=" + queryParam;

        Integer count = restTemplate.getForObject(url, Integer.class);
        return count != null ? count : 0;
    }

    public int getTotalItemCount() {
        return sizeInBackEnd(null);
    }

    public boolean hasNext() {
        return hasNext;
    }

    public int getTotalPages(int pageSize) {
        int totalItems = sizeInBackEnd(null);
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    /**
     * Yeni bir personel kaydeder.
     * 
     * @param person Kaydedilecek personel
     * @return Kaydedilen personel
     */
    public Person save(Person person) {
        return restTemplate.postForObject(baseUrl, person, Person.class);
    }

    /**
     * Mevcut bir personeli günceller.
     * 
     * @param id     Güncellenecek personelin ID'si
     * @param person Güncellenmiş personel bilgileri
     * @return Güncellenmiş personel
     */
    public Person update(Long id, Person person) {
        String url = baseUrl + "/" + id;
        restTemplate.put(url, person);
        return person;
    }

    /**
     * Belirtilen ID'ye sahip kişiyi siler ve verileri yeniler.
     * 
     * @param personId Silinecek kişinin ID'si
     */
    public void delete(Long personId) {
        restTemplate.delete(baseUrl + "/" + personId);
        refreshAll();
    }

    public void addNewMockPerson() {
        Person entity = new Person();
        Faker faker = new Faker();
        entity.setFirstName(faker.name().firstName());
        entity.setLastName(faker.name().lastName());
        entity.setNationalNumber(faker.number().digits(10));
        save(entity);
        Notification.show("Yeni çalışan başarıyla kaydedildi!", 3000, Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        refreshAll();
    }
}
