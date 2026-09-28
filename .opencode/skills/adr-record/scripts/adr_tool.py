#!/usr/bin/env python3
"""Small helper for managing Architecture Decision Records (stdlib only).

Commands:
  new "Title" [--dir docs/adr] [--format madr|minimal|nygard|y-statement] [--status proposed]
  set-status NUM STATUS [--dir docs/adr]
  supersede OLD_NUM NEW_NUM [--dir docs/adr]
  index [--dir docs/adr]
"""
import argparse
import datetime
import re
import sys
from pathlib import Path

ASSETS = Path(__file__).resolve().parent.parent / "assets"
FORMATS = {
    "madr": "madr.md",
    "minimal": "madr-minimal.md",
    "nygard": "nygard.md",
    "y-statement": "y-statement.md",
}
FILE_RE = re.compile(r"^(\d{4})-(.+)\.md$")
STATUS_RE = re.compile(r'^status:\s*"?(.*?)"?\s*$', re.M)
DATE_RE = re.compile(r"^date:\s*(.+?)\s*$", re.M)


def slugify(title: str) -> str:
    slug = re.sub(r"[^a-z0-9]+", "-", title.lower()).strip("-")
    return slug[:60].rstrip("-") or "untitled"


def adr_files(directory: Path):
    found = []
    for f in sorted(directory.glob("*.md")):
        m = FILE_RE.match(f.name)
        if m:
            found.append((int(m.group(1)), f))
    return found


def find_adr(directory: Path, num: int) -> Path:
    for n, f in adr_files(directory):
        if n == num:
            return f
    sys.exit(f"error: ADR {num:04d} not found in {directory}")


def title_of(text: str, num: int) -> str:
    for line in text.splitlines():
        if line.startswith("# "):
            return re.sub(r"^#\s*(ADR-?\d+\s*[:\-]\s*)?", "", line).strip()
    return f"ADR {num:04d}"


def cmd_new(a):
    d = Path(a.dir)
    d.mkdir(parents=True, exist_ok=True)
    nums = [n for n, _ in adr_files(d)]
    number = (max(nums) + 1) if nums else 1
    template = (ASSETS / FORMATS[a.format]).read_text(encoding="utf-8")
    text = (
        template.replace("{{NUMBER}}", f"{number:04d}")
        .replace("{{TITLE}}", a.title)
        .replace("{{STATUS}}", a.status)
        .replace("{{DATE}}", a.date or datetime.date.today().isoformat())
    )
    path = d / f"{number:04d}-{slugify(a.title)}.md"
    path.write_text(text, encoding="utf-8")
    print(path)


def set_status_in_file(path: Path, status: str):
    text = path.read_text(encoding="utf-8")
    if STATUS_RE.search(text):
        text = STATUS_RE.sub(f'status: "{status}"', text, count=1)
    else:
        text = f'---\nstatus: "{status}"\n---\n\n' + text
    path.write_text(text, encoding="utf-8")


def cmd_set_status(a):
    path = find_adr(Path(a.dir), a.num)
    set_status_in_file(path, a.status)
    print(f"{path}: status -> {a.status}")
    build_index(Path(a.dir))


def append_line(path: Path, line: str):
    text = path.read_text(encoding="utf-8").rstrip("\n")
    path.write_text(text + "\n\n" + line + "\n", encoding="utf-8")


def cmd_supersede(a):
    d = Path(a.dir)
    old, new = find_adr(d, a.old), find_adr(d, a.new)
    if a.old == a.new:
        sys.exit("error: an ADR cannot supersede itself")
    set_status_in_file(old, f"superseded by ADR-{a.new:04d}")
    append_line(old, f"Superseded by [ADR-{a.new:04d}]({new.name}).")
    append_line(new, f"Supersedes [ADR-{a.old:04d}]({old.name}).")
    print(f"ADR-{a.old:04d} superseded by ADR-{a.new:04d}")
    build_index(d)


def build_index(d: Path):
    rows = []
    for n, f in adr_files(d):
        text = f.read_text(encoding="utf-8")
        st = STATUS_RE.search(text)
        dt = DATE_RE.search(text)
        rows.append(
            f"| [{n:04d}]({f.name}) | {title_of(text, n)} | "
            f"{st.group(1) if st else '-'} | {dt.group(1) if dt else '-'} |"
        )
    body = (
        "# Architecture Decision Records\n\n"
        "One decision per record. Accepted records are immutable: to change a "
        "decision, write a new ADR that supersedes the old one.\n\n"
        "| ADR | Title | Status | Date |\n|---|---|---|---|\n"
        + "\n".join(rows)
        + "\n"
    )
    (d / "README.md").write_text(body, encoding="utf-8")
    return len(rows)


def cmd_index(a):
    d = Path(a.dir)
    if not d.is_dir():
        sys.exit(f"error: directory {d} does not exist")
    n = build_index(d)
    print(f"{d / 'README.md'}: {n} ADR(s) indexed")


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = p.add_subparsers(dest="cmd", required=True)

    n = sub.add_parser("new", help="create the next numbered ADR from a template")
    n.add_argument("title")
    n.add_argument("--dir", default="docs/adr")
    n.add_argument("--format", choices=FORMATS, default="madr")
    n.add_argument("--status", default="proposed")
    n.add_argument("--date", help="ISO date; default today")
    n.set_defaults(fn=cmd_new)

    s = sub.add_parser("set-status", help="change the status of an ADR")
    s.add_argument("num", type=int)
    s.add_argument("status")
    s.add_argument("--dir", default="docs/adr")
    s.set_defaults(fn=cmd_set_status)

    u = sub.add_parser("supersede", help="mark OLD as superseded by NEW")
    u.add_argument("old", type=int)
    u.add_argument("new", type=int)
    u.add_argument("--dir", default="docs/adr")
    u.set_defaults(fn=cmd_supersede)

    i = sub.add_parser("index", help="rebuild README.md index")
    i.add_argument("--dir", default="docs/adr")
    i.set_defaults(fn=cmd_index)

    a = p.parse_args()
    a.fn(a)


if __name__ == "__main__":
    main()
