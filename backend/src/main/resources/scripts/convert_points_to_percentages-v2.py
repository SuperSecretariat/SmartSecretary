import fitz
import sys
import json
import re

def get_input_fields_position_and_convert_them_to_percentages(pdf_file_path):
    doc = fitz.open(pdf_file_path)
    input_fields = []
    for page_number, page in enumerate(doc):
        page_width, page_height = page.rect.width, page.rect.height
        # get all words on the page
        words = page.get_text("words")

        previous_word = None
        # each word is a tuple -> (x0, y0, x1, y1, text, block_no, line_no, word_no)
        for word in words :
            # trimmed_word is the word.text without any non-underscore characters
            # if there are no underscores, it will be an empty string
            trimmed_word = re.sub(r'[^_]', '', word[4])
            if trimmed_word != "":
                # rect is the rectangle that contains the word
                rect_x0 = word[0]
                rect_y0 = word[1]
                rect_width = word[2] - word[0]
                rect_height = word[3] - word[1]
                input_field = {
                    "left": f"{(rect_x0 / page_width) * 100:.2f}%",
                    "top": f"{(rect_y0 / page_height) * 100:.2f}%",
                    "width": f"{(rect_width / page_width) * 100:.2f}%",
                    "height": f"{(rect_height / page_height) * 100:.2f}%",
                    "text": trimmed_word,
                    "previousWord": previous_word,
                    "page": page_number + 1,
                }
                input_fields.append(input_field)
            previous_word = word[4]
    return input_fields


def __main__():
    if len(sys.argv) != 2:
        print("Usage: python script.py <pdf_file_path>")
        sys.exit(1)
    pdf_file_path = sys.argv[1]
    result = get_input_fields_position_and_convert_them_to_percentages(pdf_file_path)
    print(json.dumps(result))

__main__()