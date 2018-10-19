import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {

  /**
   * Alert content from user.
   */
  readonly content: string;

  /**
   * Construct an alert content by the injected data.
   *
   * @param data the injected data.
   */
  constructor(@Inject(MAT_DIALOG_DATA) data: any) {
    this.content = data;
  }

  ngOnInit() {}

}
