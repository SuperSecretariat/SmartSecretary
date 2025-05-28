import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifyFormComponent } from './modify-form.component';

describe('ModifyFormComponent', () => {
  let component: ModifyFormComponent;
  let fixture: ComponentFixture<ModifyFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModifyFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifyFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
