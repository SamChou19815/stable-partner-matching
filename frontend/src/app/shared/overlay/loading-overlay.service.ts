import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Injectable } from '@angular/core';
import { LoadingOverlayComponent } from './loading-overlay.component';

@Injectable({
  providedIn: 'root'
})
export class LoadingOverlayService {

  /**
   * The overlay config for the loading service.
   */
  private readonly overlayConfig: OverlayConfig;

  /**
   * Create the service via the injected overlay.
   *
   * @param {Overlay} overlay the injected overlay.
   */
  constructor(private overlay: Overlay) {
    const positionStrategy = overlay.position()
      .global().centerHorizontally().centerVertically();
    this.overlayConfig = new OverlayConfig({
      hasBackdrop: true,
      backdropClass: 'cdk-overlay-light-backdrop',
      panelClass: '',
      scrollStrategy: overlay.scrollStrategies.block(),
      positionStrategy
    });
  }

  /**
   * Open the global loading overlay.
   *
   * @returns {LoadingOverlayRef} a handle to close the overlay.
   */
  open(): LoadingOverlayRef {
    const overlayRef = this.overlay.create(this.overlayConfig);
    const loadingPortal = new ComponentPortal(LoadingOverlayComponent);
    overlayRef.attach(loadingPortal);
    return new LoadingOverlayRef(overlayRef);
  }

}

/**
 * Acts as a handle for the global loading overlay.
 * It facades the complicated overlay ref from angular. Only necessary methods are provided.
 */
export class LoadingOverlayRef {

  /**
   * Wrap the overlay ref from angular.
   *
   * @param {OverlayRef} overlayRef the overlay ref from angular.
   */
  constructor(private overlayRef: OverlayRef) {
  }

  /**
   * Close the global loading overlay.
   */
  close: () => void = () => this.overlayRef.dispose();

}
