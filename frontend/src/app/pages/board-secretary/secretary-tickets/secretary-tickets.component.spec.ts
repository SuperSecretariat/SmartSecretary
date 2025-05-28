import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SecretaryTicketsComponent } from './secretary-tickets.component';

describe('SecretaryTicketsComponent', () => {
  let component: SecretaryTicketsComponent;
  let fixture: ComponentFixture<SecretaryTicketsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SecretaryTicketsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SecretaryTicketsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
