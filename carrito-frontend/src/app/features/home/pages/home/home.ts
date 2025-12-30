import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Navbar } from '../../../../shared/navbar/navbar';
import { ProductsGallery } from '../../../products/components/products-gallery/products-gallery';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule,
            Navbar,
            ProductsGallery],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {

}
