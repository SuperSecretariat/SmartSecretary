import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SecretaryNavBarComponent } from './secretary-nav-bar.component';

describe('SecretaryNavBarComponent', () => {
  let component: SecretaryNavBarComponent;
  let fixture: ComponentFixture<SecretaryNavBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SecretaryNavBarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SecretaryNavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
