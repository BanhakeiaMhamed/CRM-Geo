import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-geo-logo',
  standalone: true,
  template: `
    <svg
      class="geo-logo"
      [class.geo-logo--lg]="size === 'lg'"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <!-- Sol / couches geologiques -->
      <path d="M2 24h28v6H2z" fill="currentColor" opacity="0.18"/>
      <path d="M2 24h28" stroke="currentColor" stroke-width="1.2" opacity="0.45"/>
      <path d="M2 27.5h28" stroke="currentColor" stroke-width="0.8" opacity="0.28"/>
      <path d="M2 21h28" stroke="currentColor" stroke-width="0.8" opacity="0.22"/>

      <!-- Mecanisme foreuse (tete) -->
      <rect x="11" y="2" width="10" height="4" rx="1.2" fill="currentColor" opacity="0.92"/>
      <rect x="13.5" y="5.5" width="5" height="2" rx="0.5" fill="currentColor" opacity="0.55"/>

      <!-- Tige de sondage -->
      <rect x="14.5" y="7" width="3" height="14" rx="0.8" fill="#FF7A59"/>
      <line x1="16" y1="9" x2="16" y2="20" stroke="#fff" stroke-width="0.6" opacity="0.35"/>

      <!-- Mèches / segments -->
      <rect x="14" y="10" width="4" height="1.2" rx="0.3" fill="#E66A4A"/>
      <rect x="14" y="13" width="4" height="1.2" rx="0.3" fill="#E66A4A"/>
      <rect x="14" y="16" width="4" height="1.2" rx="0.3" fill="#E66A4A"/>

      <!-- Cone de penetration (sondage) -->
      <path d="M12.5 21 L16 27.5 L19.5 21 Z" fill="#00A4BD"/>
      <path d="M14.5 21 L16 24.5 L17.5 21 Z" fill="#fff" opacity="0.25"/>
    </svg>
  `,
  styles: [`
    :host {
      display: inline-flex;
      align-items: center;
      flex-shrink: 0;
    }

    .geo-logo {
      width: 28px;
      height: 28px;
      color: #fff;
    }

    .geo-logo--lg {
      width: 40px;
      height: 40px;
    }
  `]
})
export class GeoLogoComponent {
  @Input() size: 'sm' | 'lg' = 'sm';
}
