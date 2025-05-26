import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardSecretaryChangeFormsComponent } from './board-secretary-change-forms.component';

describe('BoardSecretaryChangeFormsComponent', () => {
  let component: BoardSecretaryChangeFormsComponent;
  let fixture: ComponentFixture<BoardSecretaryChangeFormsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardSecretaryChangeFormsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardSecretaryChangeFormsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
