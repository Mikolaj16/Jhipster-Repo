import { ITutor } from 'app/shared/model/tutor.model';

export interface IStudent {
  id?: number;
  model?: string;
  lastName?: string;
  indexNo?: string;
  tutors?: ITutor[];
}

export class Student implements IStudent {
  constructor(public id?: number, public model?: string, public lastName?: string, public indexNo?: string, public tutors?: ITutor[]) {}
}
