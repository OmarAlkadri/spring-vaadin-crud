# Vaadin Spring Boot MVC & DDD Example

## Proje Açıklaması
Bu proje, **Vaadin** ve **Spring Boot** kullanılarak **MVC** ve **DDD (Domain-Driven Design)** prensiplerine uygun olarak geliştirilmiş bir web uygulamasıdır.

Bu proje, **Monad Yazılım** firmasının teknik değerlendirmesi kapsamında geliştirilmiştir ve **Vaadin Grid** kullanarak personel yönetimi arayüzü sunmayı amaçlamaktadır.

## Proje Özellikleri
- **Vaadin Framework** kullanarak kullanıcı arayüzü geliştirme.
- **MVC** mimarisi ile iş mantığını ve görselliği ayırma.
- **DDD** prensipleri ile kod organizasyonu.
- Veritabanı devre dışıyken sahte veri kaynağı desteği.
- Personel ismine göre arama yapabilme özelliği.
- **PostgreSQL** veritabanı kullanımı (H2 yerine).

## Proje Yapısı
```
.
├── Application.java  # Uygulamanın giriş noktası
├── application
│   ├── controllers
│   │   └── PersonelController.java  # Kullanıcı isteklerini yönetme
│   └── views
│       ├── MainLayout.java  # Uygulamanın ana düzeni
│       ├── about
│       │   └── AboutView.java  # Hakkında sayfası
│       ├── helloworld
│       │   └── HelloWorldView.java  # Hello World sayfası
│       └── personel
│           ├── PersonelEditor.java  # Personel düzenleme bileşeni
│           ├── PersonelGrid.java  # Personel listesini gösteren grid
│           ├── PersonelSearch.java  # Personel arama kutusu
│           └── PersonelView.java  # Personel bölümü ana ekranı
├── domain
│   ├── dto
│   │   └── PageResponse.java  # Sayfa yanıtı nesnesi
│   ├── model
│   │   ├── AbstractEntity.java  # Genel entity sınıfı
│   │   └── Person.java  # Personel veri modeli
│   └── repository
│       └── PersonRepository.java  # Personel veri erişim katmanı
├── infrastructure
│   └── PersonelDataProvider.java  # **Vaadin Grid** için veri sağlayıcı
├── services
│   ├── IPersonService.java  # Personel servisi arayüzü
│   ├── config
│   │   └── PersonServiceConfig.java  # Servis konfigürasyonu
│   ├── implementation
│   │   ├── PersonServiceDummy.java  # Sahte veri kaynağı servisi
│   │   └── PersonServiceSQL.java  # **SQL** tabanlı personel servisi
│   ├── initialization
│   │   └── PersonDataInitializer.java  # Personel veri başlatıcı
└── data  # (Şu an boş, ek veri saklamak için kullanılabilir)
```

## Kullanılan Teknolojiler
- **Java 17**
- **Spring Boot**
- **Vaadin Flow**
- **Maven**
- **PostgreSQL**
- **Docker**

## Projeyi Çalıştırma
### Maven ile Çalıştırma
Aşağıdaki komutları kullanarak PostgreSQL veritabanını etkinleştirebilir veya devre dışı bırakabilirsiniz:
```sh
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dummy" -Ddb.enabled=false  # Sahte veri kaynağı kullanımı
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=sql" -Ddb.enabled=true    # PostgreSQL etkin
```

### Docker Compose ile Çalıştırma
Eğer PostgreSQL veritabanını **Docker Compose** ile çalıştırmak istiyorsanız, aşağıdaki komutları kullanabilirsiniz:
```sh
DB_ENABLED=true SPRING_PROFILE=sql docker-compose up --build   # PostgreSQL etkin
DB_ENABLED=false SPRING_PROFILE=dummy docker-compose up --build # Sahte veri kaynağı kullanımı
```

### VS Code Debugger Kullanımı
VS Code içinde **launch.json** dosyanızı aşağıdaki gibi yapılandırarak projeyi çalıştırabilirsiniz:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Launch Application",
      "request": "launch",
      "mainClass": "org.vaadin.example.Application"
    },
    {
      "type": "java",
      "request": "attach",
      "name": "Attach to Application started using Maven",
      "hostName": "localhost",
      "port": 5005
    },
    {
      "type": "java",
      "name": "Run Application with DB enabled",
      "request": "launch",
      "mainClass": "org.vaadin.example.Application",
      "env": {
        "DB_ENABLED": "true",
        "SPRING_PROFILES_ACTIVE": "sql"
      }
    },
    {
      "type": "java",
      "name": "Run Application with DB disabled",
      "request": "launch",
      "mainClass": "org.vaadin.example.Application",
      "env": {
        "DB_ENABLED": "false",
        "SPRING_PROFILES_ACTIVE": "dummy"
      }
    }
  ]
}
```

### PostgreSQL Kullanımı
PostgreSQL veritabanını **Docker** ile çalıştırmak için aşağıdaki komutları kullanabilirsiniz:
```sh
# PostgreSQL konteynerini başlatma
docker run --name postgres-db -e POSTGRES_USER=vaadinstart -e POSTGRES_PASSWORD=vaadinstart -e POSTGRES_DB=test -p 5433:5432 -d postgres:13

# Çalışan PostgreSQL konteynerini durdurma
docker stop postgres-db

# Konteyneri tekrar başlatma
docker start postgres-db
```
Alternatif olarak, PostgreSQL’i sisteminize doğrudan kurarak kullanabilirsiniz.

## Katkıda Bulunma
Projeye katkı sağlamak istiyorsanız:
- **Pull Request** gönderebilirsiniz.
- **Issues** üzerinden geliştirme önerilerinde bulunabilirsiniz.

## Ek Bilgiler
Bu proje, **Monad Yazılım** teknik değerlendirmesi kapsamında geliştirilmiştir. Kaynak kodlar **ZIP** dosyası olarak hazırlanıp belirtilen e-posta adresine gönderilmelidir.

