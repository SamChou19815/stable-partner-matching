import { Component, HostBinding, Input, OnInit } from "@angular/core";

@Component({
  selector: "app-blocking-overlay",
  template: "",
  styles: [`:host {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    z-index: 1;
    background-color: rgba(255, 255, 255, 0.8);
  }`]
})
export class BlockingOverlayComponent implements OnInit {
  get width(): string {
    return this._width;
  }

  set width(value: string) {
    this._width = value;
  }

  @HostBinding('style.width') private _width: string = '100%';

  constructor() {
  }

  ngOnInit() {
  }

  @Input() set active(active: boolean) {
    this._width = active ? "100%" : "0";
  }

}
