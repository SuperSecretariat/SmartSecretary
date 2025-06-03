export interface FormField {
  page: string;
  top: string;
  left: string;
  width: string;
  height: string;
  value: string;
  label: string;
  previousWord: string;
}

export interface Form {
  id: number;
  title: string;
  description: string;
  fields: FormField[];
}