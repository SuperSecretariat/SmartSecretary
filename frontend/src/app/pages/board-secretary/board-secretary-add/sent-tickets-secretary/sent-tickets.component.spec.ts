import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SentTicketsComponent } from './sent-tickets.component';

describe('SentTicketsComponent', () => {
  let component: SentTicketsComponent;
  let fixture: ComponentFixture<SentTicketsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SentTicketsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SentTicketsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
