export interface FormField {
  page: string;
  top: string;
  left: string;
  width: string;
  height: string;
  value: string;
}

export interface Form {
  id: number;
  title: string;
  description: string;
  fields: FormField[];
}