import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewRequestsComponent } from './view-requests.component';

describe('ViewRequestComponent', () => {
  let component: ViewRequestsComponent;
  let fixture: ComponentFixture<ViewRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewRequestsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});