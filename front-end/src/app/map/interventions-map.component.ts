import { Component, inject, OnInit, AfterViewInit, PLATFORM_ID, ElementRef, ViewChild } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CrmApiService } from '../shared/services/crm-api.service';
import {
  InterventionMapItem,
  STATUS_LABELS,
  TYPE_LABELS
} from '../shared/models/report.model';

declare const L: any;

@Component({
  selector: 'app-interventions-map',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './interventions-map.component.html',
  styleUrl: './interventions-map.component.css'
})
export class InterventionsMapComponent implements OnInit, AfterViewInit {
  private readonly api = inject(CrmApiService);
  private readonly platformId = inject(PLATFORM_ID);

  @ViewChild('mapContainer') mapContainer!: ElementRef<HTMLDivElement>;

  interventions: InterventionMapItem[] = [];
  loading = true;
  error = '';
  statusLabels = STATUS_LABELS;
  typeLabels = TYPE_LABELS;
  selected: InterventionMapItem | null = null;

  private map: any = null;

  ngOnInit(): void {
    this.api.getInterventionsMap().subscribe({
      next: data => {
        this.interventions = data;
        this.loading = false;
        if (isPlatformBrowser(this.platformId)) {
          setTimeout(() => this.initMap(), 0);
        }
      },
      error: () => {
        this.error = 'Impossible de charger la carte des interventions.';
        this.loading = false;
      }
    });
  }

  ngAfterViewInit(): void {
    if (!this.loading && this.interventions.length && isPlatformBrowser(this.platformId)) {
      setTimeout(() => this.initMap(), 0);
    }
  }

  private initMap(): void {
    if (!isPlatformBrowser(this.platformId) || !this.mapContainer || this.map) {
      return;
    }

    const defaultCenter = [45.764, 4.8357];
    this.map = L.map(this.mapContainer.nativeElement).setView(defaultCenter, 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);

    const bounds: number[][] = [];

    for (const item of this.interventions) {
      const latLng = [item.latitude, item.longitude];
      bounds.push(latLng);

      const marker = L.circleMarker(latLng, {
        radius: 9,
        color: '#ff7a59',
        fillColor: '#ff7a59',
        fillOpacity: 0.85,
        weight: 2
      }).addTo(this.map);

      marker.bindPopup(`
        <strong>${item.reference}</strong><br>
        ${item.title}<br>
        ${item.address}, ${item.postalCode} ${item.city}
      `);

      marker.on('click', () => {
        this.selected = item;
      });
    }

    if (bounds.length > 1) {
      this.map.fitBounds(bounds, { padding: [40, 40] });
    } else if (bounds.length === 1) {
      this.map.setView(bounds[0], 14);
    }
  }

  selectItem(item: InterventionMapItem): void {
    this.selected = item;
    if (this.map) {
      this.map.setView([item.latitude, item.longitude], 15);
    }
  }
}
