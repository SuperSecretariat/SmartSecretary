import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CnpValidatorDirective } from './cnp-validator.directive';

@NgModule({
  declarations: [
    CnpValidatorDirective
  ],
  imports: [
    CommonModule
  ],
  exports: [
    CnpValidatorDirective
  ]
})
export class ValidatorsModule { }