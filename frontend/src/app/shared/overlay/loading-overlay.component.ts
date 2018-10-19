import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-loading-overlay',
  template: '<mat-spinner></mat-spinner>',
  styles: ['']
})
export class LoadingOverlayComponent implements OnInit {

  constructor() {}

  ngOnInit() { }

}
