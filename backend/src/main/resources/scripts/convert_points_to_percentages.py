import fitz
import sys
import json

def get_input_fields_position_and_convert_them_to_percentages(pdf_file_path):
    doc = fitz.open(pdf_file_path)
    input_fields = []
    for page_number, page in enumerate(doc):
        text_instances = page.search_for("_")
        page_width, page_height = page.rect.width, page.rect.height
        for rect in text_instances:
            input_field = {
                "page": page_number + 1,
                "left": f"{(rect.x0 / page_width) * 100:.2f}%",
                "top": f"{(rect.y0 / page_height) * 100:.2f}%",
                "width": f"{(rect.width / page_width) * 100:.2f}%",
                "height": f"{(rect.height / page_height) * 100:.2f}%",
            }
            input_fields.append(input_field)
    return input_fields


def __main__():
    if len(sys.argv) != 2:
        print("Usage: python script.py <pdf_file_path>")
        sys.exit(1)
    pdf_file_path = sys.argv[1]
    result = get_input_fields_position_and_convert_them_to_percentages(pdf_file_path)
    print(json.dumps(result))

__main__()