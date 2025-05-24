import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardSecretaryAddComponent } from './board-secretary-add.component';

describe('BoardSecretaryAddComponent', () => {
  let component: BoardSecretaryAddComponent;
  let fixture: ComponentFixture<BoardSecretaryAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardSecretaryAddComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardSecretaryAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
