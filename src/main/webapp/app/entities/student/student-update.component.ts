import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IStudent, Student } from 'app/shared/model/student.model';
import { StudentService } from './student.service';
import { ITutor } from 'app/shared/model/tutor.model';
import { TutorService } from 'app/entities/tutor/tutor.service';

@Component({
  selector: 'jhi-student-update',
  templateUrl: './student-update.component.html'
})
export class StudentUpdateComponent implements OnInit {
  isSaving = false;
  tutors: ITutor[] = [];

  editForm = this.fb.group({
    id: [],
    model: [],
    lastName: [],
    indexNo: [],
    tutors: []
  });

  constructor(
    protected studentService: StudentService,
    protected tutorService: TutorService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ student }) => {
      this.updateForm(student);

      this.tutorService.query().subscribe((res: HttpResponse<ITutor[]>) => (this.tutors = res.body || []));
    });
  }

  updateForm(student: IStudent): void {
    this.editForm.patchValue({
      id: student.id,
      model: student.model,
      lastName: student.lastName,
      indexNo: student.indexNo,
      tutors: student.tutors
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const student = this.createFromForm();
    if (student.id !== undefined) {
      this.subscribeToSaveResponse(this.studentService.update(student));
    } else {
      this.subscribeToSaveResponse(this.studentService.create(student));
    }
  }

  private createFromForm(): IStudent {
    return {
      ...new Student(),
      id: this.editForm.get(['id'])!.value,
      model: this.editForm.get(['model'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      indexNo: this.editForm.get(['indexNo'])!.value,
      tutors: this.editForm.get(['tutors'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStudent>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: ITutor): any {
    return item.id;
  }

  getSelected(selectedVals: ITutor[], option: ITutor): ITutor {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
