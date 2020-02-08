import { IStudent } from 'app/shared/model/student.model';

export interface ITutor {
  id?: number;
  name?: string;
  lastName?: string;
  age?: number;
  students?: IStudent[];
}

export class Tutor implements ITutor {
  constructor(public id?: number, public name?: string, public lastName?: string, public age?: number, public students?: IStudent[]) {}
}
