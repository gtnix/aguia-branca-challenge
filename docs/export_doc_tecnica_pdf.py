#!/usr/bin/env python3
"""Gera PDF FIAP da Documentação Técnica — Challenge Águia Branca / Inovagab.

Markdown -> HTML -> Chrome headless --print-to-pdf (portável no Windows).
"""

import os
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

import markdown

DOCS = Path(__file__).resolve().parent
MD_FILE = DOCS / "DOCUMENTACAO_TECNICA_ENTREGA.md"
PDF_FILE = DOCS / "DOCUMENTACAO_TECNICA_ENTREGA.pdf"
VIDEO_URL = "https://youtu.be/9FJmqSRNSkQ"

FIAP_CSS = """
@page {
    size: A4;
    margin: 18mm 16mm 20mm 18mm;
}
@page :first {
    margin: 0;
}

html, body {
    margin: 0;
    padding: 0;
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
}
body {
    font-family: 'Arial', 'Helvetica Neue', Helvetica, sans-serif;
    font-size: 10.5pt;
    line-height: 1.5;
    color: #222;
}

.cover-page {
    page-break-after: always;
    width: 100%;
    height: 297mm;
    position: relative;
    text-align: center;
    box-sizing: border-box;
}
.cover-top-bar {
    background-color: #b5121b;
    height: 90pt;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}
.cover-top-bar .institution {
    font-size: 26pt;
    font-weight: bold;
    color: white;
    letter-spacing: 5pt;
}
.cover-body { padding: 80pt 60pt 40pt 60pt; }
.cover-body .course {
    font-size: 13pt; color: #555;
    margin-bottom: 6pt; text-transform: uppercase; letter-spacing: 1pt;
}
.cover-body .program { font-size: 11pt; color: #777; margin-bottom: 50pt; }
.cover-body .title {
    font-size: 22pt; font-weight: bold; color: #b5121b;
    line-height: 1.3; margin-bottom: 12pt;
}
.cover-body .subtitle { font-size: 13pt; color: #444; margin-bottom: 50pt; }
.cover-divider { width: 60%; height: 2px; background-color: #b5121b; margin: 0 auto 30pt auto; }
.cover-body .author { font-size: 12pt; color: #333; margin-bottom: 6pt; }
.cover-footer { position: absolute; bottom: 50pt; left: 0; right: 0; text-align: center; }
.cover-footer .city-date { font-size: 12pt; color: #555; }
.cover-tagline {
    font-style: italic; font-size: 11pt; color: #666;
    margin-top: 24pt; padding: 0 60pt;
}

h1 {
    font-size: 17pt; font-weight: bold; color: #b5121b;
    text-align: left; margin-top: 0; margin-bottom: 6pt;
    padding-bottom: 8pt; border-bottom: 3px solid #b5121b;
    page-break-after: avoid;
}
h2 {
    font-size: 13pt; font-weight: bold; color: #333;
    margin-top: 18pt; margin-bottom: 6pt;
    padding-bottom: 3pt; border-bottom: 1px solid #ccc;
    page-break-after: avoid; page-break-inside: avoid;
}
h3 {
    font-size: 11.5pt; font-weight: bold; color: #444;
    margin-top: 14pt; margin-bottom: 5pt;
    page-break-after: avoid;
}
h4 {
    font-size: 10.5pt; font-weight: bold; color: #555;
    margin-top: 10pt; margin-bottom: 4pt;
}

p { margin-top: 0; margin-bottom: 7pt; orphans: 3; widows: 3; text-align: justify; }
strong { font-weight: bold; }
em { font-style: italic; }

table {
    width: 100%; border-collapse: collapse;
    margin: 8pt 0 12pt 0; font-size: 9.2pt;
    page-break-inside: auto;
}
th, td { border: 1px solid #bbb; padding: 4pt 7pt; text-align: left; vertical-align: top; }
th { background-color: #b5121b; color: white; font-weight: bold; }
tr:nth-child(even) { background-color: #f7f7f7; }
tr:last-child td { border-bottom: 2px solid #b5121b; }
tr { page-break-inside: avoid; }
thead { display: table-header-group; }

ul, ol { margin-top: 4pt; margin-bottom: 8pt; padding-left: 20pt; }
li { margin-bottom: 3pt; }

blockquote {
    margin: 10pt 0; padding: 8pt 14pt;
    background-color: #fef3f3; border-left: 4px solid #b5121b;
    font-style: italic; color: #444;
}
blockquote p { margin: 0; }

code {
    font-family: 'Courier New', Consolas, monospace; font-size: 8.5pt;
    background-color: #f4f4f4; padding: 1pt 3pt; border-radius: 2pt;
    color: #b5121b;
}
pre {
    background: #1e1e1e; color: #e6e6e6;
    padding: 8pt 10pt; border-radius: 3pt;
    font-family: 'Courier New', Consolas, monospace; font-size: 7.5pt;
    line-height: 1.35;
    page-break-inside: avoid;
    white-space: pre-wrap; word-wrap: break-word;
    margin: 6pt 0;
}
pre code { background: transparent; color: inherit; padding: 0; }

a { color: #b5121b; text-decoration: none; }
hr { border: 0; border-top: 1px solid #ddd; margin: 12pt 0; }

.doc-footer {
    margin-top: 24pt; padding-top: 8pt;
    border-top: 1px solid #ddd;
    font-size: 8pt; color: #666; text-align: center;
}
"""

