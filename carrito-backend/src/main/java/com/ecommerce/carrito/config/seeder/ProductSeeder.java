package com.ecommerce.carrito.config.seeder;

import com.ecommerce.carrito.model.Product;
import com.ecommerce.carrito.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements EntitySeeder {

        private final ProductRepository productRepository;

        @Override
        public void seed() {
                log.info("Seeding products...");

                List<Product> products = new ArrayList<>();

                products.add(Product.builder()
                                .name("Laptop Pro 15\"")
                                .description("Portátil de alto rendimiento")
                                .price(new BigDecimal("1299.99"))
                                .imageLink("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=300&fit=crop")
                                .stock(15)
                                .category("Laptops")
                                .build());

                products.add(Product.builder()
                                .name("Mouse Inalámbrico")
                                .description("Ergonómico y preciso")
                                .price(new BigDecimal("29.99"))
                                .imageLink("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=300&fit=crop")
                                .stock(50)
                                .category("Accesorios")
                                .build());

                products.add(Product.builder()
                                .name("Teclado Mecánico")
                                .description("RGB con switches blue")
                                .price(new BigDecimal("89.99"))
                                .imageLink("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400&h=300&fit=crop")
                                .stock(30)
                                .category("Accesorios")
                                .build());

                products.add(Product.builder()
                                .name("Monitor 27\" 4K")
                                .description("Panel IPS con HDR")
                                .price(new BigDecimal("449.99"))
                                .imageLink("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400&h=300&fit=crop")
                                .stock(20)
                                .category("Monitores")
                                .build());

                products.add(Product.builder()
                                .name("Auriculares BT")
                                .description("Cancelación de ruido activa")
                                .price(new BigDecimal("159.99"))
                                .imageLink("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop")
                                .stock(40)
                                .category("Audio")
                                .build());

                products.add(Product.builder()
                                .name("Webcam HD")
                                .description("1080p con micrófono")
                                .price(new BigDecimal("79.99"))
                                .imageLink("https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400&h=300&fit=crop")
                                .stock(25)
                                .category("Accesorios")
                                .build());

                products.add(Product.builder()
                                .name("SSD 1TB NVMe")
                                .description("Velocidad de lectura 3500MB/s")
                                .price(new BigDecimal("129.99"))
                                .imageLink("https://images.unsplash.com/photo-1531492746076-161ca9bcad58?w=400&h=300&fit=crop")
                                .stock(35)
                                .category("Almacenamiento")
                                .build());

                products.add(Product.builder()
                                .name("Router WiFi 6")
                                .description("Cobertura hasta 200m²")
                                .price(new BigDecimal("199.99"))
                                .imageLink("https://images.unsplash.com/photo-1606904825846-647eb07f5820?w=400&h=300&fit=crop")
                                .stock(18)
                                .category("Redes")
                                .build());

                products.add(Product.builder()
                                .name("Tablet 10\" 128GB")
                                .description("Android 13, pantalla OLED")
                                .price(new BigDecimal("349.99"))
                                .imageLink("https://images.unsplash.com/photo-1561154464-82e9adf32764?w=400&h=300&fit=crop")
                                .stock(22)
                                .category("Tablets")
                                .build());

                products.add(Product.builder()
                                .name("Hub USB-C 7 Puertos")
                                .description("HDMI 4K, USB 3.0, lector SD")
                                .price(new BigDecimal("59.99"))
                                .imageLink("https://images.unsplash.com/photo-1625948515291-69613efd103f?w=400&h=300&fit=crop")
                                .stock(45)
                                .category("Accesorios")
                                .build());

                productRepository.saveAll(products);
                log.info("Seeded {} products", productRepository.count());
        }

        @Override
        public boolean shouldSeed() {
                return productRepository.count() == 0;
        }
}
