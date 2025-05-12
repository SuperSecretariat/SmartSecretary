import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { CnpValidatorService } from '../_services/cnp-validator.service';

@Directive({
  selector: '[appCnpValidator]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: CnpValidatorDirective,
      multi: true
    }
  ],
  standalone: false
})
export class CnpValidatorDirective implements Validator {
  
  constructor(private readonly cnpValidator: CnpValidatorService) {}
  
  validate(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null; // Let the required validator handle empty values
    }
    
    const result = this.cnpValidator.validateCnp(control.value);
    
    if (!result.isValid) {
      return { invalidCnp: result.errorMessage };
    }
    
    return null;
  }
}