COVER_PAGE = """
<div class="cover-page">
    <div class="cover-top-bar">
        <div class="institution">FIAP</div>
    </div>
    <div class="cover-body">
        <div class="course">Faculdade de Inform&aacute;tica e Administra&ccedil;&atilde;o Paulista</div>
        <div class="program">An&aacute;lise e Desenvolvimento de Sistemas (On-Line) &mdash; Frameworks Java &mdash; Fase III</div>
        <div class="title">Documenta&ccedil;&atilde;o T&eacute;cnica &mdash; Challenge Grupo &Aacute;guia Branca</div>
        <div class="subtitle">Plataforma de Inova&ccedil;&atilde;o Corporativa Inovagab</div>
        <div class="cover-divider"></div>
        <div class="author"><b>Grupo 82</b></div>
        <div class="author">Andr&eacute; Luiz Oliveira da Silva &mdash; RM 565836</div>
        <div class="author">Giuliana Abe Takara &mdash; RM 562736</div>
        <div class="cover-tagline">
            &ldquo;Conectar estrat&eacute;gia, pessoas, processos e tecnologia<br>
            em um ecossistema de inova&ccedil;&atilde;o corporativa.&rdquo;
        </div>
    </div>
    <div class="cover-footer">
        <div class="city-date">S&atilde;o Paulo &mdash; Maio de 2026</div>
    </div>
</div>
"""

VIDEO_FOOTER = f"""
<div class="doc-footer">
    V&iacute;deo demonstrativo: <a href="{VIDEO_URL}">{VIDEO_URL}</a>
</div>
"""


def find_chrome() -> str:
    candidates = [
        r"C:\Program Files\Google\Chrome\Application\chrome.exe",
        r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
        os.path.expandvars(r"%LOCALAPPDATA%\Google\Chrome\Application\chrome.exe"),
        r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
        r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    ]
    for c in candidates:
        if c and os.path.isfile(c):
            return c
    found = shutil.which("chrome") or shutil.which("msedge")
    if found:
        return found
    raise RuntimeError("Nao foi possivel localizar Chrome ou Edge.")


def strip_cover_metadata(md_text: str) -> str:
    lines = md_text.split("\n")
    skip_until = 0
    for i, line in enumerate(lines):
        if line.startswith("---") and i > 5:
            skip_until = i + 1
            break
    if skip_until == 0:
        for i, line in enumerate(lines):
            if line.startswith("## 1."):
                skip_until = i
                break
    return "\n".join(lines[skip_until:])


def render_md_to_html(md_text: str) -> str:
    body_md = strip_cover_metadata(md_text)
    return markdown.markdown(
        body_md,
        extensions=["tables", "fenced_code", "smarty"],
        output_format="html5",
    )


def render_pdf_with_chrome(html: str, pdf_path: Path) -> None:
    chrome = find_chrome()
    tmpdir = Path(tempfile.mkdtemp(prefix="inovagab_pdf_"))
    try:
        html_file = tmpdir / "input.html"
        html_file.write_text(html, encoding="utf-8")
        user_data = tmpdir / "udd"
        user_data.mkdir(exist_ok=True)
        cmd = [
            chrome,
            "--headless=new",
            "--disable-gpu",
            "--no-sandbox",
            "--no-pdf-header-footer",
            "--run-all-compositor-stages-before-draw",
            "--virtual-time-budget=10000",
            f"--user-data-dir={user_data}",
            f"--print-to-pdf={pdf_path}",
            html_file.as_uri(),
        ]
        print(f"Renderizando com Chrome headless: {chrome}")
        proc = subprocess.run(cmd, capture_output=True, text=True, timeout=120)
        if proc.returncode != 0 or not pdf_path.exists():
            sys.stderr.write(proc.stdout + "\n" + proc.stderr + "\n")
            raise RuntimeError(f"Chrome falhou (returncode={proc.returncode})")
    finally:
        shutil.rmtree(tmpdir, ignore_errors=True)


def main() -> int:
    if not MD_FILE.exists():
        print(f"Arquivo nao encontrado: {MD_FILE}", file=sys.stderr)
        return 1

    md_text = MD_FILE.read_text(encoding="utf-8")
    body_html = render_md_to_html(md_text)
    full_html = f"""<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <title>Documentacao Tecnica - Inovagab</title>
    <style>{FIAP_CSS}</style>
</head>
<body>
{COVER_PAGE}
{body_html}
{VIDEO_FOOTER}
</body>
</html>"""

    render_pdf_with_chrome(full_html, PDF_FILE)
    size_kb = PDF_FILE.stat().st_size / 1024
    print(f"PDF gerado: {PDF_FILE}")
    print(f"Tamanho: {size_kb:.0f} KB")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
