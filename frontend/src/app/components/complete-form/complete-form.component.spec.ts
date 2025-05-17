import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompleteFormComponent } from './complete-form.component';

describe('CompleteFormComponent', () => {
  let component: CompleteFormComponent;
  let fixture: ComponentFixture<CompleteFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompleteFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompleteFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
