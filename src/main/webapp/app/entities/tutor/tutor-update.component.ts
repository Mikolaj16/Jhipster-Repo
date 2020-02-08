import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ITutor, Tutor } from 'app/shared/model/tutor.model';
import { TutorService } from './tutor.service';

@Component({
  selector: 'jhi-tutor-update',
  templateUrl: './tutor-update.component.html'
})
export class TutorUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    lastName: [],
    age: []
  });

  constructor(protected tutorService: TutorService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tutor }) => {
      this.updateForm(tutor);
    });
  }

  updateForm(tutor: ITutor): void {
    this.editForm.patchValue({
      id: tutor.id,
      name: tutor.name,
      lastName: tutor.lastName,
      age: tutor.age
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tutor = this.createFromForm();
    if (tutor.id !== undefined) {
      this.subscribeToSaveResponse(this.tutorService.update(tutor));
    } else {
      this.subscribeToSaveResponse(this.tutorService.create(tutor));
    }
  }

  private createFromForm(): ITutor {
    return {
      ...new Tutor(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      age: this.editForm.get(['age'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITutor>>): void {
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
}
