import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tutor',
        loadChildren: () => import('./tutor/tutor.module').then(m => m.JApplicationTutorModule)
      },
      {
        path: 'student',
        loadChildren: () => import('./student/student.module').then(m => m.JApplicationStudentModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class JApplicationEntityModule {}
