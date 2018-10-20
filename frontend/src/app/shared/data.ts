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

export interface FreeTimeInterval {
  start: number;
  end: number;
}

export interface StudentFreeTimeRecord {
  readonly key: string;
  readonly studentId: string;
  record: FreeTimeInterval[];
}

export interface StudentPublicInfo {
  readonly id: string;
  readonly name: string;
  readonly email: string;
  readonly picture: string;
  readonly skills: string;
  readonly introduction: string;
  readonly experience: string;
  readonly freeTimes: StudentFreeTimeRecord;
  readonly pastCourses: string[]; // an array of course IDs.
  readonly currCourses: string[]; // an array of course IDs.
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
  freeTimes: StudentFreeTimeRecord;
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
};

export const allTimeIntervals: number[] = (() => {
  const arr = [];
  for (let i = 0; i < 7 * 24 * 4; i++) {
    arr.push(i);
  }
  return arr;
})();

const timeIntervalIdToString: Map<number, string> = (() => {
  const map = new Map<number, string>();
  for (const i of allTimeIntervals) {
    const weekdayInt = Math.floor(i / (24 * 4));
    let weekday: string;
    switch (weekdayInt) {
      case 0:
        weekday = 'SUN';
        break;
      case 1:
        weekday = 'MON';
        break;
      case 2:
        weekday = 'TUE';
        break;
      case 3:
        weekday = 'WED';
        break;
      case 4:
        weekday = 'THU';
        break;
      case 5:
        weekday = 'FRI';
        break;
      case 6:
        weekday = 'SAT';
        break;
      default:
        throw new Error();
    }
    const hourInt = Math.floor(i / 4) % 24;
    let hour = `${hourInt}`;
    if (hour.length === 1) {
      hour = '0' + hour;
    }
    const quarterInt = i % 4;
    let quarter: string;
    switch (quarterInt) {
      case 0:
        quarter = '00';
        break;
      case 1:
        quarter = '15';
        break;
      case 2:
        quarter = '30';
        break;
      case 3:
        quarter = '45';
        break;
      default:
        throw new Error(String(quarterInt));
    }
    const str = `${weekday} ${hour}:${quarter}`;
    map.set(i, str);
  }
  return map;
})();

export function timeIntervalIdToStr(id: number): string {
  const ans = timeIntervalIdToString.get(id);
  if (ans == null) {
    throw new Error();
  }
  return ans;
}
