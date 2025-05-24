import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CnpValidatorService {
  
  validateCnp(cnp: string): { isValid: boolean; errorMessage?: string } {
    // Check if CNP is exactly 13 digits
    if (!cnp || !/^\d{13}$/.test(cnp)) {
      return { isValid: false, errorMessage: 'Invalid CNP' };
    }

    // Extract components
    const genderDigit = parseInt(cnp.charAt(0), 10);
    const yearDigits = cnp.substring(1, 3);
    const monthDigit = parseInt(cnp.substring(3, 5), 10);
    const dayDigit = parseInt(cnp.substring(5, 7), 10);
    const countyCode = parseInt(cnp.substring(7, 9), 10);
    
    // Validate gender digit (1-9)
    if (genderDigit < 1 || genderDigit > 9) {
      return { isValid: false, errorMessage: 'Invalid CNP' };
    }
    
    // Determine century based on gender digit
    let century: number;
    if ([1, 2].includes(genderDigit)) century = 19;
    else if ([3, 4].includes(genderDigit)) century = 19;
    else if ([5, 6].includes(genderDigit)) century = 20;
    else if ([7, 8].includes(genderDigit)) century = 20;
    else century = 18; // For digit 9 (foreign residents)
    
    const fullYear = century + parseInt(yearDigits, 10);
    
    // Validate month (1-12)
    if (monthDigit < 1 || monthDigit > 12) {
      return { isValid: false, errorMessage: 'Invalid CNP' };
    }
    
    // Validate day based on month and leap year
    const daysInMonth = this.getDaysInMonth(fullYear, monthDigit);
    if (dayDigit < 1 || dayDigit > daysInMonth) {
      return { isValid: false, errorMessage: 'Invalid CNP' };
    }

    // Validate county code (1-52, except 49-50)
    if (countyCode < 1 || countyCode > 52 || countyCode === 49 || countyCode === 50) {
      return { isValid: false, errorMessage: 'Invalid CNP' };
    }

    // Calculate and validate control digit
    const controlDigit = parseInt(cnp.charAt(12), 10);
    const constantNumber = '279146358279';
    
    let sum = 0;
    for (let i = 0; i < 12; i++) {
      sum += parseInt(cnp.charAt(i), 10) * parseInt(constantNumber.charAt(i), 10);
    }
    
    const calculatedControlDigit = sum % 11;
    const expectedControlDigit = calculatedControlDigit === 10 ? 1 : calculatedControlDigit;
    
    if (controlDigit !== expectedControlDigit) {
      return { isValid: false, errorMessage: 'Invalid CNP' };
    }

    // Check if birth date is in the future
    const birthDate = new Date(fullYear, monthDigit - 1, dayDigit);
    if (birthDate > new Date()) {
      return { isValid: false, errorMessage: 'Invalid CNP' };
    }

    // If all checks pass
    return { isValid: true };
  }


   // Calculate days in month accounting for leap years   
  private getDaysInMonth(year: number, month: number): number {
    const daysPerMonth = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    
    // Adjust February for leap years
    if (month === 2 && this.isLeapYear(year)) {
      return 29;
    }
    
    return daysPerMonth[month];
  }

   // Check if year is a leap year
  private isLeapYear(year: number): boolean {
    return (year % 4 === 0 && year % 100 !== 0) || (year % 400 === 0);
  }
}