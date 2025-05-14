import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PubbleChatComponent } from './pubble-chat.component';

describe('PubbleChatComponent', () => {
  let component: PubbleChatComponent;
  let fixture: ComponentFixture<PubbleChatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PubbleChatComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PubbleChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
