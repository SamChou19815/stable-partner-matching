import { Component, Input, OnInit } from '@angular/core';
import { GlobalDataService } from '../global-data.service';
import { StudentPublicInfo } from '../data';

@Component({
  selector: 'app-student-public-info',
  templateUrl: './student-public-info.component.html',
  styleUrls: ['./student-public-info.component.scss']
})
export class StudentPublicInfoComponent implements OnInit {

  @Input() s: StudentPublicInfo = <StudentPublicInfo>{
    id: '', name: '', email: '', picture: '', skills: '',
    introduction: '', experience: '', pastCourses: [], currCourses: []
  };

  constructor(private dataService: GlobalDataService) { }

  ngOnInit() {
  }

  getCourseName(key: string): string {
    return this.dataService.getCourseNameByKey(key);
  }

}
