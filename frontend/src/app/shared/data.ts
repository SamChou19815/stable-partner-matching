export interface SimplifiedCourseInfo {
  readonly key: string;
  readonly subject: string;
  readonly code: string;
  readonly title: string;
}

export type StudentClass = 'FRESHMAN' | 'SOPHOMORE' | 'JUNIOR' | 'SENIOR';
export type TimeStatus = 'PAST' | 'CURRENT' | 'FUTURE';

export interface StudentProfile {
  readonly key: string;
  readonly name: string;
  readonly email: string;
  readonly picture: string;
  studentClass: StudentClass;
  graduationYear: number;
  skills: string;
  introduction: string;
  experience: string;
}

export interface StudentPublicInfo {
  readonly id: string;
  readonly name: string;
  readonly email: string;
  readonly picture: string;
  readonly skills: string;
  readonly introduction: string;
  readonly experience: string;
  readonly pastCourses: string[]; // an array of course IDs.
  readonly grade?: number; // an optional grade for debugging
}

export interface StudentCourse {
  readonly key?: string;
  readonly studentId: string;
  readonly courseId: string;
  readonly score?: number; // Must be an int
  status: TimeStatus;
  isTa?: boolean;
}

export interface StudentPartnership {
  readonly key: string;
  readonly student1Id: string;
  readonly student2Id: string;
  readonly student2Info: StudentPublicInfo;
  readonly courseId: string;
  readonly timeStatus: TimeStatus;
}

export interface PartnerInvitation {
  readonly key: string;
  readonly inviterId: string;
  readonly inviterInfo: StudentPublicInfo;
  readonly invitedId: string;
  readonly courseId: string;
  readonly timeStatus: TimeStatus;
}

export interface InitData {
  readonly isNotInitialized?: boolean;
  readonly allCourses: SimplifiedCourseInfo[];
  profile: StudentProfile;
  courses: StudentCourse[];
  partners: StudentPartnership[];
  partnerInvitations: PartnerInvitation[];
}

export const dummyInitData: InitData = <InitData>{
    isNotInitialized: true,
    allCourses: [],
    profile: <StudentProfile>{
      key: '', name: '', email: '', picture: '',
      studentClass: 'FRESHMAN', graduationYear: 2022,
      skills: 'break things', introduction: '', experience: ''
    },
    courses: [],
    partners: [],
    partnerInvitations: []
  }
;
