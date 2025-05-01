import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardSecretaryComponent } from './board-secretary.component';

describe('BoardSecretaryComponent', () => {
  let component: BoardSecretaryComponent;
  let fixture: ComponentFixture<BoardSecretaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardSecretaryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardSecretaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
