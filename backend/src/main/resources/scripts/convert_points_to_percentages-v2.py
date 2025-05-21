import re
import sys
import fitz
import json

def get_input_fields_position_and_convert_them_to_percentages(pdf_file_path):
    doc = fitz.open(pdf_file_path)
    input_fields = []

    for page_number, page in enumerate(doc):
        page_text = page.get_text("dict")
        blocks = page_text["blocks"]
        page_width, page_height = page.rect.width, page.rect.height

        for block in blocks:
            for line in block.get("lines", []):
                for span in line.get("spans", []):
                    match = re.search(r"_{2,}", span["text"])  # Matches 2+ underscores
                    if match:
                        start = match.start()
                        end = match.end()

                        total_text_width = span["bbox"][2] - span["bbox"][0]
                        char_width = total_text_width / len(span["text"])

                        x0 = span["bbox"][0] + start * char_width
                        x1 = span["bbox"][0] + end * char_width
                        y0 = span["bbox"][1]
                        y1 = span["bbox"][3]

                        input_field = {
                            "page": page_number + 1,
                            "left": f"{(x0 / page_width) * 100:.2f}%",
                            "top": f"{(y0 / page_height) * 100:.2f}%",
                            "width": f"{((x1 - x0) / page_width) * 100:.2f}%",
                            "height": f"{((y1 - y0) / page_height) * 100:.2f}%",
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