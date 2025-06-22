import fitz
import sys
import json
import re

def get_input_fields_position_and_convert_them_to_percentages(pdf_file_path):
    doc = fitz.open(pdf_file_path)
    input_fields = []
    for page_number, page in enumerate(doc):
        page_width, page_height = page.rect.width, page.rect.height
        # Get all words on the page
        words = page.get_text("words")

        previous_word = None
        # Each word is a tuple -> (x0, y0, x1, y1, text, block_no, line_no, word_no)
        for word in words :
            width_offset = 0
            # some words that are input fields have a trailing character that is not an underscore
            if word[4][-1] != "_":
                trimmed_word = word[4][0:-1]
                # the size of a character is almost 0.45 points, so we need to subtract it from the
                # width of the rectangle, so we can get the correct width of the input field
                width_offset = -0.45
            else:
                trimmed_word = word[4]

            # The first if matches the ___... pattern even if the spacing is not good, like: an___,
            if re.search(r"_{2,}", trimmed_word):

            # The second if matches the ___... pattern only if the spacing is perfect, like: an ___,
            #if re.fullmatch(r"_+", trimmed_word):
                # rect is the rectangle that contains the word
                rect_x0 = word[0]
                rect_y0 = word[1]
                rect_width = word[2] - word[0]
                rect_height = word[3] - word[1]
                input_field = {
                    "page": page_number + 1,
                    "top": f"{(rect_y0 / page_height) * 100:.2f}%",
                    "left": f"{(rect_x0 / page_width) * 100:.2f}%",
                    "width": f"{((rect_width / page_width) * 100 + width_offset):.2f}%",
                    "height": f"{(rect_height / page_height) * 100:.2f}%",
                    "text": trimmed_word,
                    "previousWord": previous_word,
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

    output_file_path = "src/main/resources/scripts/script-output.json"

    with open(output_file_path, "w", encoding="utf-8") as f:
        json.dump(result, f, ensure_ascii=False, indent=2)

__main__()