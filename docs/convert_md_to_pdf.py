import os
import glob
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors

def convert_md_to_pdf(md_path, pdf_path):
    with open(md_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    doc = SimpleDocTemplate(
        pdf_path,
        pagesize=letter,
        rightMargin=36, leftMargin=36,
        topMargin=36, bottomMargin=36
    )

    styles = getSampleStyleSheet()
    
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Heading1'],
        fontSize=20,
        leading=24,
        textColor=colors.HexColor('#C89B3C'),
        spaceAfter=12
    )

    h1_style = ParagraphStyle(
        'Heading1Custom',
        parent=styles['Heading1'],
        fontSize=15,
        leading=18,
        textColor=colors.HexColor('#111827'),
        spaceBefore=14,
        spaceAfter=8
    )

    h2_style = ParagraphStyle(
        'Heading2Custom',
        parent=styles['Heading2'],
        fontSize=12,
        leading=15,
        textColor=colors.HexColor('#B85C38'),
        spaceBefore=10,
        spaceAfter=6
    )

    body_style = ParagraphStyle(
        'BodyCustom',
        parent=styles['BodyText'],
        fontSize=9.5,
        leading=13,
        textColor=colors.HexColor('#1F2937'),
        spaceAfter=6
    )

    code_style = ParagraphStyle(
        'CodeCustom',
        parent=styles['Code'],
        fontSize=8.5,
        leading=11,
        fontName='Courier',
        textColor=colors.HexColor('#1E293B'),
        backColor=colors.HexColor('#F1F5F9'),
        spaceBefore=4,
        spaceAfter=6
    )

    story = []
    in_code_block = False
    code_lines = []
    in_table = False
    table_lines = []

    def flush_table(lines):
        if not lines:
            return None
        table_data = []
        for line in lines:
            parts = [p.strip() for p in line.strip('| \n').split('|')]
            if parts and not all(c == '-' for c in parts[0]):
                row = [Paragraph(p.replace('<br>', '<br/>'), body_style) for p in parts]
                table_data.append(row)
        if not table_data:
            return None
        t = Table(table_data)
        t.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#F7F4EE')),
            ('TEXTCOLOR', (0,0), (-1,0), colors.HexColor('#111827')),
            ('ALIGN', (0,0), (-1,-1), 'LEFT'),
            ('FONTNAME', (0,0), (-1,0), 'Helvetica-Bold'),
            ('FONTSIZE', (0,0), (-1,-1), 8.5),
            ('BOTTOMPADDING', (0,0), (-1,-1), 4),
            ('TOPPADDING', (0,0), (-1,-1), 4),
            ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E1')),
        ]))
        return t

    for line in lines:
        stripped = line.strip()

        if stripped.startswith('```'):
            if in_code_block:
                code_text = "<br/>".join(code_lines).replace(' ', '&nbsp;')
                story.append(Paragraph(code_text, code_style))
                story.append(Spacer(1, 4))
                code_lines = []
                in_code_block = False
            else:
                if in_table:
                    t = flush_table(table_lines)
                    if t: story.append(t)
                    table_lines = []
                    in_table = False
                in_code_block = True
            continue

        if in_code_block:
            safe_line = stripped.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')
            code_lines.append(safe_line)
            continue

        if stripped.startswith('|') and '|' in stripped[1:]:
            if not in_table:
                in_table = True
            table_lines.append(stripped)
            continue
        elif in_table:
            t = flush_table(table_lines)
            if t: story.append(t)
            table_lines = []
            in_table = False

        if not stripped:
            story.append(Spacer(1, 4))
            continue

        if stripped.startswith('---'):
            story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#E2E8F0'), spaceBefore=8, spaceAfter=8))
            continue

        if stripped.startswith('# '):
            text = stripped[2:].replace('**', '').replace('*', '')
            story.append(Paragraph(text, title_style))
        elif stripped.startswith('## '):
            text = stripped[3:].replace('**', '').replace('*', '')
            story.append(Paragraph(text, h1_style))
        elif stripped.startswith('### '):
            text = stripped[4:].replace('**', '').replace('*', '')
            story.append(Paragraph(text, h2_style))
        elif stripped.startswith('#### '):
            text = stripped[5:].replace('**', '').replace('*', '')
            story.append(Paragraph(text, h2_style))
        else:
            text = stripped.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')
            while '**' in text:
                text = text.replace('**', '<b>', 1).replace('**', '</b>', 1)
            while '`' in text:
                text = text.replace('`', '<font name="Courier" color="#C89B3C">', 1).replace('`', '</font>', 1)
            story.append(Paragraph(text, body_style))

    if in_table:
        t = flush_table(table_lines)
        if t: story.append(t)

    doc.build(story)
    print(f"[OK] Converted: {os.path.basename(md_path)} -> {os.path.basename(pdf_path)}")

if __name__ == '__main__':
    docs_dir = os.path.dirname(os.path.abspath(__file__))
    md_files = glob.glob(os.path.join(docs_dir, "*.md"))
    for md_file in md_files:
        pdf_file = md_file[:-3] + ".pdf"
        try:
            convert_md_to_pdf(md_file, pdf_file)
        except Exception as e:
            print(f"[ERROR] Error converting {os.path.basename(md_file)}: {e}")
