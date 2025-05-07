import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmittedFormsComponent } from './submitted-forms.component';

describe('SubmittedFormsComponent', () => {
  let component: SubmittedFormsComponent;
  let fixture: ComponentFixture<SubmittedFormsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmittedFormsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubmittedFormsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
