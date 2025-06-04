import { FormField } from "./form-field.model";

export interface Form {
  id: number;
  title: string;
  description: string;
  fields: FormField[];
}