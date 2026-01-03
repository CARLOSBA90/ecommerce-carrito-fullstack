import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface Product {
  id: number;
  name: string;
  price: number;
  description: string;
  image: string;
  stock: number;
}

@Injectable({
  providedIn: 'root',
})
export class ProductsService {
  private mockProducts: Product[] = [
    {
      id: 1,
      name: 'Laptop Pro 15"',
      price: 1299.99,
      description: 'Portátil de alto rendimiento',
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=300&fit=crop',
      stock: 15
    },
    {
      id: 2,
      name: 'Mouse Inalámbrico',
      price: 29.99,
      description: 'Ergonómico y preciso',
      image: 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=300&fit=crop',
      stock: 50
    },
    {
      id: 3,
      name: 'Teclado Mecánico',
      price: 89.99,
      description: 'RGB con switches blue',
      image: 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400&h=300&fit=crop',
      stock: 30
    },
    {
      id: 4,
      name: 'Monitor 27" 4K',
      price: 449.99,
      description: 'Panel IPS con HDR',
      image: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400&h=300&fit=crop',
      stock: 20
    },
    {
      id: 5,
      name: 'Auriculares BT',
      price: 159.99,
      description: 'Cancelación de ruido activa',
      image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop',
      stock: 40
    },
    {
      id: 6,
      name: 'Webcam HD',
      price: 79.99,
      description: '1080p con micrófono',
      image: 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400&h=300&fit=crop',
      stock: 25
    }
  ];

  getProducts(): Observable<Product[]> {
    return of(this.mockProducts);
  }

  getProductById(id: number): Observable<Product | undefined> {
    return of(this.mockProducts.find(p => p.id === id));
  }
}
